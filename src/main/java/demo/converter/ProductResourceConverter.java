package demo.converter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import demo.dto.ProductResource;
import demo.model.Product;

@Component("productResourceConverter")
public class ProductResourceConverter {

	public Product convert(ProductResource product) {
		Product result = new Product();
		result.setName(product.getName());
		result.setDescription(product.getDescription());
		result.setSku(product.getSku());
		result.setPrice(product.getPrice());
		return result;
	}
	
	public Iterable<Product> convertAll(Iterable<ProductResource> products) {
		List<Product> results = new ArrayList<>();
		for (ProductResource product : products) {
			results.add(convert(product));
		}
		return results ;
	}
}
