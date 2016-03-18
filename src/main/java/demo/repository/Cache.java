package demo.repository;

import java.util.Collection;
import java.util.Map;

public interface Cache<T> {

	void put(String key, T val);
	
	T get(String key);

	Collection<T> getAllOrdered();
	
	Collection<T> getAllUnordered();
	
	Collection<T> getAllUnorderedKeysFirstApproach();
	
	void remove(String key);

	<C extends Comparable<C>> Collection<C> getAllOrderedKeysFirstApproach(Class<C> type);

	Map<String, T> getAllByCodes(Iterable<String> codes);
}
