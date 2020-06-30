package com.example.application;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 * @ClassName AOPDemo
 * @Description: AOPDemo 类（或接口）是
 * @Author: zhonghanbang
 * @Date: 2020/6/2914:02
 */

@Aspect
@Component
public class AOPDemo {

    /**
     * 定义切入点
     */
    @Pointcut("execution(public * com.example.controller.*.*(*))")
    public void AOPDemo(){
    }

    /**
     * 在连接点之前执行
     */
    // @Before("AOPDemo()")
    public void doBefore(){

    }

    /**
     * 在连接点执行完后执行，无论是否抛出异常，类似finally
     */
    // @After("AOPDemo()")
    public void doAfter(){

    }

    /**
     *  在连接点执行完后执行
     */
    // @AfterReturning
    public void doAfterReturning(){

    }

    /**
     *  在连接点执行时抛出异常（指定）后执行
     */
    // @AfterThrowing
    public void doAfterThrowing(){

    }

    /**
     * 环绕通知
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("AOPDemo()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable{
        long startTime=System.currentTimeMillis();
        try{
            return joinPoint.proceed();
        }finally{
            System.out.println("耗时:"+(System.currentTimeMillis()-startTime));
        }
    }
}
