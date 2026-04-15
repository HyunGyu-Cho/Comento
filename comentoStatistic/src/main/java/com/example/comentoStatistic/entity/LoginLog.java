package com.example.comentoStatistic.entity;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class LoginLog {
    private Long id;
    private Long memberId;
    private LocalDateTime loginAt;
}
