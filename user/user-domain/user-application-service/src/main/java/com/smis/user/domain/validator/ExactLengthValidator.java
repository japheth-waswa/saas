package com.smis.user.domain.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ExactLengthValidator implements ConstraintValidator<ExactLength,Long> {
    private int length;
    @Override
    public void initialize(ExactLength constraintAnnotation) {
        this.length = constraintAnnotation.length();
    }

    @Override
    public boolean isValid(Long aLong, ConstraintValidatorContext constraintValidatorContext) {
        if(aLong == null)return true;
        return String.valueOf(aLong).length() == length;
    }
}
