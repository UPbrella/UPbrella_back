package upbrella.be.user.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = OnlyNumbersValidator.class)
public @interface OnlyNumbers {
    String message() default "The field should contain only numbers";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}