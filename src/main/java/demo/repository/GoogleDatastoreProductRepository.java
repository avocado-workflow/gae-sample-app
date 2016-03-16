package demo.repository;

import java.util.Collection;
import java.util.UUID;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.googlecode.objectify.ObjectifyService;

import demo.model.Measurement;
import demo.model.Product;
import demo.util.Profiler;

@Repository
public class GoogleDatastoreProductRepository implements ProductRepository {

	@Resource(name = "productsCache")
	private Cache<Product> cache;

	@Resource
	private Profiler profiler;
	
	@Override
	public Iterable<Product> findAllOrdered() {
		Measurement m = new Measurement("ProductRepository", "findAllOrdered");
		m.setStartTime(System.currentTimeMillis());
		
		Collection<Product> allProducts = cache.getAllOrdered();
		
		m.setEndTime(System.currentTimeMillis());
		profiler.submitMeasurementAsync(m);
		
		return allProducts;
	}
	
	@Override
	public Iterable<Product> findAllOrderedKeysFirstApproach() {
		Measurement m = new Measurement("ProductRepository", "findAllOrderedKeysFirstApproach");
		m.setStartTime(System.currentTimeMillis());
		
		Collection<Product> allProducts = cache.getAllOrderedKeysFirstApproach(Product.class);
		
		m.setEndTime(System.currentTimeMillis());
		profiler.submitMeasurementAsync(m);
		
		return allProducts;
	}

	@Override
	public Product findOne(String code) {
		Measurement m = new Measurement("ProductRepository", "findOne");
		m.setStartTime(System.currentTimeMillis());
		
		Product product = cache.get(code);
		
		m.setEndTime(System.currentTimeMillis());
		profiler.submitMeasurementAsync(m);
		
		return product;
	}

	@Override
	public Product save(Product product) {
		Measurement m = new Measurement("ProductRepository", "save");
		m.setStartTime(System.currentTimeMillis());
		
		product.setCode(UUID.randomUUID().toString());
		cache.put(product.getCode(), product);

		m.setEndTime(System.currentTimeMillis());
		profiler.submitMeasurementAsync(m);

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
