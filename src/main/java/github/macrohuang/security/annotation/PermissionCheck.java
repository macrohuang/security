package github.macrohuang.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 资源权限检查的标注，指定了requiredRole表示要访问目标方法需要有指定的角色（role）；
 * 指定了resourceType则表明目标方法是某种类型的资源，要求被调者要拥有该资源权限的角色。
 * 
 * @author huangtianlai
 * 
 */
@Target(value = { ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface PermissionCheck {
	public String requiredRole() default "";
}
