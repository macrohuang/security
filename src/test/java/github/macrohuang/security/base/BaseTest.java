package github.macrohuang.security.base;

import github.macrohuang.security.service.PermissionCheck;
import github.macrohuang.security.util.ServiceLocator;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class BaseTest {
	protected static ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-test.xml");;

	protected PermissionCheck permissionService;
	protected HibernateTemplate hibernateTemplate;

	static {
		ServiceLocator.getInstance().setApplicationContext(context);
	}
}
