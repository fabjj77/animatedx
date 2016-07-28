package com.cs.rest.status;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import org.hibernate.exception.ConstraintViolationException;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.HashMap;
import java.util.Map;

import static com.cs.rest.status.StatusCode.INVALID_ARGUMENT;

/**
 * @author Omid Alaepour.
 */
public class ValidationMessage extends ErrorMessage {

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @XmlElementWrapper(name = "errors")
    private final Map<String, String> errors = new HashMap<>();

    private ValidationMessage(final String message) {
        this(message, new HashMap<String, String>());
    }

    private ValidationMessage(final String message, @Nonnull final Map<String, String> errors) {
        super(INVALID_ARGUMENT, message);
        this.errors.putAll(errors);
    }

    public static ValidationMessage of(final MethodArgumentNotValidException exception) {
        final Map<String, String> validationMessages = new HashMap<>();
        final BindingResult result = exception.getBindingResult();

        // process the field validations
        for (final FieldError fieldError : result.getFieldErrors()) {
            validationMessages.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        // process the global validations
        for (final ObjectError globalError : result.getGlobalErrors()) {
            validationMessages.put(globalError.getObjectName(), globalError.getDefaultMessage());
        }

        return new ValidationMessage("Validation error", validationMessages);
    }

    public static ValidationMessage of(final ConstraintViolationException exception) {
        final String validationMessages = exception.getConstraintName();
        return new ValidationMessage(validationMessages);
    }
}
