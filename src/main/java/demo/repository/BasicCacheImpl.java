package demo.repository;

import org.springframework.stereotype.Component;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

@Component
public class BasicCacheImpl implements Cache {

	private MemcacheService cache = MemcacheServiceFactory.getMemcacheService();

	@Override
	public void put(Object key, Object value) {
		cache.put(key, value);
	}

	@Override
	public <T> T get(Object key, Class<T> type) {
		Object value = cache.get(key);
		if (type.isInstance(value)) {
			return (T) value;
		}
		return null;
	}
}
