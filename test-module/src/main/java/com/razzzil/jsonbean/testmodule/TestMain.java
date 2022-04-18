package com.razzzil.jsonbean.testmodule;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.customProperties.ValidationSchemaFactoryWrapper;
import com.github.victools.jsonschema.generator.*;
import com.razzzil.jsonbean.testmodule.dto.ValidationTestForm;
import lombok.SneakyThrows;

public class TestMain {
    @SneakyThrows
    public static void main(String[] args) {
        ValidationSchemaFactoryWrapper personVisitor = new ValidationSchemaFactoryWrapper();
        ObjectMapper mapper = new ObjectMapper();
        mapper.acceptJsonFormatVisitor(ValidationTestForm.class, personVisitor);
        JsonSchema personSchema = personVisitor.finalSchema();

        SchemaGeneratorConfigBuilder configBuilder = new SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_2019_09, OptionPreset.PLAIN_JSON);
        SchemaGeneratorConfig config = configBuilder.build();
        SchemaGenerator generator = new SchemaGenerator(config);
        JsonNode jsonSchema = generator.generateSchema(ValidationTestForm.class);

        int i = 0;
    }
}
