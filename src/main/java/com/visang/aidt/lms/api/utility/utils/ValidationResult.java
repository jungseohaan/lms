package com.visang.aidt.lms.api.utility.utils;

import lombok.Getter;

@Getter
public class ValidationResult {
    private final boolean isValid;
    private final String message;
    private final Object data;

    private ValidationResult(boolean isValid, String message, Object data) {
        this.isValid = isValid;
        this.message = message;
        this.data = data;
    }
    
    public static ValidationResult success(Object data) {
        return new ValidationResult(true, "성공", data);
    }

    public static ValidationResult fail(String message) {
        return new ValidationResult(false, message, null);
    }
}