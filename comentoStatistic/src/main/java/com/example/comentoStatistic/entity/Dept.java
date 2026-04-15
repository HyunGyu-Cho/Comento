package com.example.comentoStatistic.entity;

import lombok.Getter;

@Getter
public class Dept {
    private Long id;
    private String deptName; // 부서명
    private String memberId; // Member 테이블 참조(FK) 
    private String empName; // 사원명
}
