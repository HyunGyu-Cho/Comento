package com.example.comentoStatistic.service;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.comentoStatistic.client.HolidayClient;
import com.example.comentoStatistic.dto.HolidayItemDto;

@Service
public class HolidayService {

    private final HolidayClient holidayClient;

    public HolidayService(HolidayClient holidayClient) {
        this.holidayClient = holidayClient;
    }

    public List<String> getHolidays(String startDate, String endDate) {
        List<String> holidays = new ArrayList<>();

        YearMonth start = YearMonth.parse(startDate.substring(0, 7));
        YearMonth end = YearMonth.parse(endDate.substring(0, 7));

        for (YearMonth yearMonth = start; !yearMonth.isAfter(end); yearMonth = yearMonth.plusMonths(1)) {
            List<HolidayItemDto> monthHolidays = holidayClient.fetchMonthHolidays(
                    yearMonth.getYear(),
                    yearMonth.getMonthValue()
            );

            for (HolidayItemDto holiday : monthHolidays) {
                String locdate = holiday.getLocdate();
                if (locdate == null || locdate.length() != 8) {
                    continue;
                }

                holidays.add(locdate.substring(0, 4) + "-"
                        + locdate.substring(4, 6) + "-"
                        + locdate.substring(6, 8));
            }
        }

        return holidays;
    }
}
