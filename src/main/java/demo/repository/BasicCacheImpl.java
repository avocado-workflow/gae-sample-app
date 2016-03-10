package demo.repository;

import org.springframework.stereotype.Component;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.googlecode.objectify.ObjectifyService;

import demo.model.Product;

@Component
public class BasicCacheImpl<V> implements Cache<V> {

	// private static Map<Class<?>, Class<?>> keyTypesRegistry = new
	// HashMap<Class<?>, Class<?>>();
	//
	// static{
	// keyTypesRegistry.put(Product.class, String.class);
	// keyTypesRegistry.put(Order.class, Long.class);
	// }

	private MemcacheService cache = MemcacheServiceFactory.getMemcacheService();

	@Override
	public void put(String key, Object value) {
		ObjectifyService.ofy().save().entity(value).now();
		cache.put(key, value);
	}

	@Override
	public V get(String key, Class<V> type) {
		Object value = cache.get(key);
		if (value == null) {
			value = ObjectifyService.ofy().load().type(type).id(key).now();
			if (value != null) {
				cache.put(key, value);
			}
		}
		if (type.isInstance(value)) {
			return (V) value;
		}
		return null;
	}

	@Override
	public void remove(String key) {
		ObjectifyService.ofy().delete().type(Product.class).id(key).now();
		cache.delete(key);
	}
}
