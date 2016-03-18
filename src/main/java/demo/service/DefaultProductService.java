package demo.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

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
	public Iterable<Product> getAllOrdered() {
		return productRepository.findAllOrdered();
	}

	@Override
	public Iterable<Product> getAllOrderedKeysFirstApproach() {
		return productRepository.findAllOrderedKeysFirstApproach();
	}

	@Override
	public Product getByCode(String code) {
		return productRepository.findOne(code);
	}

	@Override
	public Product save(Product product) {
		return productRepository.save(product);
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

	@Override
	public Iterable<Product> getAllUnordered() {
		return productRepository.findAllUnordered();
	}

	@Override
	public Iterable<Product> getAllUnorderedKeysFirstApproach() {
		return productRepository.findAllUnorderedKeysFirstApproach();
	}
}
