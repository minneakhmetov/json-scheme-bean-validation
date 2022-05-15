package com.razzzil.jsonbean.validation.model.jsonschema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * JsonSchema Object properties with hidden type
 */
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@SuperBuilder
@Data
public class IgnoredTypeObjectJsonSchemaProperty extends ObjectJsonSchemaProperty {

    @JsonIgnore
    @Override
    protected Type getType() {
        return super.getType();
    }
}
