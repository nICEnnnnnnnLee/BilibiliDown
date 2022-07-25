package nicelee.bilibili.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface Controller {
	
	String path(); // 不鼓励使用""，这会导致PathDealer自动列出url时出现意外的状况
	
	String note();// default "";
	
	String specificPath() default "";
	
	boolean matchAll() default true; // 为假时，仅仅match前缀 
	
}
