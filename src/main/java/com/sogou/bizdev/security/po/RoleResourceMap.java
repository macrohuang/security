package com.sogou.bizdev.security.po;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class RoleResourceMap implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1841453463554056915L;
	private Long roleId;
	private Long resourceId;

	public RoleResourceMap() {
		super();
	}

	public RoleResourceMap(Long roleId, Long resourceId) {
		super();
		this.roleId = roleId;
		this.resourceId = resourceId;
	}

	public Long getRoleId() {
		return roleId;
	}

	public Long getResourceId() {
		return resourceId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public String toString() {
		return "RoleResourceMap [roleId=" + roleId + ", resourceId=" + resourceId + "]";
	}

}
