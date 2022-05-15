package com.razzzil.jsonbean.validation.configuration;

import com.razzzil.jsonbean.validation.model.jsonbean.JsonBeanConfig;
import com.razzzil.jsonbean.validation.service.JsonBeanAnalyzer;
import com.razzzil.jsonbean.validation.service.MessageLocalizationResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Custom Json Schema (Json Bean) configuration for Spring
 * Enabled by com.razzzil.jsonbean.enabled=true
 */
@Configuration
@Slf4j
@ConditionalOnProperty(prefix = "com.razzzil.jsonbean", value = "enabled", havingValue = "true")
public class JsonBeanConfiguration {

    @Bean
    public JsonBeanConfig getJsonBeanConfiguration(ApplicationContext context) {
        log.info("Configuring Json Bean without Localization");
        return JsonBeanAnalyzer.getJsonConfiguration(context, message -> message);
    }

    @Bean
    @ConditionalOnBean(MessageLocalizationResolver.class)
    @Primary
    public JsonBeanConfig getJsonBeanConfiguration(ApplicationContext context, MessageLocalizationResolver messageLocalizationResolver) {
        log.info("Configuring Json Bean with Localization");
        return JsonBeanAnalyzer.getJsonConfiguration(context, messageLocalizationResolver::getValidationMessage);
    }

}
