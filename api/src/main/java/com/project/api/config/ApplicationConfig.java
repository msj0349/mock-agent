package com.project.api.config;

import com.project.grammar.parser.OracleToMySQLConverter;
import com.project.mapping.service.ConversionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    public OracleToMySQLConverter oracleToMySQLConverter() {
        return new OracleToMySQLConverter();
    }

    @Bean
    public ConversionService conversionService(OracleToMySQLConverter converter) {
        return new ConversionService(converter);
    }
}
