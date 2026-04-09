package com.example.comentoStatistic.service;

import com.example.comentoStatistic.dao.StatisticMapper;
import com.example.comentoStatistic.dto.YearCountDto;
import com.example.comentoStatistic.dto.YearMonthCountDto;
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
}
