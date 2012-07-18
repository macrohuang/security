package com.sogou.bizdev.security.po;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class AccountRoleMap implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5238727277487064561L;
	private Long accountId;
	private Long roleId;

	public AccountRoleMap(Long accountId, Long roleId) {
		super();
		this.accountId = accountId;
		this.roleId = roleId;
	}

	public AccountRoleMap() {
		super();
	}

	public Long getAccountId() {
		return accountId;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
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
		return "AccountRoleMap [accountId=" + accountId + ", roleId=" + roleId + "]";
	}
}
