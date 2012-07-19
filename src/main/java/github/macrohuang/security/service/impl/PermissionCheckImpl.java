package github.macrohuang.security.service.impl;

import github.macrohuang.security.constants.PermissionConstants;
import github.macrohuang.security.po.AccountRoleMap;
import github.macrohuang.security.po.Resource;
import github.macrohuang.security.po.Role;
import github.macrohuang.security.po.RoleResourceMap;
import github.macrohuang.security.service.PermissionCheck;
import github.macrohuang.security.service.impl.PermissionServiceLoader.CheckStatus;
import github.macrohuang.security.util.MyCacheUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class PermissionCheckImpl implements PermissionCheck {
	private HibernateTemplate hibernateTemplate;
	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(PermissionCheckImpl.class);

	public HibernateTemplate getHibernateTemplate() {
		return hibernateTemplate;
	}

	public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}

	private boolean wrapStatus(CheckStatus status) {
		switch (status) {
		case PASS:
			return true;
		default:
			return false;
		}
	}
	@Override
	public boolean hasRole(Long accountId, String roleName) {
		if (PermissionServiceLoader.hasRole(accountId, roleName) == CheckStatus.NO_CACHE) {
			init();
		}

		return !existsRole(roleName) || wrapStatus(PermissionServiceLoader.hasRole(accountId, roleName));
	}

	@Override
	public boolean hasPermission(Long accountId, String type) {
		if (PermissionServiceLoader.hasPermission(accountId, type) == CheckStatus.NO_CACHE) {
			init();
		}
		return !existsResource(type) || wrapStatus(PermissionServiceLoader.hasPermission(accountId, type));
	}

	@Override
	public boolean canVisit(Long accountId, String type, String url) {
		if (PermissionServiceLoader.canVisit(accountId, type, url) == CheckStatus.NO_CACHE) {
			init();
		}
		return !existsResource(type, url) || wrapStatus(PermissionServiceLoader.canVisit(accountId, type, url));
	}

	@Override
	public List<String> getUserRoles(Long accountId) {
		if (PermissionServiceLoader.needRefresh()) {
			init();
		}
		return PermissionServiceLoader.getUserRoles(accountId);
	}

	@Override
	public List<String> getUserResources(Long accountId) {
		if (PermissionServiceLoader.needRefresh()) {
			init();
		}
		return PermissionServiceLoader.getUserResources(accountId);
	}
	
	@Override
	public Set<Resource> getAllResources() {
		if (PermissionServiceLoader.needRefresh()) {
			init();
		}
		return PermissionServiceLoader.getAllResources();
	}

	@SuppressWarnings("unchecked")
	private synchronized void init() {
		logger.info("init permission map, loading from db.");
		List<AccountRoleMap> accountRoleMaps = hibernateTemplate.loadAll(AccountRoleMap.class);
		Map<Long, List<String>> accountRoleMap = new HashMap<Long, List<String>>();
		for (AccountRoleMap ar : accountRoleMaps) {
			if (!accountRoleMap.containsKey(ar.getAccountId())) {
				accountRoleMap.put(ar.getAccountId(), new ArrayList<String>());
			}
			Role role = (Role) hibernateTemplate.get(Role.class, ar.getRoleId());
			accountRoleMap.get(ar.getAccountId()).add(role.getName());
		}
		PermissionServiceLoader.getUserRoleMap().clear();
		PermissionServiceLoader.getUserRoleMap().putAll(accountRoleMap);

		List<RoleResourceMap> roleResourceMaps = hibernateTemplate.loadAll(RoleResourceMap.class);
		Map<String, List<Resource>> roleResourceMap = new HashMap<String, List<Resource>>();
		for (RoleResourceMap rr : roleResourceMaps) {
			Role role = (Role) hibernateTemplate.get(Role.class, rr.getRoleId());
			if (!roleResourceMap.containsKey(role.getName())) {
				roleResourceMap.put(role.getName(), new ArrayList<Resource>());
			}
			Resource resource = (Resource) hibernateTemplate.get(Resource.class, rr.getResourceId());
			roleResourceMap.get(role.getName()).add(resource);
		}

		PermissionServiceLoader.getRoleResourceMap().clear();
		PermissionServiceLoader.getRoleResourceMap().putAll(roleResourceMap);

		List<Resource> resources = hibernateTemplate.loadAll(Resource.class);
		PermissionServiceLoader.getResources().clear();
		PermissionServiceLoader.getResources().addAll(resources);

		List<Role> roless = hibernateTemplate.loadAll(Role.class);
		PermissionServiceLoader.getRoles().clear();
		PermissionServiceLoader.getRoles().addAll(roless);
		logger.info("init permission map finish.");

	}

	private void updateCache() {
		logger.info("update permission cache.");
		MyCacheUtils.put(PermissionConstants.PERMISSION_CHECK_CACHE_DOMAIN, PermissionConstants.PERMISSION_CHECK_CACHE_KEY,
				System.currentTimeMillis());
	}

	@Override
	public void addRole(String roleName) {
		logger.info("Role:" + roleName + " is added.");
		hibernateTemplate.save(new Role(roleName));
		updateCache();
	}

	private Role getRoleByName(final String name) {
		return (Role) hibernateTemplate.execute(new HibernateCallback() {

			@Override
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery("from Role r where r.name=:name");
				query.setParameter("name", name);
				return query.uniqueResult();
			}
		});
	}

	@Override
	public boolean delRole(final String roleName) {
		logger.info("delete role invoked:" + roleName);
		return (Boolean) hibernateTemplate.execute(new HibernateCallback() {

			@Override
			public Object doInHibernate(Session session) throws HibernateException, SQLException {

				Role role = getRoleByName(roleName);

				Transaction transaction = session.beginTransaction();
				try {
					transaction.begin();
					// Delete user-role-mapping
					Query query = session.createQuery("delete AccountRoleMap t where t.roleId=:id");
					query.setParameter("id", role.getId());
					query.executeUpdate();

					// Delete role-resource-mapping
					query = session.createQuery("delete RoleResourceMap r where r.roleId=:roleId");
					query.setParameter("roleId", role.getId());
					query.executeUpdate();

					// Delete role
					session.delete(role);
					transaction.commit();
					updateCache();
					return true;
				} catch (Exception e) {
					logger.error(e.getMessage());
					transaction.rollback();
					return false;
				}
			}
		});
	}

	@Override
	public boolean addAccountRole(final Long accountId, final String roleName) {
		logger.info("add account role:" + accountId + "," + roleName);
		Role role = getRoleByName(roleName);
		if (role == null)
			return false;
		AccountRoleMap roleMap = new AccountRoleMap(accountId, role.getId());
		hibernateTemplate.save(roleMap);
		updateCache();
		return true;
	}

	@Override
	public boolean delAccountRole(Long accountId, String roleName) {
		logger.info("del account role:" + accountId + "," + roleName);
		Role role = getRoleByName(roleName);
		if (role == null)
			return false;
		AccountRoleMap roleMap = new AccountRoleMap(accountId, role.getId());
		hibernateTemplate.delete(roleMap);
		updateCache();
		return true;
	}

	@Override
	public void addResource(String type, String url) {
		logger.info("add resource:" + type + "," + url);
		Resource resource = new Resource(type, url);
		hibernateTemplate.save(resource);
		updateCache();
	}

	@SuppressWarnings("unchecked")
	private List<Resource> getResourceByType(final String type) {
		return (List<Resource>) hibernateTemplate.execute(new HibernateCallback() {

			@Override
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery("from Resource r where r.type=:type");
				query.setParameter("type", type);
				return query.list();
			}
		});
	}

	@Override
	public boolean delResourceByType(final String type) {
		logger.info("delResourceByType:" + type);
		return (Boolean) hibernateTemplate.execute(new HibernateCallback() {

			@Override
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				List<Resource> resource = getResourceByType(type);
				Transaction transaction = session.beginTransaction();
				try {
					transaction.begin();
					for (Resource r : resource) {
						Query query = session.createQuery("delete RoleResourceMap r where r.resourceId=:id");
						query.setParameter("id", r.getId());
						query.executeUpdate();
						session.delete(r);
					}
					transaction.commit();
					updateCache();
					return true;
				} catch (Exception e) {
					logger.error(e.getMessage());
					transaction.rollback();
					return false;
				}
			}
		});
	}

	private Resource getResourceByTypeAndUrl(final String type, final String url) {
		return (Resource) hibernateTemplate.execute(new HibernateCallback() {

			@Override
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery("from Resource r where r.type=:type and r.url=:url");
				query.setParameter("type", type);
				query.setParameter("url", url);
				return query.uniqueResult();
			}
		});
	}

	@Override
	public boolean delResourceByTypeAndUrl(final String type, final String url) {
		logger.info("delResourceByTypeAndUrl:" + type + "," + url);
		return (Boolean) hibernateTemplate.execute(new HibernateCallback() {

			@Override
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Resource resource = getResourceByTypeAndUrl(type, url);
				Transaction transaction = session.beginTransaction();
				try {
					transaction.begin();
					Query query = session.createQuery("delete RoleResourceMap r where r.resourceId=:id");
					query.setParameter("id", resource.getId());
					query.executeUpdate();
					session.delete(resource);
					transaction.commit();
					updateCache();
					return true;
				} catch (Exception e) {
					logger.error(e.getMessage());
					transaction.rollback();
					return false;
				}
			}
		});
	}

	@Override
	public boolean addRoleResourceByType(String role, String type) {
		logger.info("add role-resource mapping by resource type:" + role + "," + type);
		Role role2 = getRoleByName(role);
		List<Resource> resources = getResourceByType(type);
		if (resources == null || resources.isEmpty())
			return false;
		for (Resource resource : resources) {
			RoleResourceMap resourceMap = new RoleResourceMap(role2.getId(), resource.getId());
			hibernateTemplate.save(resourceMap);
		}
		updateCache();
		return true;
	}

	@Override
	public boolean delRoleResourceByType(final String role, final String type) {
		logger.info("delResourceByType,role:" + role + ",type:" + type);
		return (Boolean) hibernateTemplate.execute(new HibernateCallback() {

			@Override
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				List<Resource> resources = getResourceByType(type);
				Role role2 = getRoleByName(role);
				Transaction transaction = session.beginTransaction();
				try {
					transaction.begin();
					for (Resource r : resources) {
						Query query = session.createQuery("delete RoleResourceMap r where r.roleId=:rid and r.resourceId=:id");
						query.setParameter("rid", role2.getId());
						query.setParameter("id", r.getId());
						query.executeUpdate();
					}
					transaction.commit();
					updateCache();
					return true;
				} catch (Exception e) {
					logger.error(e.getMessage());
					transaction.rollback();
					return false;
				}
			}
		});
	}

	@Override
	public boolean addRoleResourceByTypeAndUrl(String role, String type, String url) {
		logger.info("add role-Resource map ByTypeAndUrl, role" + role + ",type:" + type + ",url:" + url);
		Role role2 = getRoleByName(role);
		Resource resource = getResourceByTypeAndUrl(type, url);
		if (role2 == null || resource == null)
			return false;
		RoleResourceMap roleResourceMap = new RoleResourceMap(role2.getId(), resource.getId());
		hibernateTemplate.save(roleResourceMap);
		updateCache();
		return true;
	}

	@Override
	public boolean delRoleResourceByUrl(final String role, final String type, final String url) {
		logger.info("delRoleResourceByUrl,role:" + role + ",type:" + type + ",url" + url);
		boolean result = (Boolean) hibernateTemplate.execute(new HibernateCallback() {
			@Override
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Role role2 = getRoleByName(role);
				Resource resource = getResourceByTypeAndUrl(type, url);
				if (role2 == null || resource == null)
					return false;
				Query query = session.createQuery("delete RoleResourceMap r where r.roleId=:rid and r.resourceId=:id");
				query.setParameter("rid", role2.getId());
				query.setParameter("id", resource.getId());
				return query.executeUpdate() > 0;
			}
		});
		if (result)
			updateCache();
		return result;
	}

	@Override
	public boolean existsRole(String role) {
		if (PermissionServiceLoader.needRefresh()) {
			init();
		}
		List<Role> roles = PermissionServiceLoader.getRoles();
		for (Role role2 : roles) {
			if (role2.getName().equals(role))
				return true;
		}
		return false;
		// try {
		// Role role2 = getRoleByName(role);
		// return role2 != null;
		// } catch (Exception e) {
		// logger.error(e.getMessage());
		// return false;
		// }
	}

	@Override
	public boolean existsResource(String type) {
		if (PermissionServiceLoader.needRefresh()) {
			init();
		}
		Set<Resource> resources = PermissionServiceLoader.getAllResources();
		for (Resource resource : resources) {
			if (resource.getType().equals(type))
				return true;
		}
		return false;
		// try {
		// List<Resource> resource = getResourceByType(type);
		// return resource != null && !resource.isEmpty();
		// } catch (Exception e) {
		// logger.error(e.getMessage());
		// return false;
		// }
	}

	@Override
	public boolean existsResource(String type, String url) {
		try {
			Resource resource = getResourceByTypeAndUrl(type, url);
			return resource != null;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
	}

	@Override
	public Set<String> listRoles() {
		if (PermissionServiceLoader.needRefresh()) {
			init();
		}
		List<Role> roles = PermissionServiceLoader.getRoles();
		Set<String> roleSet = new HashSet<String>();
		for (Role role : roles) {
			roleSet.add(role.getName());
		}
		return roleSet;
	}

	@Override
	public Map<String, List<Resource>> listRoleResourceMap() {
		if (PermissionServiceLoader.needRefresh()) {
			init();
		}
		return new HashMap<String, List<Resource>>(PermissionServiceLoader.getRoleResourceMap());
	}

	@Override
	public Set<Long> getAccountIdByRole(String role) {
		if (PermissionServiceLoader.needRefresh()) {
			init();
		}
		Set<Long> set = new HashSet<Long>();
		for (Long aid : PermissionServiceLoader.getUserRoleMap().keySet()) {
			if (hasRole(aid, role)) {
				set.add(aid);
			}
		}
		return set;
	}

	@Override
	public Set<Long> getAccountIdByResourceType(String type) {
		if (PermissionServiceLoader.needRefresh()) {
			init();
		}
		Set<Long> set = new HashSet<Long>();
		for (Long aid : PermissionServiceLoader.getUserRoleMap().keySet()) {
			if (hasPermission(aid, type)) {
				set.add(aid);
			}
		}
		return set;
	}

	@Override
	public Set<Long> getAccountIdByResourceTypeAndUrl(String type, String url) {
		if (PermissionServiceLoader.needRefresh()) {
			init();
		}
		Set<Long> set = new HashSet<Long>();
		for (Long aid : PermissionServiceLoader.getUserRoleMap().keySet()) {
			if (canVisit(aid, type, url)) {
				set.add(aid);
			}
		}
		return set;
	}


}
