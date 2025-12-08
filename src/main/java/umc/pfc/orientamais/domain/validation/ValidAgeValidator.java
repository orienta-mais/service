package umc.pfc.orientamais.domain.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.Period;

public class ValidAgeValidator implements ConstraintValidator<ValidAge, LocalDate> {

  private int minAge;
  private int maxAge;

  @Override
  public void initialize(ValidAge constraintAnnotation) {
    this.minAge = constraintAnnotation.min();
    this.maxAge = constraintAnnotation.max();
  }

  @Override
  public boolean isValid(LocalDate birthDate, ConstraintValidatorContext context) {
    if (birthDate == null) {
      return true;
    }

    LocalDate now = LocalDate.now();

    if (birthDate.isAfter(now)) {
      context.disableDefaultConstraintViolation();
      context.buildConstraintViolationWithTemplate("Data de nascimento não pode ser no futuro").addConstraintViolation();
      return false;
    }

    int age = Period.between(birthDate, now).getYears();

    if (age < minAge) {
      context.disableDefaultConstraintViolation();
      context.buildConstraintViolationWithTemplate("Idade mínima é " + minAge + " anos").addConstraintViolation();
      return false;
    }

    if (age > maxAge) {
      context.disableDefaultConstraintViolation();
      context.buildConstraintViolationWithTemplate("Idade máxima é " + maxAge + " anos").addConstraintViolation();
      return false;
    }

    return true;
  }
}
