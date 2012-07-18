package github.macrohuang.security.service;

import github.macrohuang.security.constants.PermissionConstants;
import github.macrohuang.security.po.AccountRoleMap;
import github.macrohuang.security.po.Resource;
import github.macrohuang.security.po.Role;
import github.macrohuang.security.po.RoleResourceMap;
import github.macrohuang.security.service.PermissionCheck;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

import github.macrohuang.security.base.BaseTest;
import github.macrohuang.utils.cache.MyCacheUtils;

public class PermissionServiceTest extends BaseTest {
	final String s = "unit-test";
	final Long accountId = 8764L;

	@Before
	public void init() {
		hibernateTemplate = (HibernateTemplate) context.getBean("permissionHibernateTemplate");
		Assert.assertNotNull(hibernateTemplate);
		permissionService = (PermissionCheck) context.getBean("permissionCheck");
		Assert.assertNotNull(permissionService);
		MyCacheUtils.put(PermissionConstants.PERMISSION_CHECK_CACHE_DOMAIN, PermissionConstants.PERMISSION_CHECK_CACHE_KEY,
				System.currentTimeMillis());
	}

	@Test
	public void testAddRole() {
		permissionService.addRole(s);
		Role role = (Role) hibernateTemplate.find("from Role r where name='unit-test'").get(0);
			Assert.assertNotNull(role);
			Assert.assertEquals("unit-test", role.getName());
	}

	@Test
	public void testGetRole() {
		Role role = new Role(s);
		hibernateTemplate.save(role);
		Set<String> roles = permissionService.listRoles();
			Assert.assertTrue(roles.contains(role.getName()));
	}

	@Test
	public void testDelRole() {
		Role role = new Role(s);
		hibernateTemplate.save(role);
		permissionService.delRole(s);
		Set<String> roles = permissionService.listRoles();
		Assert.assertFalse(roles.contains(role.getName()));
	}

	@Test
	public void testAddResource() {
		permissionService.addResource(s, s);
		Resource resource = (Resource) hibernateTemplate.find("from Resource r where r.type=? and r.url=?", new String[] { s, s }).get(0);
			Assert.assertNotNull(resource);
			Assert.assertEquals(s, resource.getType());
			Assert.assertEquals(s, resource.getUrl());
	}

	@Test
	public void testGetResource() {
		Resource resource = new Resource(s, s);
		hibernateTemplate.save(resource);

		Set<Resource> resources = permissionService.getAllResources();
			Assert.assertNotNull(resources);
			Assert.assertTrue(resources.contains(resource));
	}

	@Test
	public void testDelResource() {
		Resource resource = new Resource(s, s);
		hibernateTemplate.save(resource);

		permissionService.delResourceByTypeAndUrl(s, s);

		Set<Resource> resources = permissionService.getAllResources();
		Assert.assertFalse(resources.contains(resource));
	}

	@Test
	public void testAddRoleResource() {
		final Role role = new Role(s);
		final Resource resource = new Resource(s, s);
		hibernateTemplate.save(role);
		hibernateTemplate.save(resource);

		try {
			Assert.assertTrue(permissionService.addRoleResourceByType(s, s));
		} finally {
			hibernateTemplate.execute(new HibernateCallback() {

				@Override
				public Object doInHibernate(Session session) throws HibernateException, SQLException {
					Query query = session.createQuery("delete RoleResourceMap r where r.roleId=:rid and r.resourceId=:id");
					query.setParameter("rid", role.getId());
					query.setParameter("id", resource.getId());
					query.executeUpdate();
					return null;
				}
			});
		}
	}

	@Test
	public void testGetRoleResource() {
		final Role role = new Role(s);
		final Resource resource = new Resource(s, s);
		hibernateTemplate.save(role);
		hibernateTemplate.save(resource);
		final RoleResourceMap roleResourceMap = new RoleResourceMap(role.getId(), resource.getId());
		hibernateTemplate.save(roleResourceMap);

		Map<String, List<Resource>> map = permissionService.listRoleResourceMap();
		Assert.assertNotNull(map);
		Assert.assertTrue(map.containsKey(role.getName()));
		Assert.assertTrue(map.get(role.getName()).contains(resource));

		try {
		} finally {
			hibernateTemplate.execute(new HibernateCallback() {

				@Override
				public Object doInHibernate(Session session) throws HibernateException, SQLException {
					Query query = session.createQuery("delete RoleResourceMap r where r.roleId=:rid and r.resourceId=:id");
					query.setParameter("rid", role.getId());
					query.setParameter("id", resource.getId());
					query.executeUpdate();
					return null;
				}
			});
		}
	}

	@Test
	public void testDelRoleResource() {
		final Role role = new Role(s);
		final Resource resource = new Resource(s, s);
		hibernateTemplate.save(role);
		hibernateTemplate.save(resource);
		final RoleResourceMap roleResourceMap = new RoleResourceMap(role.getId(), resource.getId());
		hibernateTemplate.save(roleResourceMap);

		try {
			Assert.assertTrue(permissionService.delRoleResourceByUrl(role.getName(), resource.getType(), resource.getUrl()));

			Map<String, List<Resource>> map = permissionService.listRoleResourceMap();
			Assert.assertFalse(map.containsKey(role.getName()));
			Assert.assertTrue(map.get(role.getName()) == null || !map.get(role.getName()).contains(resource));
		} finally {
			hibernateTemplate.execute(new HibernateCallback() {

				@Override
				public Object doInHibernate(Session session) throws HibernateException, SQLException {
					Query query = session.createQuery("delete RoleResourceMap r where r.roleId=:rid and r.resourceId=:id");
					query.setParameter("rid", role.getId());
					query.setParameter("id", resource.getId());
					query.executeUpdate();
					return null;
				}
			});
		}
	}

	@Test
	public void testAddAccountRole() {
		final Role role = new Role(s);
		hibernateTemplate.save(role);
		try {
			Assert.assertTrue(permissionService.addAccountRole(accountId, s));
			AccountRoleMap accountRoleMap = new AccountRoleMap(accountId, role.getId());
			AccountRoleMap ar2 = (AccountRoleMap) hibernateTemplate.findByExample(accountRoleMap).get(0);
			Assert.assertNotNull(ar2);
			Assert.assertEquals(accountRoleMap, ar2);
		} finally {
			hibernateTemplate.execute(new HibernateCallback() {

				@Override
				public Object doInHibernate(Session session) throws HibernateException, SQLException {
					Query query = session.createQuery("delete AccountRoleMap ar where ar.accountId=:aid and ar.roleId=:rid");
					query.setParameter("aid", accountId);
					query.setParameter("rid", role.getId());
					query.executeUpdate();
					return null;
				}
			});
		}

	}

	@Test
	public void testGetAccountRole() {
		final Role role = new Role(s);
		hibernateTemplate.save(role);
		AccountRoleMap accountRoleMap = new AccountRoleMap(accountId, role.getId());
		hibernateTemplate.save(accountRoleMap);
		try {
			Assert.assertTrue(permissionService.hasRole(accountId, role.getName()));
		} finally {
			hibernateTemplate.delete(accountRoleMap);
		}
	}

	@Test
	public void testDelAccountRole() {
		final Role role = new Role(s);
		hibernateTemplate.save(role);
		AccountRoleMap accountRoleMap = new AccountRoleMap(accountId, role.getId());
		hibernateTemplate.save(accountRoleMap);
		try {
			Assert.assertTrue(permissionService.hasRole(accountId, role.getName()));
			Assert.assertTrue(permissionService.delAccountRole(accountId, role.getName()));
			Assert.assertFalse(permissionService.hasRole(accountId, role.getName()));
		} finally {
			hibernateTemplate.delete(accountRoleMap);
		}
	}

	@Test
	public void testHasPermission() {
		Resource resource = new Resource(s, s);
		Role role = new Role(s);
		hibernateTemplate.save(resource);
		hibernateTemplate.save(role);

		RoleResourceMap resourceMap = new RoleResourceMap(role.getId(), resource.getId());
		hibernateTemplate.save(resourceMap);

		AccountRoleMap roleMap = new AccountRoleMap(accountId, role.getId());
		hibernateTemplate.save(roleMap);

		try {
			Assert.assertTrue(permissionService.hasPermission(accountId, resource.getType()));
		} finally {
			hibernateTemplate.delete(roleMap);
			hibernateTemplate.delete(resourceMap);
		}

	}

	@Test
	public void testCanVisit() {
		Resource resource = new Resource(s, s);
		Role role = new Role(s);
		hibernateTemplate.save(resource);
		hibernateTemplate.save(role);

		RoleResourceMap resourceMap = new RoleResourceMap(role.getId(), resource.getId());
		hibernateTemplate.save(resourceMap);

		AccountRoleMap roleMap = new AccountRoleMap(accountId, role.getId());
		hibernateTemplate.save(roleMap);

		try {
			Assert.assertTrue(permissionService.canVisit(accountId, resource.getType(), resource.getUrl()));
		} finally {
			hibernateTemplate.delete(roleMap);
			hibernateTemplate.delete(resourceMap);
		}

	}

	@Test
	public void testGetUserRoles() {
		Role role = new Role(s);
		hibernateTemplate.save(role);
		AccountRoleMap accountRoleMap = new AccountRoleMap(accountId, role.getId());
		hibernateTemplate.save(accountRoleMap);

		try {
			Assert.assertTrue(permissionService.getUserRoles(accountId).contains(role.getName()));
		} finally {
			hibernateTemplate.delete(accountRoleMap);
		}
	}

	@Test
	public void testExistsRole() {
		Role role = new Role(s);
		hibernateTemplate.save(role);
			Assert.assertTrue(permissionService.existsRole(role.getName()));
	}

	@Test
	public void testExistsResource() {
		Resource resource = new Resource(s, s);
		hibernateTemplate.save(resource);
		Assert.assertTrue(permissionService.existsResource(s, s));
	}

	@Test
	public void testGetAccountIdByRole() {
		Role role = new Role(s);
		hibernateTemplate.save(role);
		AccountRoleMap roleMap = new AccountRoleMap(accountId, role.getId());
		hibernateTemplate.save(roleMap);
		try {
			Assert.assertTrue(permissionService.getAccountIdByRole(role.getName()).contains(accountId));
		} finally {
			hibernateTemplate.delete(roleMap);
		}
	}

	@Test
	public void testGetAccountIdByType() {
		Role role = new Role(s);
		Resource resource = new Resource(s, s);
		hibernateTemplate.save(role);
		hibernateTemplate.save(resource);
		RoleResourceMap resourceMap = new RoleResourceMap(role.getId(), resource.getId());
		hibernateTemplate.save(resourceMap);

		AccountRoleMap accountRoleMap = new AccountRoleMap(accountId, role.getId());
		hibernateTemplate.save(accountRoleMap);

		try {
			Assert.assertTrue(permissionService.getAccountIdByResourceType(resource.getType()).contains(accountId));
		} finally {
			hibernateTemplate.delete(resourceMap);
			hibernateTemplate.delete(accountRoleMap);
		}
	}

	@Test
	public void testGetAccountIdByTypeAndUrl() {
		Role role = new Role(s);
		Resource resource = new Resource(s, s);
		hibernateTemplate.save(role);
		hibernateTemplate.save(resource);
		RoleResourceMap resourceMap = new RoleResourceMap(role.getId(), resource.getId());
		hibernateTemplate.save(resourceMap);

		AccountRoleMap accountRoleMap = new AccountRoleMap(accountId, role.getId());
		hibernateTemplate.save(accountRoleMap);

		try {
			Assert.assertTrue(permissionService.getAccountIdByResourceTypeAndUrl(resource.getType(), resource.getUrl()).contains(accountId));
		} finally {
			hibernateTemplate.delete(resourceMap);
			hibernateTemplate.delete(accountRoleMap);
		}
	}

	@After
	public void clear() {
		hibernateTemplate.execute(new HibernateCallback() {

			@Override
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery("delete Role r where r.name=:name");
				query.setParameter("name", s);
				query.executeUpdate();

				query = session.createQuery("delete Resource r where r.type=:type and r.url=:url");
				query.setParameter("type", s);
				query.setParameter("url", s);
				query.executeUpdate();

				return null;
			}
		});
	}
}
