package com.cs.validation;

import com.cs.payment.Money;
import com.cs.validation.MoneyAmount.Mode;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Joakim Gottzén
 */
public class MoneyValidator implements ConstraintValidator<MoneyAmount, Money> {
    private Mode mode;

    @Override
    public void initialize(final MoneyAmount constraintAnnotation) {
        mode = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(final Money value, final ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        boolean isValid = false;
        switch (mode) {
            case NON_NEGATIVE:
                isValid = value.getCents() >= 0;
        }

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{com.cs.validation.Money.message}").addConstraintViolation();
        }

        return isValid;
    }
}
