package com.esoxsolutions.javajson.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Repeatable(JsonContexts.class)
@Retention(RUNTIME)
@Target(ElementType.TYPE)
public @interface JsonContext {

	public String Name() default "";
	public String URL() default "";
}
