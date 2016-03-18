package demo.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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

	@Override
	public Collection<T> getAllUnordered() {
		Object cachedValue = cache.get(keysForAllRegistry.get(type));
		if (cachedValue == null) {

			ObjectifyService.ofy().clear();

			Measurement m = new Measurement("BasicCacheImpl", "getAllUnordered");
			List<T> entitiesFromDatastore = ObjectifyService.ofy().cache(false).load().type(type).list();
			profiler.submitMeasurementAsync(m);

			m = new Measurement("BasicCacheImpl", "loopingToMaterialize");
			for (T t : entitiesFromDatastore) {
				t.hashCode();
			}
			profiler.submitMeasurementAsync(m);

			m = new Measurement("BasicCacheImpl", "loopingOverMaterialized");
			for (T t : entitiesFromDatastore) {
				t.hashCode();
			}
			profiler.submitMeasurementAsync(m);

			m = new Measurement("BasicCacheImpl", "putInMemcache");
			cache.put(keysForAllRegistry.get(type), entitiesFromDatastore);
			profiler.submitMeasurementAsync(m);

			return entitiesFromDatastore;
		} else {
			return (Collection<T>) cachedValue;
		}
	}

	@Override
	public Collection<T> getAllOrdered() {
		Object cachedValue = cache.get(keysForAllRegistry.get(type));
		if (cachedValue == null) {

			ObjectifyService.ofy().clear();

			Measurement m = new Measurement("BasicCacheImpl", "getAllOrdered");
			List<T> entitiesFromDatastore = ObjectifyService.ofy().cache(false).load().type(type).order("-sortOrder").order("name").list();
			profiler.submitMeasurementAsync(m);

			m = new Measurement("BasicCacheImpl", "loopingToMaterialize");
			for (T t : entitiesFromDatastore) {
				t.hashCode();
			}
			profiler.submitMeasurementAsync(m);

			m = new Measurement("BasicCacheImpl", "loopingOverMaterialized");
			for (T t : entitiesFromDatastore) {
				t.hashCode();
			}
			profiler.submitMeasurementAsync(m);

			m = new Measurement("BasicCacheImpl", "putInMemcache");
			cache.put(keysForAllRegistry.get(type), entitiesFromDatastore);
			profiler.submitMeasurementAsync(m);

			return entitiesFromDatastore;
		} else {
			return (Collection<T>) cachedValue;
		}
	}

	@Override
	public Collection<T> getAllUnorderedKeysFirstApproach() {
		Object cachedValue = cache.get(keysForAllRegistry.get(type));
		if (cachedValue == null) {

			ObjectifyService.ofy().clear();

			Measurement m = new Measurement("BasicCacheImpl", "getAllKeysOnly");
			List<Key<T>> keys = ObjectifyService.ofy().cache(false).load().type(type).keys().list();
			profiler.submitMeasurementAsync(m);

			m = new Measurement("BasicCacheImpl", "loopingOverKeys1");
			for (Key<T> key : keys) {
				key.hashCode();
			}
			profiler.submitMeasurementAsync(m);

			m = new Measurement("BasicCacheImpl", "loopingOverKeys2");
			for (Key<T> key : keys) {
				key.hashCode();
			}
			profiler.submitMeasurementAsync(m);

			ObjectifyService.ofy().clear();

			m = new Measurement("BasicCacheImpl", "getAllByKeys");
			Map<Key<T>, T> entitiesMap = ObjectifyService.ofy().cache(false).load().keys(keys);
			profiler.submitMeasurementAsync(m);

			Collection<T> entities = null;

			m = new Measurement("BasicCacheImpl", "getValuesFromMap1");
			entities = entitiesMap.values();
			profiler.submitMeasurementAsync(m);

			m = new Measurement("BasicCacheImpl", "getValuesFromMap2");
			entities = entitiesMap.values();
			profiler.submitMeasurementAsync(m);

			m = new Measurement("BasicCacheImpl", "loopingToMaterialize");
			for (T t : entities) {
				t.hashCode();
			}
			profiler.submitMeasurementAsync(m);

			m = new Measurement("BasicCacheImpl", "loopingOverMaterialized");
			for (T t : entities) {
				t.hashCode();
			}
			profiler.submitMeasurementAsync(m);

			m = new Measurement("BasicCacheImpl", "putInMemcache");
			List<T> entitiesList = new LinkedList<T>(entities);
			cache.put(keysForAllRegistry.get(type), entitiesList);
			profiler.submitMeasurementAsync(m);

			return entities;
		} else {
			return (Collection<T>) cachedValue;
		}
	}

	@Override
	public <C extends Comparable<C>> Collection<C> getAllOrderedKeysFirstApproach(Class<C> type) {

		Object cachedValue = cache.get(keysForAllRegistry.get(type));
		if (cachedValue == null) {

			ObjectifyService.ofy().clear();

			Measurement m = new Measurement("BasicCacheImpl", "getAllKeysOnly");
			List<Key<C>> keys = ObjectifyService.ofy().cache(false).load().type(type).keys().list();
			profiler.submitMeasurementAsync(m);

			m = new Measurement("BasicCacheImpl", "loopingOverKeys1");
			for (Key<C> key : keys) {
				key.hashCode();
			}
			profiler.submitMeasurementAsync(m);

			m = new Measurement("BasicCacheImpl", "loopingOverKeys2");
			for (Key<C> key : keys) {
				key.hashCode();
			}
			profiler.submitMeasurementAsync(m);

			ObjectifyService.ofy().clear();

			m = new Measurement("BasicCacheImpl", "getAllByKeys");
			Map<Key<C>, C> entitiesMap = ObjectifyService.ofy().cache(false).load().keys(keys);
			profiler.submitMeasurementAsync(m);

			Collection<C> entities = null;

			m = new Measurement("BasicCacheImpl", "getValuesFromMap1");
			entities = entitiesMap.values();
			profiler.submitMeasurementAsync(m);

			m = new Measurement("BasicCacheImpl", "getValuesFromMap2");
			entities = entitiesMap.values();
			profiler.submitMeasurementAsync(m);

			m = new Measurement("BasicCacheImpl", "convertingToList");
			LinkedList<C> entitiesList = new LinkedList<C>(entities);
			profiler.submitMeasurementAsync(m);

			m = new Measurement("BasicCacheImpl", "sortingAllByKeys");
			Collections.sort(entitiesList);
			profiler.submitMeasurementAsync(m);

			m = new Measurement("BasicCacheImpl", "putInMemcache");
			cache.put(keysForAllRegistry.get(type), entitiesList);
			profiler.submitMeasurementAsync(m);

			return entitiesList;
		} else {
			return (Collection<C>) cachedValue;
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
	public Map<String, T> getAllByCodes(Iterable<String> codes) {
		ObjectifyService.ofy().clear();

		List<Key<T>> keys = new ArrayList<>();
		for (String code : codes) {
			keys.add(Key.create(type, code));
		}

		Map<Key<T>, T> entitiesMap = ObjectifyService.ofy().cache(false).load().keys(keys);

		Map<String, T> entitiesToReturn = new HashMap<>();
		for (Entry<Key<T>, T> entry : entitiesMap.entrySet()) {
			entitiesToReturn.put(entry.getKey().getName(), entry.getValue());
		}

		return entitiesToReturn;
	}
}
