package com.sogou.bizdev.security.po;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class Resource implements Serializable {

	/**
	 * 如果用户拥有该资源的角色{@link Role}，则该用户能够使用该资源。
	 * 资源可以只是一个虚拟的，即resource字段为空，这个时候表示该资源只是一种虚拟资源
	 * ，这种情形下控制的是用户的ID，如果resource字段不为空，则是表示具体的资源
	 */
	private static final long serialVersionUID = -8306621075417209410L;
	private Long id;
	private String type;
	private String url;
	private boolean active;

	public Resource() {

	}

	public Resource(String type, String url) {
		super();
		this.type = type;
		this.url = url;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public String toString() {
		return "Resource [id=" + id + ", type=" + type + ", url=" + url + ", active=" + active + "]";
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}
}
