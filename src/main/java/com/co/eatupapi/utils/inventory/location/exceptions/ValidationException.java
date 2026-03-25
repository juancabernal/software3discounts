package com.co.eatupapi.utils.inventory.location.exceptions;

import com.co.eatupapi.utils.inventory.product.exceptions.ApiException;

public class ValidationException extends ApiException {

    public ValidationException(String message) {
        super(message, "VALIDATION_ERROR");
    }
}