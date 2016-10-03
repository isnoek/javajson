/**
 * 
 */
package com.esoxsolutions.javajson.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author dbxisn
 *
 */
@Retention(RUNTIME)
@Target(ElementType.TYPE)
public @interface JsonSerializationType {

	public JsonType JsonType() default JsonType.JSON;

	public String Schema() default "http://schema.org";
	
	public String Type() default "";
	
	public boolean ShowContext() default true;
	
}
