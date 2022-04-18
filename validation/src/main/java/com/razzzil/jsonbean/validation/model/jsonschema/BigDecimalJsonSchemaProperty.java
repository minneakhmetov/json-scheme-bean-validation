package com.razzzil.jsonbean.validation.model.jsonschema;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@SuperBuilder
@Data
public class BigDecimalJsonSchemaProperty extends NumberJsonSchemaProperty<BigDecimal> {

    @Override
    protected Type getType() {
        return Type.INTEGER;
    }
}
