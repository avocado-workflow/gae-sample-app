package demo.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import demo.model.Product;

@Repository
public class DummyProductRepository implements ProductRepository {

	@SuppressWarnings("serial")
	private Map<String, Product> products = new HashMap<String, Product>() {
		{
			Product p = new Product();
			p.setDescription("fresh apples");
			p.setName("apple");
			p.setPrice(4.5);
			p.setSku(UUID.randomUUID().toString());
			put(p.getSku(), p);
		}
	};

	@Override
	public Iterable<Product> findAll() {
		return products.values();
	}

	@Override
	public Product findOne(String sku) {
		return products.get(sku);
	}

	@Override
	public Product save(Product product) {
		return products.put(product.getSku(), product);
	}
}
