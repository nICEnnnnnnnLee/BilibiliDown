package nicelee.bilibili.annotations;

import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target({ TYPE, PACKAGE })
public @interface Bilibili {
	
	
	String name();
	
	String type() default "parser";
	
	String note() default "";
}
