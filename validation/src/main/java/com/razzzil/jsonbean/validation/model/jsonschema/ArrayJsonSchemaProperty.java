package com.razzzil.jsonbean.validation.model.jsonschema;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Data
public class ArrayJsonSchemaProperty extends JsonSchemaProperty {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer maxItems;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer minItems;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<JsonSchemaProperty> items;

    @Override
    protected Type getType() {
        return Type.ARRAY;
    }
}
