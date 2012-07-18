package com.sogou.bizdev.security.web.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;

import com.sogou.bizdev.platform.service.ServiceLocator;
import com.sogou.bizdev.security.service.PermissionCheck;

public class PermissionTag extends BodyTagSupport {
	private Long accountId;
	private String role;

	private PermissionCheck getPermissionService() {
		return (PermissionCheck) ServiceLocator.getInstance().getBean("permissionCheck");
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 7593593647544452510L;
	@Override
	public int doStartTag() throws JspException {
		return BodyTag.EVAL_BODY_AGAIN;
	}

	@Override
	public int doEndTag() throws JspException {
		try {
			if (getPermissionService().hasRole(accountId, role)) {
				bodyContent.writeOut(bodyContent.getEnclosingWriter());
			} else {
				pageContext.getOut().println("");
			}
		} catch (IOException e) {
			throw new JspTagException(e);
		}
		return Tag.EVAL_PAGE;
	}
}
