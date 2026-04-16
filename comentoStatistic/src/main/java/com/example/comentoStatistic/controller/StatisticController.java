package com.example.comentoStatistic.controller;

import com.example.comentoStatistic.dto.AverageDailyLoginDto;
import com.example.comentoStatistic.dto.DailyLoginDto;
import com.example.comentoStatistic.dto.DeptMonthlyLoginDto;
import com.example.comentoStatistic.dto.MonthlyLoginDto;
import com.example.comentoStatistic.dto.YearCountDto;
import com.example.comentoStatistic.service.StatisticService;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/stats")
public class StatisticController {
    @Autowired
    StatisticService statisticService;

    /*@GetMapping(value="/{year}", produces = "application/json")
    public ResponseEntity<YearCountDto> getYearLoginCount(@PathVariable("year") String year) {
        return ResponseEntity.ok(statisticService.getYearLogins(year));
    }

    @GetMapping(value="/{year}/{month}", produces = "application/json")
    public Object getYearMonthLoginCount(@PathVariable("year") String year,
                                         @PathVariable("month") String month) {
        return ResponseEntity.ok(statisticService.getYearMonthLogins(year, month));
    }*/

    @GetMapping(value="/monthly", produces = "application/json")
    public ResponseEntity<List<MonthlyLoginDto>> getMonthlyLoginCount(
            @RequestParam String startDate, @RequestParam String endDate
    ) {
        return ResponseEntity.ok(statisticService.getMonthlyLogins(startDate, endDate));
    }

    
    @GetMapping(value="/daily", produces = "application/json")
    public ResponseEntity<List<DailyLoginDto>> getDailyLogins(
            @RequestParam String startDate, @RequestParam String endDate) {
        return ResponseEntity.status(HttpStatus.OK).body(statisticService.getDailyLogins(startDate, endDate));
            }
    


    @GetMapping(value="/daily-avg", produces = "application/json")
    public ResponseEntity<AverageDailyLoginDto> getAverageDailyLogins(
            @RequestParam String startDate, @RequestParam String endDate) {
        return ResponseEntity.ok(statisticService.getAverageDailyLogins(startDate, endDate));
    }

    @GetMapping(value="/daily-no-holiday", produces = "application/json")
    public ResponseEntity<List<DailyLoginDto>> getDailyLoginsNoHoliday(
            @RequestParam String startDate, @RequestParam String endDate) {
        return ResponseEntity.ok(statisticService.getDailyLoginsNoHoliday(startDate, endDate));
    }

    @GetMapping(value="/monthly-dept", produces = "application/json")
    public ResponseEntity<List<DeptMonthlyLoginDto>> getDeptMonthlyLogins(
            @RequestParam String startDate, @RequestParam String endDate) {
        return ResponseEntity.ok(statisticService.getDeptMonthlyLogins(startDate, endDate));   
    }
}
