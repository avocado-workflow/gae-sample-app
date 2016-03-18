package demo.service;

import demo.model.Product;

public interface ProductService {

	Iterable<Product> getAllUnordered();

	Iterable<Product> getAllUnorderedKeysFirstApproach();

	Iterable<Product> getAllOrdered();

	Iterable<Product> getAllOrderedKeysFirstApproach();

	Product getByCode(String code);

	Product save(Product product);

	void update(String sku, Product product);

	void deleteProduct(String sku);
}
