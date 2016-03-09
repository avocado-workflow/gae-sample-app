package demo.repository;

public interface Cache {

	void put(Object key, Object val);
	
	<T> T get(Object key, Class<T> type);

	void remove(Object key);
}
