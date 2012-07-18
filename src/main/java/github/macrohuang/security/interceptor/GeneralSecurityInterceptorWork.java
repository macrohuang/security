package github.macrohuang.security.interceptor;

import github.macrohuang.security.exception.NoPermissionException;
import github.macrohuang.security.service.PermissionCheck;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;

import github.macrohuang.platform.bizlog.SystemLoginUser;
import github.macrohuang.platform.generalinterceptor.AbstractGeneralInterceptorWorker;

public class GeneralSecurityInterceptorWork extends AbstractGeneralInterceptorWorker {

	private PermissionCheck permissionCheck;

	public PermissionCheck getPermissionCheck() {
		return permissionCheck;
	}

	public void setPermissionCheck(PermissionCheck permissionCheck) {
		this.permissionCheck = permissionCheck;
	}

	@Override
	public void doWorkBeforeMethodInvoke(SystemLoginUser userDto, JoinPoint jp, Object[] params) {
		Signature signature = jp.getSignature();
		MethodSignature methodSignature = (MethodSignature) signature;
		Method method = methodSignature.getMethod();
		if (method.isAnnotationPresent(github.macrohuang.security.annotation.PermissionCheck.class)) {
			github.macrohuang.security.annotation.PermissionCheck pc = method
					.getAnnotation(github.macrohuang.security.annotation.PermissionCheck.class);
			if (!permissionCheck.hasRole(userDto.getTargetId(), pc.requiredRole())) {
				throw new NoPermissionException("User required a resource that required role:" + pc.requiredRole() + ", but "
						+ userDto.getTargetId() + " didn't have that role.");
			}
		}
	}

	@Override
	public void doWorkAfterMethodInvoke(SystemLoginUser userDto, JoinPoint jp, Object result, Object[] params) {
		Signature signature = jp.getSignature();
		MethodSignature methodSignature = (MethodSignature) signature;
		Method method = methodSignature.getMethod();
		if (method.isAnnotationPresent(github.macrohuang.security.annotation.PermissionCheck.class)) {
			github.macrohuang.security.annotation.PermissionCheck pc = method
					.getAnnotation(github.macrohuang.security.annotation.PermissionCheck.class);
			if (!permissionCheck.hasRole(userDto.getTargetId(), pc.requiredRole())) {
				result = null;
			}
		}
	}
}
