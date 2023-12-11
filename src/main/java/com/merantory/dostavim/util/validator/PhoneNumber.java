package com.merantory.dostavim.util.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhoneNumberValidator.class)
public @interface PhoneNumber {
    String message() default "Ошибка валидации номера телефона, проверьте корректность ввода. Пример: 75552305555";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
