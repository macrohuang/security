package github.macrohuang.security.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ServiceLocator {

	private static final ServiceLocator serviceLocator = new ServiceLocator();
	private ApplicationContext applicationContext;
	private ServiceLocator() {
		applicationContext = new ClassPathXmlApplicationContext("applicationContext-bizdev-security.xml");
	}

	public Object getBean(String beanName) {
		if (null == applicationContext) {
			throw new RuntimeException("factory is null,u should set it first.");
		}

		return applicationContext.getBean(beanName);
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public static ServiceLocator getInstance() {
		return serviceLocator;
	}
}
