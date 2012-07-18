package com.sogou.bizdev.security.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.sogou.bizdev.security.constants.PermissionConstants;
import com.sogou.bizdev.security.po.Resource;
import com.sogou.bizdev.security.po.Role;
import com.sogou.bizdev.utils.cache.MyCacheUtils;

public final class PermissionServiceLoader {
	private static final Map<Long, List<String>> userRoleMap = new HashMap<Long, List<String>>();
	private static final Map<String, List<Resource>> roleResourceMap = new HashMap<String, List<Resource>>();
	private static final List<Resource> RESOURCES = new ArrayList<Resource>();
	private static final List<Role> ROLES = new ArrayList<Role>();
	private static long timeTag = -1l;
	public enum CheckStatus {
		PASS, FAIL, NO_CACHE
	}

	public static boolean needRefresh() {
		try {
			Long time = Long.parseLong(String.valueOf(MyCacheUtils.get(PermissionConstants.PERMISSION_CHECK_CACHE_DOMAIN,
					PermissionConstants.PERMISSION_CHECK_CACHE_KEY)));
			if (time > timeTag) {
				timeTag = time;
				return true;
			}
		} catch (Exception e) {
			timeTag = System.currentTimeMillis();
			MyCacheUtils.put(PermissionConstants.PERMISSION_CHECK_CACHE_DOMAIN, PermissionConstants.PERMISSION_CHECK_CACHE_KEY, timeTag);
			return true;
		}
		return false;
	}

	static CheckStatus hasRole(Long accountId, String roleName) {
		if (needRefresh()) {
			return CheckStatus.NO_CACHE;
		}
		if (userRoleMap.containsKey(accountId) && userRoleMap.get(accountId).contains(roleName)) {
			return CheckStatus.PASS;
		}
		return CheckStatus.FAIL;
	}

	static CheckStatus hasPermission(Long accountId, String type) {
		if (needRefresh()) {
			return CheckStatus.NO_CACHE;
		}
		if (userRoleMap.containsKey(accountId)) {
			List<String> roles = userRoleMap.get(accountId);
			for (String role : roles) {
				if (roleResourceMap.containsKey(role)) {
					for (Resource resource : roleResourceMap.get(role)) {
						if (type.equals(resource.getType())) {
							return CheckStatus.PASS;
						}
					}
				}
			}
		}
		return CheckStatus.FAIL;
	}

	static CheckStatus canVisit(Long accountId, String type, String url) {
		if (needRefresh()) {
			return CheckStatus.NO_CACHE;
		}
		if (StringUtils.isEmpty(type) || StringUtils.isEmpty(url))
			return CheckStatus.FAIL;
		if (userRoleMap.containsKey(accountId)) {
			List<String> roles = userRoleMap.get(accountId);
			for (String role : roles) {
				if (roleResourceMap.containsKey(role)) {
					for (Resource resource : roleResourceMap.get(role)) {
						if (type.equals(resource.getType()) && url.equals(resource.getUrl())) {
							return CheckStatus.PASS;
						}
					}
				}
			}
		}
		return CheckStatus.FAIL;
	}

	static List<String> getUserRoles(Long accountId) {
		return userRoleMap.get(accountId);
	}
	
	static List<String> getUserResources(Long accountId) {
		List<String> resources = new ArrayList<String>();
		List<String> roles = userRoleMap.get(accountId);
		if( null != roles && roles.size() > 0 ){
			for( String role : roles ){
				List<Resource> resourceList = roleResourceMap.get(role);
				if( null != resourceList && resourceList.size() > 0){
					for( Resource r : resourceList ){
						resources.add(r.getType());
					}
				}
			}
		}
		return resources;
	}

	static Set<Resource> getAllResources() {
		Set<Resource> resources = new HashSet<Resource>(RESOURCES);
		return resources;
	}

	static Map<Long, List<String>> getUserRoleMap() {
		return userRoleMap;
	}

	static Map<String, List<Resource>> getRoleResourceMap() {
		return roleResourceMap;
	}

	static List<Resource> getResources() {
		return RESOURCES;
	}

	static List<Role> getRoles() {
		return ROLES;
	}
}
