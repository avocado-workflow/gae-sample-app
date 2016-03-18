package demo.repository;

import java.util.Map;

import demo.model.Product;

public interface ProductRepository {

	Iterable<Product> findAllUnordered();
	
	Iterable<Product> findAllUnorderedKeysFirstApproach();

	Iterable<Product> findAllOrdered();
	
	Iterable<Product> findAllOrderedKeysFirstApproach();
	
	Map<String, Product> findAllByCodes(Iterable<String> codes);

	Product findOne(String sku);

	Product save(Product product);

	void deleteBySku(String sku);

	void update(Product product);
}
