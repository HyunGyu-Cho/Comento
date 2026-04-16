package com.example.comentoStatistic.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.comentoStatistic.dao.StatisticMapper;
import com.example.comentoStatistic.dto.AverageDailyLoginDto;
import com.example.comentoStatistic.dto.DailyLoginDto;
import com.example.comentoStatistic.dto.DeptMonthlyLoginDto;
import com.example.comentoStatistic.dto.MonthlyLoginDto;

@ExtendWith(MockitoExtension.class)
class StatisticServiceTest {

    @Mock
    private StatisticMapper statisticMapper;

    @Mock
    private HolidayService holidayService;

    @InjectMocks
    private StatisticService statisticService;

    @Test
    @DisplayName("휴일 제외 일별 로그인 조회는 공휴일과 주말 데이터를 제거한다")
    void getDailyLoginsWithoutHoliday_excludesHolidaysAndWeekends() {
        DailyLoginDto holiday = dailyLogin("2025-03-03", 30L);
        DailyLoginDto weekday = dailyLogin("2025-03-04", 40L);
        DailyLoginDto saturday = dailyLogin("2025-03-08", 10L);
        DailyLoginDto sunday = dailyLogin("2025-03-09", 12L);

        given(statisticMapper.selectDailyLoginCount("2025-03-01", "2025-03-10"))
                .willReturn(List.of(holiday, weekday, saturday, sunday));
        given(holidayService.getHolidays("2025-03-01", "2025-03-10"))
                .willReturn(List.of("2025-03-03"));

        List<DailyLoginDto> result = statisticService.getDailyLoginsWithoutHoliday("2025-03-01", "2025-03-10");

        assertThat(result).extracting(DailyLoginDto::getDate)
                .containsExactly("2025-03-04");
    }

    @Test
    @DisplayName("월별 로그인 조회는 매퍼 호출을 위임한다")
    void getMonthlyLogins_delegatesToMapper() {
        List<MonthlyLoginDto> expected = List.of(monthlyLogin("2025-01", 100L));
        given(statisticMapper.selectMonthLoginCount("2025-01", "2025-03")).willReturn(expected);

        List<MonthlyLoginDto> result = statisticService.getMonthlyLogins("2025-01", "2025-03");

        assertThat(result).isSameAs(expected);
        then(statisticMapper).should().selectMonthLoginCount("2025-01", "2025-03");
        then(holidayService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("일별 로그인 조회는 매퍼 호출을 위임한다")
    void getDailyLogins_delegatesToMapper() {
        List<DailyLoginDto> expected = List.of(dailyLogin("2025-03-04", 25L));
        given(statisticMapper.selectDailyLoginCount("2025-03-01", "2025-03-31")).willReturn(expected);

        List<DailyLoginDto> result = statisticService.getDailyLogins("2025-03-01", "2025-03-31");

        assertThat(result).isSameAs(expected);
        then(statisticMapper).should().selectDailyLoginCount("2025-03-01", "2025-03-31");
    }

    @Test
    @DisplayName("평균 일별 로그인 조회는 매퍼 호출을 위임한다")
    void getAverageDailyLogins_delegatesToMapper() {
        AverageDailyLoginDto expected = averageDailyLogin(15.4, 20L, 308L);
        given(statisticMapper.selectAverageDailyLogin("2025-03-01", "2025-03-31")).willReturn(expected);

        AverageDailyLoginDto result = statisticService.getAverageDailyLogins("2025-03-01", "2025-03-31");

        assertThat(result).isSameAs(expected);
        then(statisticMapper).should().selectAverageDailyLogin("2025-03-01", "2025-03-31");
    }

    @Test
    @DisplayName("부서별 월별 로그인 조회는 매퍼 호출을 위임한다")
    void getDeptMonthlyLogins_delegatesToMapper() {
        List<DeptMonthlyLoginDto> expected = List.of(deptMonthlyLogin("플랫폼", "2025-03", 60L));
        given(statisticMapper.selectDeptMonthlyLogin("2025-01", "2025-03")).willReturn(expected);

        List<DeptMonthlyLoginDto> result = statisticService.getDeptMonthlyLogins("2025-01", "2025-03");

        assertThat(result).isSameAs(expected);
        then(statisticMapper).should().selectDeptMonthlyLogin("2025-01", "2025-03");
        then(holidayService).should(never()).getHolidays("2025-01", "2025-03");
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
