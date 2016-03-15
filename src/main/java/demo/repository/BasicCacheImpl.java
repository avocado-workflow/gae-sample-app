package demo.repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.googlecode.objectify.ObjectifyService;

import demo.model.Order;
import demo.model.Product;

//@Component
public class BasicCacheImpl<T> implements Cache<T> {

	private static Map<Class<?>, String> keysForAllRegistry = new HashMap<Class<?>, String>();

	static {
		keysForAllRegistry.put(Product.class, "ALL_PRODUCTS");
		keysForAllRegistry.put(Order.class, "ALL_ORDERS");
	}

	private MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
	
	private Class<T> type;
	
	public BasicCacheImpl(Class<T> type) {
		this.type = type;
	}
	
	@Override
	public void put(String key, Object value) {
		ObjectifyService.ofy().save().entity(value).now();
		cache.put(key, value);
		cache.delete(keysForAllRegistry.get(type));
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<T> getAllOrdered() {
		Object cachedValue = cache.get(keysForAllRegistry.get(type));
		if (cachedValue == null) {
			List<T> entitiesFromDatastore = ObjectifyService.ofy().load().type(type).order("-sortOrder").order("name").list();
			cache.put(keysForAllRegistry.get(type), entitiesFromDatastore);
			return entitiesFromDatastore;
		} else {
			return (Collection<T>)cachedValue;
		}
	}

	@Override
	public T get(String key) {
		Object value = cache.get(key);
		if (value == null) {
			value = ObjectifyService.ofy().load().type(type).id(key).now();
			if (value != null) {
				cache.put(key, value);
			}
		}
		return (T) value;
	}

	@Override
	public void remove(String key) {
		ObjectifyService.ofy().delete().type(type).id(key).now();
		cache.delete(key);
		cache.delete(keysForAllRegistry.get(type));
	}
}
