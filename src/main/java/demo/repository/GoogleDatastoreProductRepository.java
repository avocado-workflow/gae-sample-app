package demo.repository;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.googlecode.objectify.ObjectifyService;

import demo.model.Product;

@Repository
public class GoogleDatastoreProductRepository implements ProductRepository {

	@Override
	public Iterable<Product> findAll() {
		return ObjectifyService.ofy().load().type(Product.class).list();
	}

	@Override
	public Product findOne(String sku) {
		return ObjectifyService.ofy().load().type(Product.class).id(sku).now();
	}

	@Override
	public Product save(Product product) {
		product.setSku(UUID.randomUUID().toString());
	    ObjectifyService.ofy().save().entity(product).now();
		return product;
	}
}
