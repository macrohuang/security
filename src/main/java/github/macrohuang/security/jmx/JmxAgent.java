package github.macrohuang.security.jmx;

import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

import com.sun.jdmk.comm.AuthInfo;
import com.sun.jdmk.comm.HtmlAdaptorServer;

public abstract class JmxAgent implements InitializingBean {

	private final Logger log = Logger.getLogger(JmxAgent.class);

	public abstract Integer getJmxHtmlAdapterPort();
	
	public abstract String getJmxHtmlAdapterName();

	public abstract String getUserName();

	public abstract String getPassword();

	public abstract Map<String, Object> getMBean();

	private void create() throws Exception {
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
		Map<String, Object> mbean = getMBean();
		if (null != mbean && !mbean.isEmpty()) {
			Set<Map.Entry<String, Object>> set = mbean.entrySet();
			for (Map.Entry<String, Object> en : set) {
				ObjectName constantsName = new ObjectName(en.getKey() + ":name=" + en.getKey());
				server.registerMBean(en.getValue(), constantsName);
			}
		}
		ObjectName adapterName = new ObjectName(getJmxHtmlAdapterName() + ":name=htmladapter,port=" + getJmxHtmlAdapterPort());
		HtmlAdaptorServer adapter = new HtmlAdaptorServer();
		adapter.setPort(getJmxHtmlAdapterPort());
        AuthInfo login = new AuthInfo();
		login.setLogin(StringUtils.isBlank(getUserName()) ? "" : getUserName());
		login.setPassword(StringUtils.isBlank(getPassword()) ? "" : getPassword());
		adapter.addUserAuthenticationInfo(login);
		server.registerMBean(adapter, adapterName);
        adapter.start();

		log.info("JmxAgent start in " + getJmxHtmlAdapterPort() + ".....");
    }    

	@Override
	public void afterPropertiesSet() throws Exception {
		create();
	}   
} 