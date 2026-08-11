package com.nicico.internal.sales.util.nationalcode;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = IranianNationalCodeValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface IranianNationalCode {
	String message() default "کد ملی وارده معتبر نیست";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}