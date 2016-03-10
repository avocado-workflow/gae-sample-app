package demo.repository;

public interface Cache<V> {

	void put(String key, V val);
	
	V get(String key, Class<V> type);

	void remove(String key);
}
