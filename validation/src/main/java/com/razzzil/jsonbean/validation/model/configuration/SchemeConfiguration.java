package com.razzzil.jsonbean.validation.model.configuration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Properties config
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SchemeConfiguration {

    private JsonBeanConfiguration jsonbean;
    private JsonSchemaConfiguration jsonschema;

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class JsonBeanConfiguration {
        private Boolean enabled;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class JsonSchemaConfiguration {
        private Boolean enabled;
        private String url;
    }
}
