package com.razzzil.jsonbean.validation.model.jsonschema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class EnumJsonSchemaProperty extends JsonSchemaProperty {

    @JsonProperty("enum")
    private List<String> enumValues;

    @JsonIgnore
    @Override
    protected Type getType() {
        return Type.ENUM;
    }
}
