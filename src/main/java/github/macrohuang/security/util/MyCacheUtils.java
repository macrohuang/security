package github.macrohuang.security.util;

import java.util.HashMap;
import java.util.Map;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class MyCacheUtils {
	private static final CacheManager cacheManager = new CacheManager();
	private static Map<String,Ehcache> ehcacheMap = new HashMap<String, Ehcache>();
	private static final Logger log = Logger.getLogger(MyCacheUtils.class);

	private synchronized static Ehcache getCache(String domain) {
		if(StringUtils.isBlank(domain)) {
			throw new IllegalArgumentException("param in null.");
		}

		if(null == ehcacheMap.get(domain)) {
			Ehcache ehcache = cacheManager.getEhcache(domain);
			ehcacheMap.put(domain, ehcache);
		}

		Ehcache ehcache = ehcacheMap.get(domain);
		if(null == ehcache) {
			log.error("get ehcache null by:" + domain);
		}

		return ehcache;
	}

	public static void put(String domain, String key, Object o) {
		Element element = new Element(key, o);
	    getCache(domain).put(element);
	}

	/**
	 * 
	 * @param domain
	 * @param key
	 * @param o
	 * @param eternal
	 * @param timeToIdleSeconds 空闲时间，如果大于这个时间从没有被get过，则delete
	 * @param timeToLiveSeconds 生存时间，大于这个时间后立即delete
	 * @param version
	 */
	public static void put(String domain, String key, Object o, boolean eternal, int timeToIdleSeconds, int timeToLiveSeconds, long version) {
		Element element = new Element(key, o);
		element.setEternal(eternal);
		element.setTimeToIdle(timeToIdleSeconds);
		element.setTimeToLive(timeToLiveSeconds);
		element.setVersion(version);
		getCache(domain).put(element);
	}

	public static Object get(String domain, String key) {
	    Element elem = getCache(domain).get(key);
	    return elem != null ? elem.getValue() : null;
	}

	public static boolean delete(String domain, String key) {
		Element e = getCache(domain).get(key);
		if(null != e) {
			return getCache(domain).removeElement(e);
		} else {
			return true;
		}
	}
	
	public static String getCacheKey(Long accountId, Long taskInstanceId) {
		return "cache_key_report_data_" + "_" + accountId + "_" + taskInstanceId;
	}	

}
