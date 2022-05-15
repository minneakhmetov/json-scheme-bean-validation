package com.razzzil.jsonbean.validation.model.jsonschema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * JsonSchema integer property
 */
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@SuperBuilder
@Data
public class BigIntegerJsonSchemaProperty extends NumberJsonSchemaProperty<BigInteger> {

    @Override
    protected Type getType() {
        return Type.INTEGER;
    }

    public BigDecimalJsonSchemaProperty convertToBigDecimal() {
        return BigDecimalJsonSchemaProperty.builder()
                .exclusiveMaximum(new BigDecimal(super.getExclusiveMaximum()))
                .exclusiveMinimum(new BigDecimal(super.getExclusiveMinimum()))
                .maximum(new BigDecimal(super.getMaximum()))
                .minimum(new BigDecimal(super.getMinimum()))
                .build();
    }
}
