package com.razzzil.jsonbean.validation.service;

import com.razzzil.jsonbean.validation.annotation.JsonSchemed;
import com.razzzil.jsonbean.validation.model.jsonbean.JsonBean;
import com.razzzil.jsonbean.validation.model.jsonbean.JsonBeanConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.ApplicationContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.razzzil.jsonbean.validation.constant.SchemaConstants.JAVAX_ANNOTATIONS_SEARCH_PACKAGE;

/**
 * Custom Json Schema (Json Bean) Analyzer Service
 */
@Slf4j
public class JsonBeanAnalyzer {

    private static final List<String> IGNORED_ANNOTATION_PROPERTIES = List.of("groups", "payload", "getClass", "annotationType", "toString", "hashCode", "equals", "message");

    private static final String MESSAGE_METHOD_NAME = "message";

    /**
     * @param context Spring Application Context
     * @param messageSource function to apply localization
     * @return JsonBeanConfig full config
     */
    @SneakyThrows
    public static JsonBeanConfig getJsonConfiguration(ApplicationContext context, Function<String, Object> messageSource) {
        Reflections javaxAnnotations = new Reflections(JAVAX_ANNOTATIONS_SEARCH_PACKAGE);
        Set<String> javaxAnnotationClasses = javaxAnnotations
                .getStore()
                .get("SubTypes")
                .get(Annotation.class.getCanonicalName());
        Map<String, JsonBean> fullConfiguration = new HashMap<>();
        List<String> packages = AutoConfigurationPackages.get(context.getAutowireCapableBeanFactory());
        for (String packageName : packages) {
            Reflections reflections = new Reflections(packageName);
            Set<Class<?>> jsonSchemedClasses = reflections.getTypesAnnotatedWith(JsonSchemed.class);
            for (Class<?> jsonSchemedClass : jsonSchemedClasses) {
                JsonSchemed jsonSchemedClassAnnotation = jsonSchemedClass.getAnnotation(JsonSchemed.class);
                String entityName = "".equals(jsonSchemedClassAnnotation.title()) ? jsonSchemedClass.getSimpleName() : jsonSchemedClassAnnotation.title();
                Map<String, Map<String, Map<String, Object>>> schemaFields = new HashMap<>();
                List<Field> fields = Stream.of(jsonSchemedClass.getDeclaredFields())
                        .filter(field -> !Modifier.isFinal(field.getModifiers()))
                        .collect(Collectors.toList());
                for (Field field : fields) {
                    Map<String, Map<String, Object>> maps = new HashMap<>();
                    schemaFields.put(field.getName(), maps);
                    Annotation[] fieldAnnotations = field.getDeclaredAnnotations();
                    for (Annotation fieldAnnotation : fieldAnnotations) {
                        if (javaxAnnotationClasses.contains(fieldAnnotation.annotationType().getName())) {
                            Method[] fieldAnnotationMethods = fieldAnnotation.annotationType().getMethods();
                            Map<String, Object> fieldAnnotationMap = new HashMap<>();
                            maps.put(fieldAnnotation.annotationType().getSimpleName(), fieldAnnotationMap);
                            for (Method fieldAnnotationMethod : fieldAnnotationMethods) {
                                if (MESSAGE_METHOD_NAME.equals(fieldAnnotationMethod.getName())) {
                                    fieldAnnotationMap.put(MESSAGE_METHOD_NAME, messageSource.apply((String) fieldAnnotationMethod.invoke(fieldAnnotation)));
                                }
                                if (!IGNORED_ANNOTATION_PROPERTIES.contains(fieldAnnotationMethod.getName())) {
                                    fieldAnnotationMap.put(fieldAnnotationMethod.getName(), fieldAnnotationMethod.invoke(fieldAnnotation));
                                }
                            }
                        }
                    }
                }
                fullConfiguration.put(entityName, new JsonBean(entityName, jsonSchemedClassAnnotation.description(), schemaFields));
            }
        }
        log.info("Json validation entity scheme was initialized");
        return new JsonBeanConfig(fullConfiguration);
    }

}
