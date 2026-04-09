package com.example.comentoStatistic.dao;

import com.example.comentoStatistic.dto.YearCountDto;
import com.example.comentoStatistic.dto.YearMonthCountDto;

//이 인터페이스는 SQL을 호출하기 위한 "자바 메서드 형태의 창구"
//MyBatis: Cotroller -> Service -> Mapper interface -> MyBatis -> XML(SQL) -> DB
//즉 이 인터페이스는 자바 코드와 SQL을 연결하는 중간다리
public interface StatisticMapper {
    YearCountDto selectYearLogin(String year);
    YearMonthCountDto selectYearMonthLogin(String yearMonth);
}
