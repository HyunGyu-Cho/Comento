package com.example.comentoStatistic.service;

import com.example.comentoStatistic.dao.StatisticMapper;
import com.example.comentoStatistic.dto.YearCountDto;
import com.example.comentoStatistic.dto.AverageDailyLoginDto;
import com.example.comentoStatistic.dto.DailyLoginDto;
import com.example.comentoStatistic.dto.DeptMonthlyLoginDto;
import com.example.comentoStatistic.dto.MonthlyLoginDto;
import com.example.comentoStatistic.dto.YearMonthCountDto;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatisticService {

    @Autowired
    StatisticMapper statisticMapper;

    public YearCountDto getYearLogins(String year) {
        return statisticMapper.selectYearLogin(year);
    }

    public YearMonthCountDto getYearMonthLogins(String year, String month) {

        return statisticMapper.selectYearMonthLogin(year+month);
    }


    // 1. 월별 접속자 수
    public List<MonthlyLoginDto> getMonthlyLogins(String startDate, String endDate) {
        return statisticMapper.selectMonthLoginCount(startDate, endDate);
    }

    // 2. 일자별 접속자 수
    public List<DailyLoginDto> getDailyLogins(String startDate, String endDate) {
        return statisticMapper.selectDailyLoginCount(startDate, endDate);
    }

    // 3. 평균 하루 로그인 수
    public AverageDailyLoginDto getAverageDailyLogins(String startDate, String endDate) {
        return statisticMapper.selectAverageDailyLogin(startDate, endDate);
    }

    //4. 휴일 포함 로그인 수 

    //5. 부서별 월별 로그인 수 
    public List<DeptMonthlyLoginDto> getDeptMonthlyLogins(String startDate, String endDate) {
        return statisticMapper.selectDeptMonthlyLogin(startDate, endDate);
    }
}
