package com.razzzil.jsonbean.validation.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Add this annotation to DTO with javax.validation annotations to enable schema for this DTO
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface JsonSchemed {

    /**
     * @return Json Schema URL.
     */
    String schema();

    /**
     * @return Json Schema Description
     */
    String description();

    /**
     * @return Json Schema Model title; If not set, the name of class would be used
     */
    String title() default "";
}
