package demo.repository;

import demo.model.Product;

public interface ProductRepository {

	Iterable<Product> findAll();

	Product findOne(String sku);

	Product save(Product product);
}
