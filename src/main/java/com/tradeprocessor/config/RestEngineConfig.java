package com.tradeprocessor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestEngineConfig {
  @Bean
  RestClient riskEngineClient() {
    return RestClient.builder().baseUrl("http://127.0.0.1:8000")
        .defaultHeader("Content-Type", "application/json").build();
  }
}
