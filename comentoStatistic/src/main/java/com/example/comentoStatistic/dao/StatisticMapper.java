package com.example.comentoStatistic.dao;

import com.example.comentoStatistic.dto.AverageDailyLoginDto;
import com.example.comentoStatistic.dto.DailyLoginDto;
import com.example.comentoStatistic.dto.DeptMonthlyLoginDto;
import com.example.comentoStatistic.dto.MonthlyLoginDto;
import com.example.comentoStatistic.dto.YearCountDto;
import com.example.comentoStatistic.dto.YearMonthCountDto;

import java.util.List;

import org.apache.ibatis.annotations.Param;

//이 인터페이스는 SQL을 호출하기 위한 "자바 메서드 형태의 창구"
//MyBatis: Cotroller -> Service -> Mapper interface -> MyBatis -> XML(SQL) -> DB
//즉 이 인터페이스는 자바 코드와 SQL을 연결하는 중간다리
public interface StatisticMapper {
    YearCountDto selectYearLogin(String year);
    YearMonthCountDto selectYearMonthLogin(String yearMonth);
    List<MonthlyLoginDto> selectMonthLoginCount(@Param("startDate") String startDate, 
                            @Param("endDate") String endDate);
    List<DailyLoginDto> selectDailyLoginCount(@Param("startDate") String startDate, 
                            @Param("endDate") String endDate);
    AverageDailyLoginDto selectAverageDailyLogin(@Param("startDate") String startDate,
                            @Param("endDate") String endDate);

    List<DeptMonthlyLoginDto> selectDeptMonthlyLogin(@Param("startDate") String startDate,
                            @Param("endDate") String endDate);
}

// service 에서 statisticMapper.selectMonthLoginCount() 호출
// MyBatis가 StatisticMapper.xml에서 id = "selectMonthlyLoginCount" 인 SQL을 찾음
// SQL 실행 결과를 resultType = "MonthlyLoginDto" 에 맞춰 매핑
// 여러 행이 반환되면 List<MonthlyLoginDto>로 자동 변환하여 리턴