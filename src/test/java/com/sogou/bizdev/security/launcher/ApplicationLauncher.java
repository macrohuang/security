package com.sogou.bizdev.security.launcher;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.sogou.bizdev.platform.service.ServiceLocator;
import com.sogou.bizdev.security.service.PermissionCheck;

public class ApplicationLauncher {

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-test.xml");
		ServiceLocator.getInstance().setFactory(context);
		PermissionCheck permissionService = (PermissionCheck) context.getBean("permissionCheck");
		System.out.println(permissionService.hasRole(1L, "test"));
		Thread.sleep(1000000);
	}
}
