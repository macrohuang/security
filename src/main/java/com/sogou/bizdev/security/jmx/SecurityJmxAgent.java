package com.sogou.bizdev.security.jmx;

import java.util.HashMap;
import java.util.Map;

import com.sogou.bizdev.platform.jmx.JmxAgent;

public class SecurityJmxAgent extends JmxAgent {

	@Override
	public Integer getJmxHtmlAdapterPort() {
		return 8888;
	}

	@Override
	public String getJmxHtmlAdapterName() {
		return "sogou_bizdev_sample_manager";
	}

	@Override
	public String getUserName() {
		return "security";
	}

	@Override
	public String getPassword() {
		return "1qazSE$rfvGY&";
	}

	@Override
	public Map<String, Object> getMBean() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("sogou_biz_sample_manager", new SecurityManage());
		return map;
	}

}
