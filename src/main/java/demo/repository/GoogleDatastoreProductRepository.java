package demo.repository;

import java.util.UUID;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.googlecode.objectify.ObjectifyService;

import demo.model.Product;

@Repository
public class GoogleDatastoreProductRepository implements ProductRepository {

	@Resource(name = "productsCache")
	private Cache<Product> cache;

	@Override
	public Iterable<Product> findAllOrdered() {
		return cache.getAllOrdered();
	}

	@Override
	public Product findOne(String sku) {
		return cache.get(sku);
	}

	@Override
	public Product save(Product product) {
		product.setCode(UUID.randomUUID().toString());
		cache.put(product.getCode(), product);
		return product;
	}

	@Override
	public void deleteBySku(String sku) {
		cache.remove(sku);
	}

	@Override
	public void update(Product product) {
		cache.put(product.getCode(), product);
	}
}
