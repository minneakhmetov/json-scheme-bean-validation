package com.razzzil.jsonbean.validation.model.jsonbean;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class JsonBean {
    private String entityName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String description;
    private Map<String, Map<String, Map<String, Object>>> fields;
}
