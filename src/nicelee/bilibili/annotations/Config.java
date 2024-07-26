package nicelee.bilibili.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(FIELD)
public @interface Config {
	
	
	String key();					// key - e.g. bilibili.name.format
	
	String defaultValue();			// 默认值
	
	String note() default "";		// 用于配置页的提示
	
	String pathType() default "";	// 针对文件/文件夹字符串类型， dir 表示文件夹， file 表示文件
	
	int multiply() default 1; 		// 类型为数值的时候，对其进行乘法操作
	
	String eq_true() default "true";// 针对bool类型, 只有值与其相等(忽略大小写)时为true
	
	String[] valids() default {};	// 值可能的取值范围(忽略大小写)，如果不为空会进行检查
	
	boolean warning() default true; // 对于不合法的值进行警告
}
