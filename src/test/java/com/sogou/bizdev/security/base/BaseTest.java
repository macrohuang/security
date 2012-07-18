package com.sogou.bizdev.security.base;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.sogou.bizdev.platform.service.ServiceLocator;
import com.sogou.bizdev.security.service.PermissionCheck;

public class BaseTest {
	protected static ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-test.xml");;

	protected PermissionCheck permissionService;
	protected HibernateTemplate hibernateTemplate;

	static {
		ServiceLocator.getInstance().setFactory(context);
	}
}
