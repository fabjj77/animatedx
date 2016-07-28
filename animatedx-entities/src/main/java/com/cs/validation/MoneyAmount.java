package com.cs.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Joakim Gottzén
 */
@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = MoneyValidator.class)
@Documented
public @interface MoneyAmount {
    Mode value() default Mode.NON_NEGATIVE;

    String message() default "com.cs.validation.Money.nonNegativeAmount";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    enum Mode {
        NON_NEGATIVE
    }
}
