package umc.pfc.orientamais.config.infraestructure.observability;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

  @Around(
      "execution(* umc.pfc.orientamais.adapters.input.rest.controller..*(..)) || execution(* umc.pfc.orientamais.application.service..*(..))")
  public Object logMethod(ProceedingJoinPoint joinPoint) throws Throwable {
    String methodName = joinPoint.getSignature().toShortString();
    Object[] args = joinPoint.getArgs();

    log.info(">> {} args={}", methodName, args.length > 0 ? args : "nenhum");

    try {
      Object result = joinPoint.proceed();
      log.info("<< {} result={}", methodName, result != null ? result : "vazio");
      return result;
    } catch (Exception ex) {
      if (ex instanceof RuntimeException) {
        log.warn("!! {} causou exceção: {}", methodName, ex.getMessage());
      } else {
        log.error("!! {} causou exceção inesperada", methodName, ex);
      }
      throw ex;
    }
  }
}
