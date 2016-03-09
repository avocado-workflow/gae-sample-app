package demo.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import demo.model.Product;
import demo.repository.ProductRepository;

@Service
public class DefaultProductService implements ProductService {

	@Resource(name = "googleDatastoreProductRepository")
	private ProductRepository productRepository;

	@Override
	public Iterable<Product> getAll() {
		return productRepository.findAll();
	}

	@Override
	public Product getBySku(String sku) {
		return productRepository.findOne(sku);
	}

	@Override
	public Product save(Product product) {
		return productRepository.save(product);
	}

	@Override
	public void update(String sku, Product product) {
		product.setSku(sku);
		productRepository.update(product);
	}

	@Override
	public void deleteProduct(String sku) {
		productRepository.deleteBySku(sku);
	}
	
}
