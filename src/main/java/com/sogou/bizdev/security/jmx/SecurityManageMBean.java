package com.sogou.bizdev.security.jmx;



public interface SecurityManageMBean {

	/**
	 * 增加一种资源（小流量），type不能为空，url可为空
	 * 
	 * @param type
	 * @param url
	 * @return
	 */
	public boolean addResource(String type, String url);

	/**
	 * 根据type删除一种资源，角色-资源的对应关系也会相应被删除
	 * 
	 * @param type
	 * @return
	 */
	public boolean delResourceByType(String type);

	/**
	 * 根据type和url删除一种资源
	 * 
	 * @param type
	 * @param url
	 * @return
	 */
	public boolean delResourceByTypeAndUrl(String type, String url);

	/**
	 * 增加一种角色，角色名字应该唯一
	 * 
	 * @param role
	 * @return
	 */
	public boolean addRole(String role);

	/**
	 * 删除一种角色
	 * 
	 * @param role
	 * @return
	 */
	public boolean delRole(String role);

	/**
	 * 给某个角色授予某种类型的权限
	 * 
	 * @param role
	 * @param type
	 * @return
	 */
	public boolean addRoleResourceByType(String role, String type);

	/**
	 * 删除指定角色的指定类型权限
	 * 
	 * @param role
	 *            TODO
	 * @param type
	 * 
	 * @return
	 */
	public boolean delRoleResourceByType(String role, String type);

	/**
	 * 给某个角色授予某个权限
	 * 
	 * @param role
	 * @param type
	 * @param url
	 * @return
	 */
	public boolean addRoleResourceByTypeAndUrl(String role, String type, String url);

	/**
	 * 删除某个角色的某个权限
	 * 
	 * @param role
	 *            TODO
	 * @param type
	 * @param url
	 * @return
	 */
	public boolean delRoleResourceByTypeAndUrl(String role, String type, String url);

	/**
	 * 给一批账户ID授予某个角色的权限，id之间用英文的逗号隔开
	 * 
	 * @param accountIds
	 * @param role
	 * @return
	 */
	public boolean addAccountRole(String accountIds, String role);

	/**
	 * 回收一批账户的角色
	 * 
	 * @param accountIds
	 * @param role
	 * @return
	 */
	public boolean delAccountRole(String accountIds, String role);

	/**
	 * 列出系统中定义的所有资源（小流量）
	 * 
	 * @return
	 */
	public String listResources();

	/**
	 * 列出所有角色
	 * 
	 * @return
	 */
	public String listRoles();

	/**
	 * 列出角色和资源的匹配关系
	 * 
	 * @return
	 */
	public String listRoleResourceMap();

	/**
	 * 判断某个用户是否具有某种角色
	 * 
	 * @param accountId
	 * @param role
	 * @return
	 */
	public boolean userHasRole(Long accountId, String role);

	/**
	 * 查询某个资源需要哪些角色
	 * 
	 * @param type
	 * @param url
	 * @return
	 */
	public String requiredRole(String type, String url);

	/**
	 * 查询某个类型的资源（小流量）有哪些用户能够访问
	 * 
	 * @param type
	 * @return
	 */
	public String getAccountIdByResourceType(String type);

	/**
	 * 查询某个资源（小流量）有哪些用户能够访问
	 * 
	 * @param type
	 * @param url
	 * @return
	 */
	public String getAccountIdByResourceTypeAndUrl(String type, String url);

	/**
	 * 查看某种角色的用户有哪些
	 * 
	 * @param role
	 * @return
	 */
	public String getAccountIdByRole(String role);
}
