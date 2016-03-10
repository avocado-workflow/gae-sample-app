package demo.repository;

import java.util.UUID;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.googlecode.objectify.ObjectifyService;

import demo.model.Product;

@Repository
public class GoogleDatastoreProductRepository implements ProductRepository {

	@Resource
	private Cache<Product> cache;

	@Override
	public Iterable<Product> findAll() {ObjectifyService.ofy().clear();
		// TODO : no cache yet. Implement this
		return ObjectifyService.ofy().load().type(Product.class).list();
	}

	@Override
	public Product findOne(String sku) {
		return cache.get(sku, Product.class);
	}

	@Override
	public Product save(Product product) {
		product.setSku(UUID.randomUUID().toString());
		cache.put(product.getSku(), product);
		return product;
	}

	@Override
	public void deleteBySku(String sku) {
		cache.remove(sku);
	}

	@Override
	public void update(Product product) {
		cache.put(product.getSku(), product);
	}
}
