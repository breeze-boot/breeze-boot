/*
 * Copyright (c) 2021-2022, gaoweixuan (breeze-cloud@foxmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.breeze.boot.log.config;

import com.breeze.boot.log.annotation.SysLog;
import com.breeze.boot.log.dto.SysLogDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * 系统日志方面
 *
 * @author breeze
 * @date 2022-10-19
 */
@Slf4j
@Aspect
@EnableAspectJAutoProxy
public class SysLogAspect {

    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.registerModule(new JavaTimeModule());
    }

    /**
     * 发布保存系统的日志事件
     */
    @Autowired
    private PublisherSaveSysLogEvent publisherSaveSysLogEvent;

    /**
     * AOP 切点
     */
    @Pointcut("@annotation(com.breeze.boot.log.annotation.SysLog)")
    public void logPointcut() {
    }

    /**
     * 在
     * 处理完请求后执行此处代码
     *
     * @param joinPoint 切点
     * @return {@link Object}
     */
    @SneakyThrows
    @Around(value = "logPointcut()")
    public Object doAround(ProceedingJoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        HttpServletRequest request = attributes.getRequest();
        log.info("HTTP_METHOD : {} , URL {} , IP : {}", request.getMethod(), request.getRequestURL(), request.getRemoteAddr());
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 方法名称
        String methodName = signature.getDeclaringTypeName() + "." + signature.getName();
        // 方法
        Method method = signature.getMethod();
        // 入参
        Object[] param = joinPoint.getArgs();
        String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(param);
        // 注解
        SysLog sysLog = method.getAnnotation(SysLog.class);

        log.info("进入方法 [{}], \n 传入参数：\n {}", methodName, jsonString);

        SysLogDTO build = SysLogDTO.builder()
                .systemModule("--")
                .logTitle(sysLog.description())
                .system("")
                .doType(sysLog.type().getCode())
                .logType(0)
                .browser(request.getRemoteAddr())
                .ip(request.getRemoteAddr())
                .requestType(request.getMethod())
                .content(jsonString)
                .result(1)
                .build();
        // 发布
        this.publisherSaveSysLogEvent.publisherEvent(new SysLogSaveEvent(build));
        Object proceed = joinPoint.proceed();
        log.info("方法[{}]执行结束, \n 返回值：\n {}", methodName, mapper.writerWithDefaultPrettyPrinter().writeValueAsString(proceed));
        return proceed;
    }

    /**
     * 如果处理请求时出现异常
     * 抛出异常后执行
     *
     * @param joinPoint 切点
     * @param e         异常
     */
    @AfterThrowing(value = "@annotation(sysLog)", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, SysLog sysLog, Exception e) {
    }

}
