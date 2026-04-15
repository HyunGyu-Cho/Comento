package com.example.comentoStatistic.entity;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class Member {

    private Long id;
    private String email;
    private String password;
    private LocalDateTime createdAt;
}
