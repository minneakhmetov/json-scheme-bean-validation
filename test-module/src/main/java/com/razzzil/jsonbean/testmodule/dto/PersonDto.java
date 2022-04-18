package com.razzzil.jsonbean.testmodule.dto;

import com.razzzil.jsonbean.validation.annotation.JsonSchemed;
import com.razzzil.jsonbean.validation.constant.SchemaVersion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@JsonSchemed(schema = SchemaVersion.DRAFT_6,
        description = "Person representation",
        title = "PersonDto")
public class PersonDto {

    private String name;
    private String surname;
    private Integer age;
}
