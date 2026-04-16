package com.example.comentoStatistic.client;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.example.comentoStatistic.dto.HolidayItemDto;
import com.example.comentoStatistic.dto.HolidayResponseDto;

/**
 * 한국천문연구원 공공 공휴일 API를 호출하는 클라이언트
 * - API: data.go.kr 특일 정보 (getRestDeInfo)
 * - 월 단위로 공휴일 목록을 조회
 * - API 장애 시에도 빈 리스트를 반환하여 서비스 전체에 영향 없도록 처리
 */
@Component
public class HolidayClient {

    private static final Logger log = LoggerFactory.getLogger(HolidayClient.class);

    private final RestClient holidayRestClient;  // RestClientConfig에서 Bean으로 주입 (baseUrl, timeout 설정 포함)
    private final String apiKey;                 // 환경변수 HOLIDAY_API_KEY에서 주입

    public HolidayClient(RestClient holidayRestClient, @Value("${HOLIDAY_API_KEY}") String apiKey) {
        this.holidayRestClient = holidayRestClient;
        this.apiKey = apiKey;
    }

    /**
     * 특정 연/월의 공휴일 목록을 조회한다
     * @param year  조회 연도 (예: 2024)
     * @param month 조회 월 (예: 3)
     * @return 해당 월의 공휴일 목록, 실패 시 빈 리스트
     */
    public List<HolidayItemDto> fetchMonthHolidays(int year, int month) {
        try {
            // 1. 공공 API 호출
            HolidayResponseDto response = holidayRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/B090041/openapi/service/SpcdeInfoService/getRestDeInfo")
                            .queryParam("serviceKey", apiKey)
                            .queryParam("solYear", String.format("%04d", year))   // 연도 4자리 보장
                            .queryParam("solMonth", String.format("%02d", month)) // 월 2자리 보장
                            .queryParam("_type", "json")
                            .build())
                    .retrieve()
                    // 2. HTTP 상태 코드별 예외 처리
                    .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                        log.error("공휴일 API 클라이언트 오류: status={}", res.getStatusCode());
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                        log.error("공휴일 API 서버 오류: status={}", res.getStatusCode());
                    })
                    .body(HolidayResponseDto.class);

            // 3. 중첩 JSON 구조를 Optional 체이닝으로 안전하게 파싱
            //    response > body > items > item(List<HolidayItemDto>)
            return Optional.ofNullable(response)                    // response가 null이면 → 바로 emptyList
            .map(HolidayResponseDto::getResponse)         // response.getResponse() — null이면 → 바로 emptyList
            .map(HolidayResponseDto.Response::getBody)    // .getBody() — null이면 → 바로 emptyList
            .map(HolidayResponseDto.Body::getItems)       // .getItems() — null이면 → 바로 emptyList
            .map(HolidayResponseDto.Items::getItems)      // .getItems() — null이면 → 바로 emptyList
            .orElse(Collections.emptyList());             // 최종: List<HolidayItemDto> 또는 빈 리스트

        } catch (Exception e) {
            // 4. 네트워크 장애, 타임아웃 등 onStatus로 잡지 못하는 예외 처리
            log.error("공휴일 API 호출 실패: year={}, month={}, error={}", year, month, e.getMessage());
            return Collections.emptyList();
        }
    }
}
