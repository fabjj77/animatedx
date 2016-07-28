package com.cs.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Joakim Gottzén
 */
public class NullOrNotEmptyStringValidator implements ConstraintValidator<NullOrNotEmpty, String> {
    @Override
    public void initialize(final NullOrNotEmpty constraintAnnotation) {
    }

    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        final boolean isValid = !value.trim().isEmpty();
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{com.cs.validation.NullOrNotEmptyString.message}").addConstraintViolation();
        }

        return isValid;
    }
}
