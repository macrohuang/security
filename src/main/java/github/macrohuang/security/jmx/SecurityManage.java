package github.macrohuang.security.jmx;

import github.macrohuang.security.po.Resource;
import github.macrohuang.security.service.PermissionCheck;
import github.macrohuang.security.util.ServiceLocator;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

public class SecurityManage implements SecurityManageMBean {
	private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(SecurityManage.class);
	private PermissionCheck permissionCheck;


	public PermissionCheck getPermissionService() {
		if (permissionCheck == null) {
			synchronized (this) {
				permissionCheck = (PermissionCheck) ServiceLocator.getInstance().getBean("permissionCheck");
			}
		}
		return permissionCheck;
	}

	public void setPermissionService(PermissionCheck permissionService) {
		this.permissionCheck = permissionService;
	}

	@Override
	public boolean addResource(String type, String url) {
		if (StringUtils.isEmpty(type) || getPermissionService().existsResource(type, url)) {
			return false;
		}
		LOGGER.info("Resource add:" + type + "," + url);
		getPermissionService().addResource(type, url);
		return true;
	}

	@Override
	public boolean delResourceByType(String type) {
		if (StringUtils.isEmpty(type))
			return false;
		LOGGER.info("Resource delete:" + type);
		return getPermissionService().delResourceByType(type);
	}

	@Override
	public boolean delResourceByTypeAndUrl(String type, String url) {
		if (StringUtils.isEmpty(type))
			return false;
		return getPermissionService().delResourceByTypeAndUrl(type, url);
	}

	@Override
	public boolean addRole(String role) {
		if (StringUtils.isEmpty(role) || getPermissionService().existsRole(role))
			return false;
		getPermissionService().addRole(role);
		return true;
	}

	@Override
	public boolean delRole(String role) {
		if (StringUtils.isEmpty(role))
			return false;
		return getPermissionService().delRole(role);
	}

	@Override
	public boolean addRoleResourceByType(String role, String type) {
		if (StringUtils.isEmpty(role) || StringUtils.isEmpty(type) || !getPermissionService().existsRole(role)) {
			return false;
		}
		return getPermissionService().addRoleResourceByType(role, type);
	}

	@Override
	public boolean delRoleResourceByType(String role, String type) {
		if (StringUtils.isEmpty(role) || StringUtils.isEmpty(type))
			return false;
		return getPermissionService().delRoleResourceByType(role, type);
	}

	@Override
	public boolean addRoleResourceByTypeAndUrl(String role, String type, String url) {
		if (StringUtils.isEmpty(role) || StringUtils.isEmpty(type) || !getPermissionService().existsRole(role))
			return false;
		return getPermissionService().addRoleResourceByTypeAndUrl(role, type, url);
	}

	@Override
	public boolean delRoleResourceByTypeAndUrl(String role, String type, String url) {
		if (StringUtils.isEmpty(role) || StringUtils.isEmpty(type))
			return false;
		return getPermissionService().delRoleResourceByUrl(role, type, url);
	}

	@Override
	public boolean addAccountRole(String accountIds, String role) {
		if (StringUtils.isEmpty(accountIds) || StringUtils.isEmpty(role)) {
			return false;
		}
		Set<Long> ids = getAccountIds(accountIds);
		boolean result = true;
		for (Long id : ids) {
			result &= getPermissionService().addAccountRole(id, role);
		}
		return result;
	}

	@Override
	public boolean delAccountRole(String accountIds, String role) {
		if (StringUtils.isEmpty(accountIds) || StringUtils.isEmpty(role))
			return false;
		Set<Long> ids = getAccountIds(accountIds);
		boolean result = true;
		for (Long id : ids) {
			result &= getPermissionService().delAccountRole(id, role);
		}
		return result;
	}

	@Override
	public String listResources() {
		Set<Resource> resources = getPermissionService().getAllResources();
		return resources.toString();
	}

	@Override
	public String listRoles() {
		return getPermissionService().listRoles().toString();
	}

	@Override
	public String listRoleResourceMap() {
		return getPermissionService().listRoleResourceMap().toString();
	}

	@Override
	public boolean userHasRole(Long accountId, String role) {
		return getPermissionService().hasRole(accountId, role);
	}

	@Override
	public String requiredRole(String type, String url) {
		Map<String, List<Resource>> map = getPermissionService().listRoleResourceMap();
		Set<String> roles = new HashSet<String>();
		for (String role : map.keySet()) {
			List<Resource> resources = map.get(role);
			for (Resource resource : resources) {
				if (StringUtils.isEmpty(url) && resource.getType().equals(type) || !StringUtils.isEmpty(url) && resource.getType().equals(type)
						&& resource.getUrl().equals(url)) {
					roles.add(role);
				}
			}
		}
		return roles.toString();
	}

	@Override
	public String getAccountIdByResourceType(String type) {
		if (StringUtils.isEmpty(type))
			return "";
		return getPermissionService().getAccountIdByResourceType(type).toString();
	}

	@Override
	public String getAccountIdByResourceTypeAndUrl(String type, String url) {
		if (StringUtils.isEmpty(type) || StringUtils.isEmpty(url))
			return "";
		return getPermissionService().getAccountIdByResourceTypeAndUrl(type, url).toString();
	}

	@Override
	public String getAccountIdByRole(String role) {
		if (StringUtils.isEmpty(role))
			return "";
		return getPermissionService().getAccountIdByRole(role).toString();
	}

	private Set<Long> getAccountIds(String ids) {
		if (StringUtils.isEmpty(ids))
			return Collections.emptySet();
		Set<Long> idSet = new HashSet<Long>();
		for (String id : ids.split(",")) {
			idSet.add(Long.parseLong(id));
		}
		return idSet;
	}
}
