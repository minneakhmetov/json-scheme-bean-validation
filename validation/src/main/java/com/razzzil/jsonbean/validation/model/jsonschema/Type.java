package com.razzzil.jsonbean.validation.model.jsonschema;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter(onMethod = @__(@JsonValue))
public enum Type {
    OBJECT("object"),
    ARRAY("array"),
    ENUM("enum"),
    FORMATTED("string"),
    BOOLEAN("boolean"),
    STRING("string"),
    INTEGER("integer"),
    NUMBER("number"),
    NULL("null");

    private String name;
}
