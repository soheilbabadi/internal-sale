package com.nicico.internal.sales.util;

import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDateRange {
	String message() default "End date must be equal or after start date";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	String startField();

	String endField();
}