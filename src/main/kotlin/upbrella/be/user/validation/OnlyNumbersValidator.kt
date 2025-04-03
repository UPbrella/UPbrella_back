package upbrella.be.user.validation

import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class OnlyNumbersValidator : ConstraintValidator<OnlyNumbers, String> {

    override fun initialize(constraintAnnotation: OnlyNumbers) {
    }

    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
        if (value == null) return true
        return value.matches(Regex("^\\d+$"))
    }
}