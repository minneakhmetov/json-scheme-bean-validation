package com.razzzil.jsonbean.validation.model.jsonschema;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Data
public abstract class NumberJsonSchemaProperty<T extends Number> extends JsonSchemaProperty {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T minimum;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T maximum;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T exclusiveMinimum;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T exclusiveMaximum;

    public static <V extends Number> void processFieldProperties(List<JsonSchemaProperty> fieldProperties, Class<V> bigClass, Consumer<NumberJsonSchemaProperty<V>> consumer) {
        Iterator<JsonSchemaProperty> iterator = fieldProperties.iterator();
        while (iterator.hasNext()) {
            JsonSchemaProperty fieldProperty = iterator.next();
            if (fieldProperty instanceof BigIntegerJsonSchemaProperty) {
                if (bigClass.equals(BigInteger.class)) {
                    consumer.accept((NumberJsonSchemaProperty<V>) fieldProperty);
                    return;
                }
                if (bigClass.equals(BigDecimal.class)) {
                    BigDecimalJsonSchemaProperty property = ((BigIntegerJsonSchemaProperty) fieldProperty).convertToBigDecimal();
                    consumer.accept((NumberJsonSchemaProperty<V>) fieldProperty);
                    iterator.remove();
                    fieldProperties.add(property);
                    return;
                }
            }
            if (fieldProperty instanceof BigDecimalJsonSchemaProperty) {
                consumer.accept(((NumberJsonSchemaProperty<V>) fieldProperty));
                return;
            }
        }
        if (bigClass.equals(BigInteger.class)) {
            BigIntegerJsonSchemaProperty integerJsonSchemaProperty = new BigIntegerJsonSchemaProperty();
            consumer.accept((NumberJsonSchemaProperty<V>) integerJsonSchemaProperty);
            fieldProperties.add(integerJsonSchemaProperty);
        }
        if (bigClass.equals(BigDecimal.class)) {
            BigDecimalJsonSchemaProperty decimalJsonSchemaProperty = new BigDecimalJsonSchemaProperty();
            consumer.accept((NumberJsonSchemaProperty<V>) decimalJsonSchemaProperty);
            fieldProperties.add(decimalJsonSchemaProperty);
        }
    }



}
