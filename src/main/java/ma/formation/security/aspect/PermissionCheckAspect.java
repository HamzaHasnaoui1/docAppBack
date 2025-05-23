package ma.formation.security.aspect;

import lombok.RequiredArgsConstructor;
import ma.formation.exceptions.UnauthorizedException;
import ma.formation.security.annotations.RequirePermission;
import ma.formation.security.service.PermissionCheckService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
public class PermissionCheckAspect {

    private final PermissionCheckService permissionCheckService;

    @Around("@annotation(ma.formation.security.annotations.RequirePermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        
        RequirePermission requirePermission = method.getAnnotation(RequirePermission.class);
        String permissionName = requirePermission.value();
        
        if (!permissionCheckService.currentUserHasPermission(permissionName)) {
            throw new UnauthorizedException("Vous n'avez pas la permission nécessaire : " + permissionName);
        }
        
        return joinPoint.proceed();
    }
} 