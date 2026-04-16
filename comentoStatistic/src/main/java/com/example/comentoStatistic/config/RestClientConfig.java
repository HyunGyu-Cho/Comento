package com.example.comentoStatistic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient holidayRestClient() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(5000); // 서버 연결 5초 넘으면 포기
        requestFactory.setReadTimeout(5000); // 연결 후 응답을 기다리는 시간이 5초 넘으면 포기

        return RestClient.builder()
                .baseUrl("https://apis.data.go.kr")
                .requestFactory(requestFactory)
                .build();
    }
}
