package com.example.comentoStatistic.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.comentoStatistic.client.HolidayClient;
import com.example.comentoStatistic.dto.HolidayItemDto;

@ExtendWith(MockitoExtension.class)
class HolidayServiceTest {

    @Mock
    private HolidayClient holidayClient;

    @InjectMocks
    private HolidayService holidayService;

    @Test
    @DisplayName("공휴일 조회는 월 단위로 외부 API를 호출하고 yyyy-MM-dd 형식으로 반환한다")
    void getHolidays_collectsAcrossMonths() {
        given(holidayClient.fetchMonthHolidays(2025, 3))
                .willReturn(List.of(holiday("삼일절 대체휴일", "20250303")));
        given(holidayClient.fetchMonthHolidays(2025, 4))
                .willReturn(List.of(holiday("식목일", "20250405")));

        List<String> result = holidayService.getHolidays("2025-03-01", "2025-04-30");

        assertThat(result).containsExactly("2025-03-03", "2025-04-05");
        then(holidayClient).should().fetchMonthHolidays(2025, 3);
        then(holidayClient).should().fetchMonthHolidays(2025, 4);
    }

    @Test
    @DisplayName("잘못된 locdate 형식은 무시한다")
    void getHolidays_ignoresInvalidLocdate() {
        given(holidayClient.fetchMonthHolidays(2025, 3))
                .willReturn(List.of(
                        holiday("정상 데이터", "20250301"),
                        holiday("누락 데이터", null),
                        holiday("잘못된 길이", "202531")
                ));

        List<String> result = holidayService.getHolidays("2025-03-01", "2025-03-31");

        assertThat(result).containsExactly("2025-03-01");
    }

    private HolidayItemDto holiday(String dateName, String locdate) {
        HolidayItemDto dto = new HolidayItemDto();
        ReflectionTestUtils.setField(dto, "dateName", dateName);
        ReflectionTestUtils.setField(dto, "locdate", locdate);
        ReflectionTestUtils.setField(dto, "isHoliday", "Y");
        return dto;
    }
}
