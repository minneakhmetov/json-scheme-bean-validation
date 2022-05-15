package com.razzzil.jsonbean.validation.model.jsonbean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Custom Json Schema (Json Bean) Display DTO
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class JsonBeanConfig {
    private Map<String, JsonBean> configuration;
}
