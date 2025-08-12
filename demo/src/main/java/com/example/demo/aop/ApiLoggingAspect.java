package com.example.demo.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Aspect
@Component
public class ApiLoggingAspect {
    private static final Logger log = LoggerFactory.getLogger(ApiLoggingAspect.class);

    // Áp dụng cho class/method có @ApiMonitored
    @Around("@within(com.example.demo.aop.ApiMonitored) || @annotation(com.example.demo.aop.ApiMonitored)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        String method = pjp.getSignature().toShortString();
        long start = System.currentTimeMillis();

        log.info("[AOP] -> {}", method);

        Object proceed = pjp.proceed();

        if (proceed instanceof Mono<?> mono) {
            return mono
                .doOnSuccess(ret -> log.info("[AOP] <- {} OK in {} ms", method, (System.currentTimeMillis()-start)))
                .doOnError(ex -> log.error("[AOP] <- {} ERROR in {} ms: {}", method, (System.currentTimeMillis()-start), ex.getMessage()));
        }

        log.info("[AOP] <- {} OK in {} ms", method, (System.currentTimeMillis()-start));
        return proceed;
    }
}
