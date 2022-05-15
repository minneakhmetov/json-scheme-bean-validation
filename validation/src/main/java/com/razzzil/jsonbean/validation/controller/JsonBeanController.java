package com.razzzil.jsonbean.validation.controller;

import com.razzzil.jsonbean.validation.model.jsonbean.JsonBean;
import com.razzzil.jsonbean.validation.model.jsonbean.JsonBeanConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.PermitAll;
import java.util.Set;


/**
 * Custom Json Schema (Json Bean) Controller
 * Enabled by property com.razzzil.jsonbean.enabled=true
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/scheme/jsonbean")
@ConditionalOnProperty(prefix = "com.razzzil.jsonbean", value = "enabled", havingValue = "true")
public class JsonBeanController {

    private final JsonBeanConfig configuration;

    /**
     * @return Set<String> Set Of Names DTOs
     */
    @GetMapping
    @PermitAll
    public ResponseEntity<Set<String>> scheme() {
        return ResponseEntity.ok(configuration.getConfiguration().keySet());
    }

    /**
     * Get Scheme by DTO name
     * @param schemeName DTO name
     * @return JsonBean Scheme of DTO
     */
    @GetMapping("/{schemeName}")
    @PermitAll
    public ResponseEntity<JsonBean> scheme(@PathVariable String schemeName) {
        return ResponseEntity.ok(configuration.getConfiguration().get(schemeName));
    }
}
