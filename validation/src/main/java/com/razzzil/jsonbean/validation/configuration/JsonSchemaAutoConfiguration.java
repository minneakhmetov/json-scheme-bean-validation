package com.razzzil.jsonbean.validation.configuration;

import com.razzzil.jsonbean.validation.controller.JsonBeanController;
import com.razzzil.jsonbean.validation.controller.JsonSchemaController;
import com.razzzil.jsonbean.validation.model.configuration.SchemeConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


/**
 * Spring Started Main Auto Configuration
 */
@Configuration
@Slf4j
@Import({JsonBeanController.class, JsonSchemaController.class, JsonBeanConfiguration.class, JsonSchemaConfiguration.class})
public class JsonSchemaAutoConfiguration {


    /**
     * @return SchemeConfiguration properties configuration
     */
    @Bean
    @ConfigurationProperties(prefix = "com.razzzil")
    public SchemeConfiguration schemeConfiguration(){
        return new SchemeConfiguration();
    }
}
