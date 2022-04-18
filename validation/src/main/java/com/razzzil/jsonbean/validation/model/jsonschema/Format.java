package com.razzzil.jsonbean.validation.model.jsonschema;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter(onMethod = @__(@JsonValue))
public enum Format {
    DATETIME("date-time"),
    DATE("date"),
    TIME("time"),
    DURATION("duration");

    private String name;

}
