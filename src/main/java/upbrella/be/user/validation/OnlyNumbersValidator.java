package upbrella.be.user.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class OnlyNumbersValidator implements ConstraintValidator<OnlyNumbers, String> {

    @Override
    public void initialize(OnlyNumbers constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (value == null) return true;
        return value.matches("^\\d+$");
    }
}