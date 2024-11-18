package LuckyVicky.backend.global.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ErrorLoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(ErrorLoggingAspect.class);

    @Pointcut("execution(* LuckyVicky.backend..*.controller.*.*(..)) || "
            + "execution(* LuckyVicky.backend..*.service.*.*(..)) ||"
            + " execution(* LuckyVicky.backend..*.repository.*.*(..))")
    public void applicationLayerPointcut() {
    }

    @AfterThrowing(pointcut = "applicationLayerPointcut()", throwing = "exception")
    public void logError(JoinPoint joinPoint, Throwable exception) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getMethod().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        Object[] args = joinPoint.getArgs();

        logger.error("!! [ERROR] Exception in {}.{}() with arguments: {}", className, methodName, args);
        logger.error("Exception: {}", exception.getMessage(), exception);
    }
}

