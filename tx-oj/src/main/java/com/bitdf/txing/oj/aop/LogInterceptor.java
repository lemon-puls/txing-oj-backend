package com.bitdf.txing.oj.aop;

import cn.hutool.core.util.StrUtil;
import com.bitdf.txing.oj.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * 请求响应日志 AOP
 *
 * @author Lizhiwei
 * @date 2023/1/24 3:44:13
 * 注释：
 **/
@Aspect
@Component
@Slf4j
public class LogInterceptor {

    @Autowired
    LoginService loginService;

    /**
     * 执行拦截
     */
    @Around("execution(* com.bitdf.txing.oj.controller.*.*(..))")
    public Object doInterceptor(ProceedingJoinPoint point) throws Throwable {
        // 延期token（根据需要）
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();
        String token = httpServletRequest.getHeader("TOKEN");
        if (StrUtil.isNotBlank(token) && !"null".equals(token)) {
            loginService.renewTokenIfNecessary(token);
        }


        // 计时
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // 生成请求唯一 id
        String requestId = UUID.randomUUID().toString();
        String url = httpServletRequest.getRequestURI();
        // 获取请求参数
        Object[] args = point.getArgs();
        String reqParam = "[" + StringUtils.join(args, ", ") + "]";
        // 输出请求日志
        log.info("request start，id: {}, path: {}, ip: {}, params: {}", requestId, url,
                httpServletRequest.getRemoteHost(), reqParam);
        // 执行原方法
        Object result = point.proceed();
        // 输出响应日志
        stopWatch.stop();
        long totalTimeMillis = stopWatch.getTotalTimeMillis();
        log.info("request end, id: {}, cost: {}ms", requestId, totalTimeMillis);
        return result;
    }
}

