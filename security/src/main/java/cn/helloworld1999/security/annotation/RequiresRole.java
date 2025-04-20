package cn.helloworld1999.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义角色权限注解
 * 用于方法级别的权限控制
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresRole {
    /**
     * 需要的角色
     */
    String value();
    
    /**
     * 逻辑类型：AND 表示需要同时具有所有角色，OR 表示具有任一角色即可
     */
    String logical() default "AND";
}
