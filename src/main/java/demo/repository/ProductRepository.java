package demo.repository;

import demo.model.Product;

public interface ProductRepository {

	Iterable<Product> findAllOrdered();

	Product findOne(String sku);

	Product save(Product product);

	void deleteBySku(String sku);

	void update(Product product);
}
