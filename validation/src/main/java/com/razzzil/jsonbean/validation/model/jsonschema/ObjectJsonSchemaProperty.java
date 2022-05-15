package com.razzzil.jsonbean.validation.model.jsonschema;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Map;

/**
 * JsonSchema Object properties
 */
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Data
public class ObjectJsonSchemaProperty extends JsonSchemaProperty {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<JsonSchemaProperty> allOf;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<JsonSchemaProperty> oneOf;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<JsonSchemaProperty> anyOf;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, JsonSchemaProperty> properties;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> required;

    @Override
    protected Type getType() {
        return Type.OBJECT;
    }
}
