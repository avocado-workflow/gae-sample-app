package demo.repository;

import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

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
	public Iterable<Product> findAllUnordered() {
		return cache.getAllUnordered();
	}

	@Override
	public Iterable<Product> findAllUnorderedKeysFirstApproach() {
		return cache.getAllUnorderedKeysFirstApproach();
	}

	@Override
	public Iterable<Product> findAllOrdered() {
		return cache.getAllOrdered();
	}

	@Override
	public Iterable<Product> findAllOrderedKeysFirstApproach() {
		return cache.getAllOrderedKeysFirstApproach(Product.class);
	}

	@Override
	public Product findOne(String code) {
		return cache.get(code);
	}

	@Override
	public Product save(Product product) {
		Measurement m = new Measurement("ProductRepository", "save");

		product.setCode(UUID.randomUUID().toString());
		cache.put(product.getCode(), product);

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

	@Override
	public Map<String, Product> findAllByCodes(Iterable<String> codes) {
		return cache.getAllByCodes(codes);
	}
}
