package com.razzzil.jsonbean.validation.configuration;

import com.razzzil.jsonbean.validation.model.configuration.SchemeConfiguration;
import com.razzzil.jsonbean.validation.model.jsonbean.JsonBeanConfig;
import com.razzzil.jsonbean.validation.model.jsonschema.JsonRootSchema;
import com.razzzil.jsonbean.validation.service.JsonBeanAnalyzer;
import com.razzzil.jsonbean.validation.service.JsonSchemaAnalyzer;
import com.razzzil.jsonbean.validation.service.MessageLocalizationResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Map;


/**
 * JsonSchema Configuration
 * Enabled by com.razzzil.jsonschema.enabled=true
 */
@Configuration
@ConditionalOnProperty(prefix = "com.razzzil.jsonschema", value = "enabled", havingValue = "true")
@Slf4j
public class JsonSchemaConfiguration {

    @Bean
    public Map<String, JsonRootSchema> getJsonSchemaConfiguration(ApplicationContext context, SchemeConfiguration schemeConfiguration) {
        log.info("Configuring Json Schema without Localization");
        return JsonSchemaAnalyzer.getJsonSchema(context, schemeConfiguration.getJsonschema(), message -> message);
    }

//    @Bean
//    @ConditionalOnBean(MessageLocalizationResolver.class)
//    @Primary
//    public Map<String, JsonRootSchema> getJsonSchemaConfiguration(ApplicationContext context, SchemeConfiguration schemeConfiguration, MessageLocalizationResolver messageLocalizationResolver) {
//        log.info("Configuring Json Schema with Localization");
//        return JsonSchemaAnalyzer.getJsonSchema(context, schemeConfiguration.getJsonschema(), messageLocalizationResolver::getValidationMessage);
//    }

}
