package cn.helloworld1999.synology.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
/*
@TODO 这里要加一个测试sid有效性的功能，不然调用一次登录一次 太不合理了
 */
public @interface AutoLogin {
}
