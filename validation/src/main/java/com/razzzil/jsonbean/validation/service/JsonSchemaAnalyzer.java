package com.razzzil.jsonbean.validation.service;

import com.razzzil.jsonbean.validation.annotation.JsonSchemed;
import com.razzzil.jsonbean.validation.controller.JsonSchemaController;
import com.razzzil.jsonbean.validation.model.configuration.SchemeConfiguration;
import com.razzzil.jsonbean.validation.model.jsonschema.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.reflections.Reflections;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.ApplicationContext;

import javax.validation.constraints.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.time.chrono.HijrahDate;
import java.time.chrono.JapaneseDate;
import java.time.chrono.MinguoDate;
import java.time.chrono.ThaiBuddhistDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.razzzil.jsonbean.validation.constant.SchemaConstants.JAVAX_ANNOTATIONS_SEARCH_PACKAGE;

/**
 * JsonSchema Analyzer Service
 */
@Slf4j
public class JsonSchemaAnalyzer {

    private static final List<Class<?>> DATE_CLASSES =
            List.of(Date.class, Calendar.class, Instant.class, LocalDate.class,
                    LocalDateTime.class, LocalTime.class, MonthDay.class, OffsetDateTime.class, OffsetTime.class,
                    Year.class,
                    YearMonth.class, ZonedDateTime.class, HijrahDate.class, JapaneseDate.class, MinguoDate.class,
                    ThaiBuddhistDate.class);

    /**
     * @param context Spring Application Context
     * @param jsonSchemaConfiguration Properties config
     * @param messageSource function to apply localization
     * @return Map<String, JsonRootSchema> full config
     */
    @SneakyThrows
    public static Map<String, JsonRootSchema> getJsonSchema(ApplicationContext context,
                                                            SchemeConfiguration.JsonSchemaConfiguration jsonSchemaConfiguration,
                                                            Function<String, String> messageSource) {
        Reflections javaxAnnotations = new Reflections(JAVAX_ANNOTATIONS_SEARCH_PACKAGE);
        Set<String> javaxAnnotationClasses = javaxAnnotations
                .getStore()
                .get("SubTypes")
                .get(Annotation.class.getCanonicalName());
        List<String> packages = AutoConfigurationPackages.get(context.getAutowireCapableBeanFactory());
        Map<String, JsonRootSchema> result = new HashMap<>();
        for (String packageName : packages) {
            Reflections reflections = new Reflections(packageName);
            Set<Class<?>> jsonSchemedClasses = reflections.getTypesAnnotatedWith(JsonSchemed.class);
            for (Class<?> jsonSchemedClass : jsonSchemedClasses) {
                JsonSchemed jsonSchemedClassAnnotation = jsonSchemedClass.getAnnotation(JsonSchemed.class);
                if (jsonSchemedClass.isEnum()) {
                    EnumJsonRootSchema enumJsonRootSchema = new EnumJsonRootSchema();
                    String entityName = getEntityName(jsonSchemedClassAnnotation, jsonSchemedClass);
                    List<String> enumValues = Arrays.stream(jsonSchemedClass.getEnumConstants())
                            .map(Object::toString)
                            .collect(Collectors.toList());
                    enumJsonRootSchema.setEnumValues(enumValues);
                    enumJsonRootSchema.setSchema(jsonSchemedClassAnnotation.schema());
                    enumJsonRootSchema.setTitle(entityName);
                    enumJsonRootSchema.setId(
                            jsonSchemaConfiguration.getUrl() + JsonSchemaController.URI + "/" + entityName);
                    enumJsonRootSchema.setDescription(jsonSchemedClassAnnotation.description());
                } else {
                    processJsonSchemedClass(jsonSchemedClassAnnotation, jsonSchemedClass, jsonSchemaConfiguration,
                            messageSource, javaxAnnotationClasses, result);
                }
            }
        }
        log.info("Json validation entity scheme was initialized");
        return result;
    }

    private static void processJsonSchemedClass(JsonSchemed jsonSchemedClassAnnotation,
                                                Class<?> jsonSchemedClass,
                                                SchemeConfiguration.JsonSchemaConfiguration jsonSchemaConfiguration,
                                                Function<String, String> messageSource,
                                                Set<String> javaxAnnotationClasses,
                                                Map<String, JsonRootSchema> result) {
        String entityName = getEntityName(jsonSchemedClassAnnotation, jsonSchemedClass);
        if (!result.containsKey(entityName)) {
            JsonRootSchema jsonRootSchema =
                    getSchema(entityName, jsonSchemedClassAnnotation, jsonSchemedClass, jsonSchemaConfiguration,
                            messageSource, javaxAnnotationClasses, result);
            result.put(jsonRootSchema.getTitle(), jsonRootSchema);
        }
    }

    private static JsonRootSchema getSchema(String entityName,
                                            JsonSchemed jsonSchemedClassAnnotation,
                                            Class<?> jsonSchemedClass,
                                            SchemeConfiguration.JsonSchemaConfiguration jsonSchemaConfiguration,
                                            Function<String, String> messageSource,
                                            Set<String> javaxAnnotationClasses,
                                            Map<String, JsonRootSchema> result) {
        List<Field> fields = Stream.of(jsonSchemedClass.getDeclaredFields())
                .filter(field -> !Modifier.isFinal(field.getModifiers()))
                .collect(Collectors.toList());
        Map<String, JsonSchemaProperty> properties = new HashMap<>();
        List<String> required = new ArrayList<>();
        JsonRootSchema jsonRootSchema = new JsonRootSchema();
        jsonRootSchema.setProperties(properties);
        jsonRootSchema.setTitle(entityName);
        jsonRootSchema.setId(jsonSchemaConfiguration.getUrl() + JsonSchemaController.URI + "/" + entityName);
        if (Objects.nonNull(jsonSchemedClassAnnotation)) {
            jsonRootSchema.setSchema(jsonSchemedClassAnnotation.schema());
            jsonRootSchema.setDescription(jsonSchemedClassAnnotation.description());
        }
        for (Field field : fields) {
            Map<Class<?>, Annotation> fieldAnnotations = Stream.of(field.getDeclaredAnnotations())
                    .filter(annotation -> javaxAnnotationClasses.contains(annotation.annotationType().getName()))
                    .collect(Collectors.toMap(Annotation::annotationType, Function.identity()));
            List<JsonSchemaProperty> fieldProperties = new ArrayList<>();
            if (fieldHasAnyType(field, BigInteger.class, Integer.class, Byte.class, Long.class, Short.class, int.class,
                    byte.class, long.class, short.class)) {
                BigIntegerJsonSchemaProperty bigIntegerJsonSchemaProperty = new BigIntegerJsonSchemaProperty();
                for (Class<?> annotationClass : fieldAnnotations.keySet()) {
                    if (annotationClass.equals(Min.class)) {
                        Min min = (Min) fieldAnnotations.get(annotationClass);
                        bigIntegerJsonSchemaProperty.setMinimum(BigInteger.valueOf(min.value()));
                        bigIntegerJsonSchemaProperty.appendDescription(messageSource.apply(min.message()));
                    }
                    if (annotationClass.equals(Max.class)) {
                        Max max = (Max) fieldAnnotations.get(annotationClass);
                        bigIntegerJsonSchemaProperty.setMaximum(BigInteger.valueOf(max.value()));
                        bigIntegerJsonSchemaProperty.appendDescription(messageSource.apply(max.message()));
                    }
                    if (annotationClass.equals(Positive.class)) {
                        Positive positive = (Positive) fieldAnnotations.get(annotationClass);
                        bigIntegerJsonSchemaProperty.setMinimum(BigInteger.ZERO);
                        bigIntegerJsonSchemaProperty.appendDescription(messageSource.apply(positive.message()));
                    }
                    if (annotationClass.equals(PositiveOrZero.class)) {
                        PositiveOrZero positiveOrZero = (PositiveOrZero) fieldAnnotations.get(annotationClass);
                        bigIntegerJsonSchemaProperty.setExclusiveMinimum(BigInteger.ZERO);
                        bigIntegerJsonSchemaProperty.appendDescription(messageSource.apply(positiveOrZero.message()));
                    }
                    if (annotationClass.equals(Negative.class)) {
                        Negative negative = (Negative) fieldAnnotations.get(annotationClass);
                        bigIntegerJsonSchemaProperty.setMaximum(BigInteger.ZERO);
                        bigIntegerJsonSchemaProperty.appendDescription(messageSource.apply(negative.message()));
                    }
                    if (annotationClass.equals(NegativeOrZero.class)) {
                        NegativeOrZero negativeOrZero = (NegativeOrZero) fieldAnnotations.get(annotationClass);
                        bigIntegerJsonSchemaProperty.setExclusiveMaximum(BigInteger.ZERO);
                        bigIntegerJsonSchemaProperty.appendDescription(messageSource.apply(negativeOrZero.message()));
                    }
                    if (annotationClass.equals(DecimalMin.class)) {
                        DecimalMin min = (DecimalMin) fieldAnnotations.get(annotationClass);
                        if (min.inclusive()) {
                            bigIntegerJsonSchemaProperty.setMinimum(NumberUtils.createBigInteger(min.value()));
                        } else {
                            bigIntegerJsonSchemaProperty.setExclusiveMaximum(NumberUtils.createBigInteger(min.value()));
                        }
                        bigIntegerJsonSchemaProperty.appendDescription(messageSource.apply(min.message()));
                    }
                    if (annotationClass.equals(DecimalMax.class)) {
                        DecimalMax max = (DecimalMax) fieldAnnotations.get(annotationClass);
                        if (max.inclusive()) {
                            bigIntegerJsonSchemaProperty.setMaximum(NumberUtils.createBigInteger(max.value()));
                        } else {
                            bigIntegerJsonSchemaProperty.setExclusiveMaximum(NumberUtils.createBigInteger(max.value()));
                        }
                        bigIntegerJsonSchemaProperty.appendDescription(messageSource.apply(max.message()));
                    }
                }
                fieldProperties.add(bigIntegerJsonSchemaProperty);
            } else if (fieldHasAnyType(field, BigDecimal.class, Double.class, Float.class, double.class, float.class)) {
                BigDecimalJsonSchemaProperty bigDecimalJsonSchemaProperty = new BigDecimalJsonSchemaProperty();
                for (Class<?> annotationClass : fieldAnnotations.keySet()) {
                    if (annotationClass.equals(Min.class)) {
                        Min min = (Min) fieldAnnotations.get(annotationClass);
                        bigDecimalJsonSchemaProperty.setMinimum(BigDecimal.valueOf(min.value()));
                        bigDecimalJsonSchemaProperty.appendDescription(messageSource.apply(min.message()));
                    }
                    if (annotationClass.equals(Max.class)) {
                        Max max = (Max) fieldAnnotations.get(annotationClass);
                        bigDecimalJsonSchemaProperty.setMaximum(BigDecimal.valueOf(max.value()));
                        bigDecimalJsonSchemaProperty.appendDescription(messageSource.apply(max.message()));
                    }
                    if (annotationClass.equals(Positive.class)) {
                        Positive positive = (Positive) fieldAnnotations.get(annotationClass);
                        bigDecimalJsonSchemaProperty.setMinimum(BigDecimal.ZERO);
                        bigDecimalJsonSchemaProperty.appendDescription(messageSource.apply(positive.message()));
                    }
                    if (annotationClass.equals(PositiveOrZero.class)) {
                        PositiveOrZero positiveOrZero = (PositiveOrZero) fieldAnnotations.get(annotationClass);
                        bigDecimalJsonSchemaProperty.setExclusiveMinimum(BigDecimal.ZERO);
                        bigDecimalJsonSchemaProperty.appendDescription(messageSource.apply(positiveOrZero.message()));
                    }
                    if (annotationClass.equals(Negative.class)) {
                        Negative negative = (Negative) fieldAnnotations.get(annotationClass);
                        bigDecimalJsonSchemaProperty.setMaximum(BigDecimal.ZERO);
                        bigDecimalJsonSchemaProperty.appendDescription(messageSource.apply(negative.message()));
                    }
                    if (annotationClass.equals(NegativeOrZero.class)) {
                        NegativeOrZero negativeOrZero = (NegativeOrZero) fieldAnnotations.get(annotationClass);
                        bigDecimalJsonSchemaProperty.setExclusiveMaximum(BigDecimal.ZERO);
                        bigDecimalJsonSchemaProperty.appendDescription(messageSource.apply(negativeOrZero.message()));
                    }
                    if (annotationClass.equals(DecimalMin.class)) {
                        DecimalMin min = (DecimalMin) fieldAnnotations.get(annotationClass);
                        if (min.inclusive()) {
                            bigDecimalJsonSchemaProperty.setMinimum(NumberUtils.createBigDecimal(min.value()));
                        } else {
                            bigDecimalJsonSchemaProperty.setExclusiveMaximum(NumberUtils.createBigDecimal(min.value()));
                        }
                        bigDecimalJsonSchemaProperty.appendDescription(messageSource.apply(min.message()));
                    }
                    if (annotationClass.equals(DecimalMax.class)) {
                        DecimalMax max = (DecimalMax) fieldAnnotations.get(annotationClass);
                        if (max.inclusive()) {
                            bigDecimalJsonSchemaProperty.setMaximum(NumberUtils.createBigDecimal(max.value()));
                        } else {
                            bigDecimalJsonSchemaProperty.setExclusiveMaximum(NumberUtils.createBigDecimal(max.value()));
                        }
                        bigDecimalJsonSchemaProperty.appendDescription(messageSource.apply(max.message()));
                    }
                }
                fieldProperties.add(bigDecimalJsonSchemaProperty);
            } else if (fieldHasAnyType(field, CharSequence.class)) {
                Optional<BigDecimalJsonSchemaProperty> bigDecimalJsonSchemaPropertyCandidate = Optional.empty();
                Optional<StringJsonSchemaProperty> stringJsonSchemaPropertyCandidate = Optional.empty();
                for (Class<?> annotationClass : fieldAnnotations.keySet()) {
                    if (annotationClass.equals(DecimalMin.class)) {
                        DecimalMin min = (DecimalMin) fieldAnnotations.get(annotationClass);
                        BigDecimalJsonSchemaProperty bdjsp = bigDecimalJsonSchemaPropertyCandidate.orElse(
                                new BigDecimalJsonSchemaProperty());
                        if (min.inclusive()) {
                            bdjsp.setMinimum(NumberUtils.createBigDecimal(min.value()));
                        } else {
                            bdjsp.setExclusiveMaximum(NumberUtils.createBigDecimal(min.value()));
                        }
                        bdjsp.appendDescription(messageSource.apply(min.message()));
                        bigDecimalJsonSchemaPropertyCandidate = Optional.of(bdjsp);
                    }
                    if (annotationClass.equals(DecimalMax.class)) {
                        DecimalMax max = (DecimalMax) fieldAnnotations.get(annotationClass);
                        BigDecimalJsonSchemaProperty bdjsp = bigDecimalJsonSchemaPropertyCandidate.orElse(
                                new BigDecimalJsonSchemaProperty());
                        if (max.inclusive()) {
                            bdjsp.setMaximum(NumberUtils.createBigDecimal(max.value()));
                        } else {
                            bdjsp.setExclusiveMaximum(NumberUtils.createBigDecimal(max.value()));
                        }
                        bdjsp.appendDescription(messageSource.apply(max.message()));
                        bigDecimalJsonSchemaPropertyCandidate = Optional.of(bdjsp);
                    }
                    if (annotationClass.equals(Size.class)) {
                        Size size = (Size) fieldAnnotations.get(annotationClass);
                        StringJsonSchemaProperty sjsp = stringJsonSchemaPropertyCandidate.orElse(
                                new StringJsonSchemaProperty());
                        sjsp.setMaxLength(size.max());
                        sjsp.setMinLength(size.min());
                        sjsp.appendDescription(messageSource.apply(size.message()));
                        stringJsonSchemaPropertyCandidate = Optional.of(sjsp);
                    }
                    if (annotationClass.equals(Pattern.class)) {
                        Pattern pattern = (Pattern) fieldAnnotations.get(annotationClass);
                        StringJsonSchemaProperty sjsp = stringJsonSchemaPropertyCandidate.orElse(
                                new StringJsonSchemaProperty());
                        sjsp.setPattern(pattern.regexp());
                        sjsp.appendDescription(messageSource.apply(pattern.message()));
                        stringJsonSchemaPropertyCandidate = Optional.of(sjsp);
                    }
                    if (annotationClass.equals(Email.class)) {
                        Email email = (Email) fieldAnnotations.get(annotationClass);
                        StringJsonSchemaProperty sjsp = stringJsonSchemaPropertyCandidate.orElse(
                                new StringJsonSchemaProperty());
                        sjsp.setPattern(email.regexp());
                        sjsp.appendDescription(messageSource.apply(email.message()));
                        stringJsonSchemaPropertyCandidate = Optional.of(sjsp);
                    }
                    if (annotationClass.equals(NotBlank.class)) {
                        NotBlank notBlank = (NotBlank) fieldAnnotations.get(annotationClass);
                        StringJsonSchemaProperty sjsp = stringJsonSchemaPropertyCandidate.orElse(
                                new StringJsonSchemaProperty());
                        sjsp.setMinLength(1);
                        sjsp.appendDescription(messageSource.apply(notBlank.message()));
                        stringJsonSchemaPropertyCandidate = Optional.of(sjsp);
                    }
                }
                if (bigDecimalJsonSchemaPropertyCandidate.isPresent()) {
                    fieldProperties.add(bigDecimalJsonSchemaPropertyCandidate.get());
                } else if (stringJsonSchemaPropertyCandidate.isPresent()) {
                    fieldProperties.add(stringJsonSchemaPropertyCandidate.get());
                } else {
                    fieldProperties.add(new StringJsonSchemaProperty());
                }
            } else if (fieldHasAnyType(field, DATE_CLASSES)) {
                StringJsonSchemaProperty stringJsonSchemaProperty = new StringJsonSchemaProperty();
                for (Class<?> annotationClass : fieldAnnotations.keySet()) {
                    if (annotationClass.equals(Past.class)) {
                        Past past = (Past) fieldAnnotations.get(annotationClass);
                        stringJsonSchemaProperty.setFormat(Format.DATETIME);
                        stringJsonSchemaProperty.appendDescription(messageSource.apply(past.message()));
                    }
                    if (annotationClass.equals(PastOrPresent.class)) {
                        PastOrPresent pastOrPresent = (PastOrPresent) fieldAnnotations.get(annotationClass);
                        stringJsonSchemaProperty.setFormat(Format.DATETIME);
                        stringJsonSchemaProperty.appendDescription(messageSource.apply(pastOrPresent.message()));
                    }
                    if (annotationClass.equals(Future.class)) {
                        Future future = (Future) fieldAnnotations.get(annotationClass);
                        stringJsonSchemaProperty.setFormat(Format.DATETIME);
                        stringJsonSchemaProperty.appendDescription(messageSource.apply(future.message()));
                    }
                    if (annotationClass.equals(FutureOrPresent.class)) {
                        FutureOrPresent futureOrPresent = (FutureOrPresent) fieldAnnotations.get(annotationClass);
                        stringJsonSchemaProperty.setFormat(Format.DATETIME);
                        stringJsonSchemaProperty.appendDescription(messageSource.apply(futureOrPresent.message()));
                    }
                }
                fieldProperties.add(stringJsonSchemaProperty);
            } else if (fieldHasAnyType(field, Boolean.class)) {
                BooleanJsonSchemaProperty booleanJsonSchemaProperty = new BooleanJsonSchemaProperty();
                for (Class<?> annotationClass : fieldAnnotations.keySet()) {
                    if (annotationClass.equals(AssertTrue.class)) {
                        //todo
                    }
                    if (annotationClass.equals(AssertFalse.class)) {
                        //todo
                    }
                }
                fieldProperties.add(booleanJsonSchemaProperty);
            } else if (fieldHasAnyType(field, Enum.class)) {
                EnumJsonSchemaProperty enumJsonSchemaProperty = new EnumJsonSchemaProperty();
                List<String> enumValues = Arrays.stream(field.getType().getEnumConstants())
                        .map(Object::toString)
                        .collect(Collectors.toList());
                enumJsonSchemaProperty.setEnumValues(enumValues);
                fieldProperties.add(enumJsonSchemaProperty);
            } else if (field.getType().isArray() || fieldHasAnyType(field, Collection.class)) {
                ArrayJsonSchemaProperty arrayJsonSchemaProperty = new ArrayJsonSchemaProperty();
                for (Class<?> annotationClass : fieldAnnotations.keySet()) {
                    if (annotationClass.equals(NotEmpty.class)) {
                        NotEmpty notEmpty = (NotEmpty) fieldAnnotations.get(annotationClass);
                        arrayJsonSchemaProperty.setMinItems(1);
                        arrayJsonSchemaProperty.appendDescription(messageSource.apply(notEmpty.message()));
                    }
//                    if (annotationClass.equals(AssertFalse.List.class) ||
//                            annotationClass.equals(AssertTrue.List.class) ||
//                            annotationClass.equals(DecimalMax.List.class) ||
//                            annotationClass.equals(DecimalMin.List.class) ||
//                            annotationClass.equals(Digits.List.class) ||
//                            annotationClass.equals(Email.List.class) ||
//                            annotationClass.equals(Future.List.class) ||
//                            annotationClass.equals(FutureOrPresent.List.class) ||
//                            annotationClass.equals(Max.List.class) ||
//                            annotationClass.equals(Min.List.class) ||
//                            annotationClass.equals(Negative.List.class) ||
//                            annotationClass.equals(NegativeOrZero.List.class) ||
//                            annotationClass.equals(NotBlank.List.class) ||
//                            annotationClass.equals(NotEmpty.List.class) ||
//                            annotationClass.equals(Null.List.class) ||
//                            annotationClass.equals(Past.List.class) ||
//                            annotationClass.equals(PastOrPresent.List.class) ||
//                            annotationClass.equals(Pattern.List.class) ||
//                            annotationClass.equals(Positive.List.class) ||
//                            annotationClass.equals(PositiveOrZero.List.class) ||
//                            annotationClass.equals(Size.List.class)) {
//
//                        if (field.getType().isArray()) {
//                            Class<?> componentJsonArrayType = field.getType().getComponentType();
//                            JsonSchemed nestedSchemedClassAnnotation =
//                                    componentJsonArrayType.getAnnotation(JsonSchemed.class);
//                            String nestedEntityName =
//                                    getEntityName(nestedSchemedClassAnnotation, componentJsonArrayType);
//                            JsonRootSchema item =
//                                    getSchema(nestedEntityName, nestedSchemedClassAnnotation, componentJsonArrayType,
//                                            jsonSchemaConfiguration, messageSource, javaxAnnotationClasses, result);
//                            arrayJsonSchemaProperty.setItems(Collections.singletonList(item));
//                        }
//                        if (fieldHasAnyType(field, Collection.class)) {
//                            if (field.getGenericType() instanceof ParameterizedType) {
//                                ParameterizedType aType = (ParameterizedType) field.getGenericType();
//                                Arrays.stream(aType.getActualTypeArguments())
//                                        .findAny()
//                                        .ifPresent(type -> {
//                                            Class<?> componentJsonArrayType = type.getClass();
//                                            JsonSchemed nestedSchemedClassAnnotation = componentJsonArrayType.getAnnotation(JsonSchemed.class);
//                                            String nestedEntityName =
//                                                    getEntityName(nestedSchemedClassAnnotation, componentJsonArrayType);
//                                            JsonRootSchema item =
//                                                    getSchema(nestedEntityName, nestedSchemedClassAnnotation, componentJsonArrayType,
//                                                            jsonSchemaConfiguration, messageSource, javaxAnnotationClasses, result);
//                                            arrayJsonSchemaProperty.setItems(Collections.singletonList(item));
//                                        });
//                            }
//                        }
//                    }
                }
                //todo
                fieldProperties.add(arrayJsonSchemaProperty);
            } else {
                ObjectJsonSchemaProperty objectJsonSchemaProperty = new ObjectJsonSchemaProperty();
                JsonSchemed refJsonSchemedClassAnnotation = field.getType().getAnnotation(JsonSchemed.class);
                Class<?> refJsonSchemedClass = field.getType();
                if (Objects.nonNull(refJsonSchemedClassAnnotation)) {
                    processJsonSchemedClass(jsonSchemedClassAnnotation, refJsonSchemedClass, jsonSchemaConfiguration,
                            messageSource, javaxAnnotationClasses, result);
                    String nestedEntityName = getEntityName(refJsonSchemedClassAnnotation, refJsonSchemedClass);
                    objectJsonSchemaProperty.setRefs(
                            jsonSchemaConfiguration.getUrl() + JsonSchemaController.URI + "/" + nestedEntityName);
                } else {
                    throw new IllegalArgumentException(
                            String.format("Field (Name: %s, Class: %s; Type: %s) is not annotated with @JsonSchemed",
                                    field.getName(), jsonSchemedClass.getCanonicalName(),
                                    field.getType().getCanonicalName()));
                }
                fieldProperties.add(objectJsonSchemaProperty);
            }
            boolean nullable = fieldAnnotations.keySet()
                    .stream()
                    .noneMatch(annotationClass -> annotationClass.equals(NotNull.class));
            if (nullable) {
                fieldProperties.add(new NullJsonSchemaProperty());
            } else {
                required.add(field.getName());
            }
            for (Class<?> annotationClass : fieldAnnotations.keySet()) {
                if (annotationClass.equals(Null.class)) {
                    Null aNull = (Null) fieldAnnotations.get(annotationClass);
                    NullJsonSchemaProperty nullJsonSchemaProperty = new NullJsonSchemaProperty();
                    nullJsonSchemaProperty.appendDescription(messageSource.apply(aNull.message()));
                    fieldProperties = new ArrayList<>();
                    fieldProperties.add(nullJsonSchemaProperty);
                }
            }
            if (fieldProperties.size() > 1) {
                IgnoredTypeObjectJsonSchemaProperty ignoredTypeObjectJsonSchemaProperty =
                        new IgnoredTypeObjectJsonSchemaProperty();
                ignoredTypeObjectJsonSchemaProperty.setAnyOf(fieldProperties);
                properties.put(field.getName(), ignoredTypeObjectJsonSchemaProperty);
            } else {
                properties.put(field.getName(), fieldProperties.get(0));
            }
        }
        jsonRootSchema.setRequired(required);
        return jsonRootSchema;
    }

    private static String getEntityName(JsonSchemed jsonSchemedClassAnnotation,
                                        Class<?> jsonSchemedClass) {
        return "".equals(jsonSchemedClassAnnotation.title()) ?
                jsonSchemedClass.getSimpleName() :
                jsonSchemedClassAnnotation.title();
    }

    private static boolean fieldHasAnyType(Field field,
                                           Class<?>... classes) {
        return Arrays.stream(classes)
                .anyMatch(clazz -> clazz.isAssignableFrom(field.getType()));
    }

    private static boolean fieldHasAnyType(Field field,
                                           List<Class<?>> classes) {
        return classes.stream()
                .anyMatch(clazz -> clazz.isAssignableFrom(field.getType()));
    }

}
