package umc.pfc.orientamais.domain.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/** Validates that a person's age is within acceptable range (minimum and maximum age). */
@Documented
@Constraint(validatedBy = ValidAgeValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAge {

  String message() default "Idade inválida";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  int min() default 16;

  int max() default 100;
}
