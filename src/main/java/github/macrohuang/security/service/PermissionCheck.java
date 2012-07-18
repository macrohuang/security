package github.macrohuang.security.service;

import github.macrohuang.security.po.Resource;

import java.util.List;
import java.util.Map;
import java.util.Set;


public interface PermissionCheck {
	/**
	 * 是否有某种角色
	 * 
	 * @param accountId
	 * @param roleName
	 * @return
	 */
	public boolean hasRole(Long accountId, String roleName);

	/**
	 * 是否有某种类型的资源的权限
	 * 
	 * @param accountId
	 * @param type
	 *            资源类型
	 * @return
	 */
	public boolean hasPermission(Long accountId, String type);

	/**
	 * 是否拥有某个具体资源的权限，这个是最细粒度的权限验证
	 * 
	 * @param accountId
	 * @param url
	 *            所要访问的资源，可以是url，path，方法名等
	 * @return
	 */
	public boolean canVisit(Long accountId, String type, String url);

	/**
	 * 获取某个用户的所有角色
	 * 
	 * @param accountId
	 * @return
	 */
	public List<String> getUserRoles(Long accountId);
	
	/**
	 * 获取某个用户的所有资源
	 * 
	 * @param accountId
	 * @return
	 */
	public List<String> getUserResources(Long accountId);

	/**
	 * 获取整个系统所定义的所有资源
	 * 
	 * @return
	 */
	public Set<Resource> getAllResources();

	/**
	 * 增加一种新角色
	 * 
	 * @param roleName
	 */
	public void addRole(String roleName);

	/**
	 * 删除一种角色
	 * 
	 * @param roleName
	 * @return TODO
	 */
	public boolean delRole(String roleName);

	/**
	 * 给用户赋予某种角色
	 * 
	 * @param accountId
	 * @param roleName
	 * @return TODO
	 */
	public boolean addAccountRole(Long accountId, String roleName);

	/**
	 * 删除用户的角色
	 * 
	 * @param accountId
	 * @param roleName
	 * @return TODO
	 */
	public boolean delAccountRole(Long accountId, String roleName);

	/**
	 * 增加一种资源，type不能为空
	 * 
	 * @param type
	 * @param url
	 */
	public void addResource(String type, String url);

	/**
	 * 删除某种类型的资源
	 * 
	 * @param type
	 * @return TODO
	 */
	public boolean delResourceByType(String type);

	/**
	 * 删除某种类型的一个子资源
	 * 
	 * @param url
	 * @param type
	 *            TODO
	 * @return
	 */
	public boolean delResourceByTypeAndUrl(String type, String url);

	/**
	 * 将某种类型的资源的权限赋予指定的角色
	 * 
	 * @param role
	 * @param type
	 * @return TODO
	 */
	public boolean addRoleResourceByType(String role, String type);

	/**
	 * 删除指定角色指定类型的资源
	 * 
	 * @param role
	 *            TODO
	 * @param type
	 * @return
	 */
	public boolean delRoleResourceByType(String role, String type);

	/**
	 * 根据type及url给指定角色授权
	 * 
	 * @param role
	 * @param url
	 * @return TODO
	 */
	public boolean addRoleResourceByTypeAndUrl(String role, String type, String url);

	/**
	 * 删除指定角色的权限
	 * 
	 * @param role
	 *            TODO
	 * @param url
	 * @return TODO
	 */
	public boolean delRoleResourceByUrl(String role, String type, String url);

	/**
	 * 角色是否已经存在
	 * 
	 * @param role
	 * @return
	 */
	public boolean existsRole(String role);

	/**
	 * 资源是否已经存在
	 * 
	 * @param type
	 * @param url
	 * @return
	 */
	public boolean existsResource(String type);

	/**
	 * 资源是否已经存在
	 * 
	 * @param type
	 * @param url
	 * @return
	 */
	public boolean existsResource(String type, String url);

	/**
	 * 
	 * @return
	 */
	public Set<String> listRoles();

	public Map<String, List<Resource>> listRoleResourceMap();

	/**
	 * 通过角色查看哪些用户具有该角色，可以用该方案来查询小流量的用户列表
	 * 
	 * @param role
	 * @return
	 */
	public Set<Long> getAccountIdByRole(String role);

	/**
	 * 通过小流量类型查看哪些用户具有该权限，可以用该方案来查询小流量的用户列表
	 * 
	 * @param role
	 * @return
	 */
	public Set<Long> getAccountIdByResourceType(String type);

	public Set<Long> getAccountIdByResourceTypeAndUrl(String type, String url);
}
