package phoug.store.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // Pointcut для всех методов приложения, кроме LogController
    @Pointcut("execution(* phoug.store..*(..)) && !within(phoug.store.controller.LogController)")
    public void applicationPackagePointcut() {}

    // Логирование перед выполнением метода
    @Before("applicationPackagePointcut()")
    public void logBefore(JoinPoint joinPoint) {
        if (logger.isInfoEnabled()) {
            logger.info("Executing method: {}", joinPoint.getSignature().toShortString());
        }
    }

    // Логирование после успешного выполнения метода
    @AfterReturning(pointcut = "applicationPackagePointcut()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        if (logger.isInfoEnabled()) {
            String methodName = joinPoint.getSignature().toShortString();

            // Исключение для метода viewLogsByDate (можно по имени или классу/методу)
            if (methodName.contains("LogServiceImpl.viewLogsByDate")) {
                logger.info("Method {} executed successfully. (result omitted)", methodName);
            } else {
                logger.info("Method {} executed successfully. Result: {}", methodName, result);
            }
        }
    }


    // Логирование ошибок
    @AfterThrowing(pointcut = "applicationPackagePointcut()", throwing = "error")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable error) {
        if (logger.isErrorEnabled()) {
            logger.error("Error in method: {}. Error: {}",
                    joinPoint.getSignature().toShortString(), error.getMessage(), error);
        }
    }
}
