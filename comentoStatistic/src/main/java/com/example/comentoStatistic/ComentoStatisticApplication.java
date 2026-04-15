package com.example.comentoStatistic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class ComentoStatisticApplication {

	public static void main(String[] args) {
		SpringApplication.run(ComentoStatisticApplication.class, args);
	}

}
