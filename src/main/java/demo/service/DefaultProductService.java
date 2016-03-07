package demo.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import demo.converter.ProductConverter;
import demo.converter.ProductResourceConverter;
import demo.dto.ProductResource;
import demo.model.Product;
import demo.repository.ProductRepository;

@Service
public class DefaultProductService implements ProductService {

	@Resource(name = "googleDatastoreProductRepository")
	private ProductRepository productRepository;

	@Resource
	private ProductConverter productConverter;

	@Resource
	private ProductResourceConverter productResourceConverter;

	@Override
	public Iterable<ProductResource> getAll() {
		Iterable<Product> products = productRepository.findAll();
		return productConverter.convertAll(products);
	}

	@Override
	public ProductResource getBySku(String sku) {
		Product product = productRepository.findOne(sku);
		Assert.notNull(product);
		return productConverter.convert(product);
	}

	@Override
	public ProductResource save(ProductResource productResource) {
		Product product = productResourceConverter.convert(productResource);
		Product savedProduct = productRepository.save(product);
		return productConverter.convert(savedProduct);
	}

	@Override
	public void update(String sku, ProductResource productResource) {
		productResource.setSku(sku);
		Product product = productResourceConverter.convert(productResource);
		productRepository.save(product);
	}
}
