package com.sogou.bizdev.security.interceptor;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;

import com.sogou.bizdev.platform.bizlog.SystemLoginUser;
import com.sogou.bizdev.platform.generalinterceptor.AbstractGeneralInterceptorWorker;
import com.sogou.bizdev.security.exception.NoPermissionException;
import com.sogou.bizdev.security.service.PermissionCheck;

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
		if (method.isAnnotationPresent(com.sogou.bizdev.security.annotation.PermissionCheck.class)) {
			com.sogou.bizdev.security.annotation.PermissionCheck pc = method
					.getAnnotation(com.sogou.bizdev.security.annotation.PermissionCheck.class);
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
		if (method.isAnnotationPresent(com.sogou.bizdev.security.annotation.PermissionCheck.class)) {
			com.sogou.bizdev.security.annotation.PermissionCheck pc = method
					.getAnnotation(com.sogou.bizdev.security.annotation.PermissionCheck.class);
			if (!permissionCheck.hasRole(userDto.getTargetId(), pc.requiredRole())) {
				result = null;
			}
		}
	}
}
