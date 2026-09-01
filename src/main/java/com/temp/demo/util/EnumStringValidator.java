package com.temp.demo.util;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EnumStringValidator implements ConstraintValidator<EnumStringValue, String> {
    private final List<String> enumStringValues = new ArrayList<>();

    @Override
    public void initialize(EnumStringValue constraintAnnotation) {
        @SuppressWarnings("rawtypes")
        Enum[] enumConstants = constraintAnnotation.enumClass().getEnumConstants();
        for(@SuppressWarnings("rawtypes") Enum value : enumConstants) {
            enumStringValues.add(value.name());
        }
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return Objects.isNull(value) ? Boolean.TRUE : enumStringValues.contains(value);
    }
}
