package com.example.comentoStatistic.dto;

import lombok.Getter;

@Getter
public class DeptMonthlyLoginDto {
    private String deptName;
    private String month;
    private long loginCount;
}
