package com.razzzil.jsonbean.validation.controller;

import com.razzzil.jsonbean.validation.model.jsonschema.JsonRootSchema;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.PermitAll;
import java.util.Map;
import java.util.Set;

/**
 * JsonSchema Controller
 * Enabled by property com.razzzil.jsonschema.enabled=true
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(JsonSchemaController.URI)
@ConditionalOnProperty(prefix = "com.razzzil.jsonschema", value = "enabled", havingValue = "true")
public class JsonSchemaController {

    public static final String URI = "/scheme/jsonschema";

    private final Map<String, JsonRootSchema> configuration;

    /**
     * @return Set<String> Set Of Names DTOs
     */
    @GetMapping
    @PermitAll
    public ResponseEntity<Set<String>> scheme() {
        return ResponseEntity.ok(configuration.keySet());
    }

    /**
     * Get Scheme by DTO name
     * @param schemeName DTO name
     * @return JsonRootSchema JsonSchema model
     */
    @GetMapping("/{schemeName}")
    @PermitAll
    public ResponseEntity<JsonRootSchema> scheme(@PathVariable String schemeName) {
        return ResponseEntity.ok(configuration.get(schemeName));
    }
}
