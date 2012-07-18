package com.sogou.bizdev.security.interceptor;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;

import com.sogou.bizdev.security.exception.NoPermissionException;
import com.sogou.bizdev.security.service.PermissionCheck;

public class PermissiohCheckAspect {
	private PermissionCheck permissionCheck;
	private static Logger logger = Logger.getLogger(PermissiohCheckAspect.class);

	public PermissionCheck getPermissionCheck() {
		return permissionCheck;
	}

	public void setPermissionCheck(PermissionCheck permissionCheck) {
		this.permissionCheck = permissionCheck;
	}

	public void checkPermissionBefore(JoinPoint jp) {
		Signature signature = jp.getSignature();
		MethodSignature methodSignature = (MethodSignature) signature;
		Method method = methodSignature.getMethod();
		if (method.isAnnotationPresent(com.sogou.bizdev.security.annotation.PermissionCheck.class)) {
			if (jp.getArgs() != null) {
				Long accountId = null;
				try {
					accountId = Long.parseLong(String.valueOf(jp.getArgs()[0]));
				} catch (Exception e) {
					logger.info(method.getName()
							+ " is annotated by com.sogou.bizdev.security.annotation.PermissionCheck.class which requir the first argument of this method should be accountId, but get:"
							+ jp.getArgs()[0]);
					throw new IllegalArgumentException(
							method.getName()
									+ " is annotated by com.sogou.bizdev.security.annotation.PermissionCheck.class which requir the first argument of this method should be accountId, but get:"
									+ jp.getArgs()[0]);
				}
				com.sogou.bizdev.security.annotation.PermissionCheck pc = method
						.getAnnotation(com.sogou.bizdev.security.annotation.PermissionCheck.class);
				if (!permissionCheck.hasRole(accountId, pc.requiredRole())) {
					logger.info("User required a resource that required role:" + pc.requiredRole() + ", but " + accountId + " didn't have that role.");
					throw new NoPermissionException("User required a resource that required role:" + pc.requiredRole() + ", but " + accountId
							+ " didn't have that role.");
				}
			}
		}
	}
}
