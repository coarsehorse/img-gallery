package com.agileengine.imggallery.conf;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Created by coarse_horse on 21/08/2020
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(ObjectMapper objectMapper) {
        return new RestTemplate();
    }
}
