package com.razzzil.jsonbean.validation.service;

/**
 * Interface for creating localization service
 */
public interface MessageLocalizationResolver {

    Object getValidationMessage(String path);
}
