package demo.service;

import demo.model.Product;

public interface ProductService {

	Iterable<Product> getAll();

	Product getByCode(String code);

	Product save(Product product);

	void update(String sku, Product product);

	void deleteProduct(String sku);
}
