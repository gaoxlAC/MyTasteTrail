package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自动填充注解
 * @Author：xlg
 * @Date：2024/1/22 17:43
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)  // 运行时注解,因此可以通过反射机制获取注解信息。
public @interface AutoFill {
    /**
     * 数据库操作类型
     * @return
     */
    OperationType value();
}
