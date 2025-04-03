package upbrella.be.user.validation

import javax.validation.Constraint
import javax.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [OnlyNumbersValidator::class])
annotation class OnlyNumbers(
    val message: String = "The field should contain only numbers",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)