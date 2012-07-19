package github.macrohuang.security.launcher;

import github.macrohuang.security.service.PermissionCheck;
import github.macrohuang.security.util.ServiceLocator;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ApplicationLauncher {

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-test.xml");
		ServiceLocator.getInstance().setApplicationContext(context);
		PermissionCheck permissionService = (PermissionCheck) context.getBean("permissionCheck");
		System.out.println(permissionService.hasRole(1L, "test"));
		Thread.sleep(1000000);
	}
}
