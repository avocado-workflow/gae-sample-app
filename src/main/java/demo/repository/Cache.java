package demo.repository;

import java.util.Collection;

public interface Cache<T> {

	void put(String key, T val);
	
	T get(String key);

	Collection<T> getAllOrdered();
	
	void remove(String key);
}
