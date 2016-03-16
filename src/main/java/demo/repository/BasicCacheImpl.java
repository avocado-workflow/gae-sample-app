package demo.repository;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;

import demo.model.Measurement;
import demo.model.Order;
import demo.model.Product;
import demo.util.Profiler;

//@Component
public class BasicCacheImpl<T> implements Cache<T> {

	@Resource
	private Profiler profiler;
	
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
			Measurement m = new Measurement("BasicCacheImpl", "getAllOrdered");
			m.setStartTime(System.currentTimeMillis());
			
			List<T> entitiesFromDatastore = ObjectifyService.ofy().cache(false).load().type(type).order("-sortOrder").order("name").list();

			m.setEndTime(System.currentTimeMillis());
			profiler.submitMeasurementAsync(m);

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

	@Override
	public <C extends Comparable<C>> Collection<C> getAllOrderedKeysFirstApproach(Class<C> type) {

		Object cachedValue = cache.get(keysForAllRegistry.get(type));
		if (cachedValue == null) {
			
			Measurement m = new Measurement("BasicCacheImpl", "getAllKeysOnly");
			m.setStartTime(System.currentTimeMillis());
			
			List<Key<C>> keys = ObjectifyService.ofy().cache(false).load().type(type).keys().list();
			
			m.setEndTime(System.currentTimeMillis());
			profiler.submitMeasurementAsync(m);

			m = new Measurement("BasicCacheImpl", "getAllByKeys");
			m.setStartTime(System.currentTimeMillis());
			
			Map<Key<C>, C> entitiesMap = ObjectifyService.ofy().cache(false).load().keys(keys);

			m.setEndTime(System.currentTimeMillis());
			profiler.submitMeasurementAsync(m);

			List<C> entities  = new LinkedList<C>(entitiesMap.values());
			
			Collections.sort(entities);
			
			cache.put(keysForAllRegistry.get(type), entities);
			return entities;
		} else {
			return (Collection<C>)cachedValue;
		}
	}
}
