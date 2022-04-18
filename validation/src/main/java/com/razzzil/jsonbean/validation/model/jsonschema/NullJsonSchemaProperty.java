package com.razzzil.jsonbean.validation.model.jsonschema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@SuperBuilder
@Data
public class NullJsonSchemaProperty extends JsonSchemaProperty {
    @Override
    protected Type getType() {
        return Type.NULL;
    }
}
