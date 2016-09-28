/**
 * 
 */
package com.esoxsolutions.javajson.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(FIELD)
/**
 * @author Iede Snoek
 * 
 *
 */
public @interface JsonSerializable {

	public String JsonFieldName() default "";
	public String JsonType() default "";
	public String Id() default "";
	public String Container() default "";
}
