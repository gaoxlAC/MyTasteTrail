package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bytecode.constant.MethodConstant;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 自动填充切面
 * @Author：xlg
 * @Date：2024/1/22 17:56
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    /**
     * 自动填充切点，拦截对应的方法
     * 选择了 com.sky.mapper 包下的任何类的任何方法作为切入点
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){}

    /**
     * 自动填充前置通知
     * @param joinPoint
     */
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint){
        log.info("自动填充前置通知...");

        // 获取方法签名对象
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        // 获取方法上的注解
        AutoFill autoFill = methodSignature.getMethod().getAnnotation(AutoFill.class);

        // 获得注解中的操作类型
        OperationType operationType = autoFill.value();

        // 获取方法的参数
        Object[] args = joinPoint.getArgs();
        if(args == null || args.length == 0){
            return;
        }

        // 实体对象
        Object entity = args[0];

        // 准备赋值的数据
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        // 根据操作类型进行赋值
        if(operationType == OperationType.INSERT){
            // 新增操作
            try {
                // 获取set方法对象并通过反射机制设置值
                entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class).invoke(entity, now);
                entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class).invoke(entity, now);

                entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class).invoke(entity, currentId);
                entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class).invoke(entity, currentId);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            // 更新操作
            try {
                // 获取set方法对象并通过反射机制设置值
                entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class).invoke(entity, now);
                entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class).invoke(entity, currentId);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
