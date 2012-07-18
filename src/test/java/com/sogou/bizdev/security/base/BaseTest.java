package github.macrohuang.security.base;

import github.macrohuang.security.service.PermissionCheck;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate3.HibernateTemplate;

import github.macrohuang.platform.service.ServiceLocator;

public class BaseTest {
	protected static ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-test.xml");;

	protected PermissionCheck permissionService;
	protected HibernateTemplate hibernateTemplate;

	static {
		ServiceLocator.getInstance().setFactory(context);
	}
}
