package com.razzzil.jsonbean.validation.model.jsonschema;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * String JsonSchema properties
 */
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Data
public class StringJsonSchemaProperty extends JsonSchemaProperty {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer maxLength;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer minLength;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String pattern;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Format format;

    @Override
    protected Type getType() {
        return Type.STRING;
    }

    public static void processFieldProperties(List<JsonSchemaProperty> fieldProperties, Consumer<StringJsonSchemaProperty> consumer){
        Optional<StringJsonSchemaProperty> stringJsonSchemaPropertyCandidate = fieldProperties.stream()
                .filter(jsonSchemaProperty -> jsonSchemaProperty instanceof StringJsonSchemaProperty)
                .findFirst()
                .map(jsonSchemaProperty -> (StringJsonSchemaProperty) jsonSchemaProperty);
        if (stringJsonSchemaPropertyCandidate.isPresent()) {
            consumer.accept(stringJsonSchemaPropertyCandidate.get());
        } else {
            StringJsonSchemaProperty property = new StringJsonSchemaProperty();
            consumer.accept(property);
            fieldProperties.add(property);
        }
    }
}
