package demo.service;

import demo.dto.ProductResource;

public interface ProductService {

	Iterable<ProductResource> getAll();

	ProductResource getBySku(String sku);

	ProductResource save(ProductResource product);

	void update(String sku, ProductResource product);
}
