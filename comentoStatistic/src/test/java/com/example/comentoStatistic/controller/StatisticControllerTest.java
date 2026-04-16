package com.example.comentoStatistic.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import com.example.comentoStatistic.dto.AverageDailyLoginDto;
import com.example.comentoStatistic.dto.DailyLoginDto;
import com.example.comentoStatistic.dto.DeptMonthlyLoginDto;
import com.example.comentoStatistic.dto.MonthlyLoginDto;
import com.example.comentoStatistic.service.StatisticService;

@WebMvcTest(StatisticController.class)
class StatisticControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StatisticService statisticService;

    @Test
    @DisplayName("월별 로그인 API는 서비스 결과를 JSON으로 반환한다")
    void getMonthlyLogins_returnsServiceResult() throws Exception {
        MonthlyLoginDto january = monthlyLogin("2025-01", 120L);
        MonthlyLoginDto february = monthlyLogin("2025-02", 95L);
        given(statisticService.getMonthlyLogins("2025-01", "2025-02"))
                .willReturn(List.of(january, february));

        mockMvc.perform(get("/api/v1/stats/monthly")
                        .param("startDate", "2025-01")
                        .param("endDate", "2025-02"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].month").value("2025-01"))
                .andExpect(jsonPath("$[0].loginCount").value(120))
                .andExpect(jsonPath("$[1].month").value("2025-02"))
                .andExpect(jsonPath("$[1].loginCount").value(95));

        then(statisticService).should().getMonthlyLogins(eq("2025-01"), eq("2025-02"));
    }

    @Test
    @DisplayName("필수 파라미터가 없으면 400을 반환한다")
    void getMonthlyLogins_withoutRequiredParam_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/stats/monthly")
                        .param("startDate", "2025-01"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("일별 로그인 API는 서비스 결과를 JSON으로 반환한다")
    void getDailyLogins_returnsServiceResult() throws Exception {
        DailyLoginDto dto = dailyLogin("2025-03-04", 17L);
        given(statisticService.getDailyLogins("2025-03-01", "2025-03-31"))
                .willReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/stats/daily")
                        .param("startDate", "2025-03-01")
                        .param("endDate", "2025-03-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].date").value("2025-03-04"))
                .andExpect(jsonPath("$[0].loginCount").value(17));
    }

    @Test
    @DisplayName("평균 일별 로그인 API는 단일 집계 객체를 반환한다")
    void getAverageDailyLogins_returnsAggregateResult() throws Exception {
        AverageDailyLoginDto dto = averageDailyLogin(42.5, 20L, 850L);
        given(statisticService.getAverageDailyLogins("2025-03-01", "2025-03-31"))
                .willReturn(dto);

        mockMvc.perform(get("/api/v1/stats/daily-avg")
                        .param("startDate", "2025-03-01")
                        .param("endDate", "2025-03-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageDailyLogin").value(42.5))
                .andExpect(jsonPath("$.totalDays").value(20))
                .andExpect(jsonPath("$.totalLogins").value(850));
    }

    @Test
    @DisplayName("휴일 제외 API는 필터링된 일별 로그인 목록을 반환한다")
    void getDailyLoginsWithoutHoliday_returnsFilteredResult() throws Exception {
        DailyLoginDto dto = dailyLogin("2025-03-04", 22L);
        given(statisticService.getDailyLoginsWithoutHoliday("2025-03-01", "2025-03-31"))
                .willReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/stats/daily-no-holiday")
                        .param("startDate", "2025-03-01")
                        .param("endDate", "2025-03-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].date").value("2025-03-04"))
                .andExpect(jsonPath("$[0].loginCount").value(22));
    }

    @Test
    @DisplayName("부서별 월별 로그인 API는 서비스 결과를 JSON으로 반환한다")
    void getDeptMonthlyLogins_returnsServiceResult() throws Exception {
        DeptMonthlyLoginDto dto = deptMonthlyLogin("플랫폼", "2025-03", 61L);
        given(statisticService.getDeptMonthlyLogins("2025-01", "2025-03"))
                .willReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/stats/monthly-dept")
                        .param("startDate", "2025-01")
                        .param("endDate", "2025-03"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].deptName").value("플랫폼"))
                .andExpect(jsonPath("$[0].month").value("2025-03"))
                .andExpect(jsonPath("$[0].loginCount").value(61));
    }

    private MonthlyLoginDto monthlyLogin(String month, long loginCount) {
        MonthlyLoginDto dto = new MonthlyLoginDto();
        ReflectionTestUtils.setField(dto, "month", month);
        ReflectionTestUtils.setField(dto, "loginCount", loginCount);
        return dto;
    }

    private DailyLoginDto dailyLogin(String date, long loginCount) {
        DailyLoginDto dto = new DailyLoginDto();
        ReflectionTestUtils.setField(dto, "date", date);
        ReflectionTestUtils.setField(dto, "loginCount", loginCount);
        return dto;
    }

    private AverageDailyLoginDto averageDailyLogin(double averageDailyLogin, long totalDays, long totalLogins) {
        AverageDailyLoginDto dto = new AverageDailyLoginDto();
        ReflectionTestUtils.setField(dto, "averageDailyLogin", averageDailyLogin);
        ReflectionTestUtils.setField(dto, "totalDays", totalDays);
        ReflectionTestUtils.setField(dto, "totalLogins", totalLogins);
        return dto;
    }

    private DeptMonthlyLoginDto deptMonthlyLogin(String deptName, String month, long loginCount) {
        DeptMonthlyLoginDto dto = new DeptMonthlyLoginDto();
        ReflectionTestUtils.setField(dto, "deptName", deptName);
        ReflectionTestUtils.setField(dto, "month", month);
        ReflectionTestUtils.setField(dto, "loginCount", loginCount);
        return dto;
    }
}
