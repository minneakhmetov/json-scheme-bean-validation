package com.razzzil.jsonbean.validation.model.jsonschema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * JsonSchema Number Property with floating point
 */
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@SuperBuilder
@Data
public class BigDecimalJsonSchemaProperty extends NumberJsonSchemaProperty<BigDecimal> {

    @Override
    protected Type getType() {
        return Type.NUMBER;
    }
}
