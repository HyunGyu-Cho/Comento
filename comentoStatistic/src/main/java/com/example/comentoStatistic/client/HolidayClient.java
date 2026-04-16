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

@Component
public class HolidayClient {

    private static final Logger log = LoggerFactory.getLogger(HolidayClient.class);

    private final RestClient holidayRestClient;
    private final String apiKey;

    public HolidayClient(RestClient holidayRestClient, @Value("${HOLIDAY_API_KEY}") String apiKey) {
        this.holidayRestClient = holidayRestClient;
        this.apiKey = apiKey;
    }

    public List<HolidayItemDto> fetchMonthHolidays(int year, int month) {
        try {
            HolidayResponseDto response = holidayRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/B090041/openapi/service/SpcdeInfoService/getRestDeInfo")
                            .queryParam("serviceKey", apiKey)
                            .queryParam("solYear", String.format("%04d", year))
                            .queryParam("solMonth", String.format("%02d", month))
                            .queryParam("_type", "json")
                            .build())
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                        log.error("공휴일 API 클라이언트 오류: status={}", res.getStatusCode());
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                        log.error("공휴일 API 서버 오류: status={}", res.getStatusCode());
                    })
                    .body(HolidayResponseDto.class);

            return Optional.ofNullable(response)
                    .map(HolidayResponseDto::getResponse)
                    .map(HolidayResponseDto.Response::getBody)
                    .map(HolidayResponseDto.Body::getItems)
                    .map(HolidayResponseDto.Items::getItems)
                    .orElse(Collections.emptyList());

        } catch (Exception e) {
            log.error("공휴일 API 호출 실패: year={}, month={}, error={}", year, month, e.getMessage());
            return Collections.emptyList();
        }
    }
}
