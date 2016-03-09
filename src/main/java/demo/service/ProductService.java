package demo.service;

import demo.model.Product;

public interface ProductService {

	Iterable<Product> getAll();

	Product getBySku(String sku);

	Product save(Product product);

	void update(String sku, Product product);

	void deleteProduct(String sku);
}
