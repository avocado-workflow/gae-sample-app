package demo.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import demo.model.Measurement;
import demo.model.Product;
import demo.repository.ProductRepository;
import demo.util.Profiler;

@Service
public class DefaultProductService implements ProductService {

	@Resource
	private Profiler profiler;
	
	@Resource(name = "googleDatastoreProductRepository")
	private ProductRepository productRepository;

	@Override
	public Iterable<Product> getAll() {
		Measurement m = new Measurement("DefaultProductService", "getAll");
		m.setStartTime(System.currentTimeMillis());
		
		Iterable<Product> allProducts = productRepository.findAllOrdered();

		m.setEndTime(System.currentTimeMillis());
		profiler.submitMeasurementAsync(m);
		
		return allProducts;
	}

	@Override
	public Product getByCode(String code) {
		Measurement m = new Measurement("DefaultProductService", "getByCode");
		m.setStartTime(System.currentTimeMillis());
		
		Product product = productRepository.findOne(code);
		
		m.setEndTime(System.currentTimeMillis());
		profiler.submitMeasurementAsync(m);

		return product;
	}

	@Override
	public Product save(Product product) {
		Measurement m = new Measurement("DefaultProductService", "save");
		m.setStartTime(System.currentTimeMillis());

		Product savedProduct = productRepository.save(product);
		
		m.setEndTime(System.currentTimeMillis());
		profiler.submitMeasurementAsync(m);

		return savedProduct;
	}

	@Override
	public void update(String sku, Product product) {
		product.setCode(sku);
		productRepository.update(product);
	}

	@Override
	public void deleteProduct(String sku) {
		productRepository.deleteBySku(sku);
	}
	
}
