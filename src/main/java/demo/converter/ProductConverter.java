package demo.converter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import demo.dto.ProductResource;
import demo.model.Product;

@Component("productConverter")
public class ProductConverter {

	public ProductResource convert(Product product) {
		ProductResource result = new ProductResource();
		result.setName(product.getName());
		result.setDescription(product.getDescription());
		result.setSku(product.getSku());
		result.setPrice(product.getPrice());
		return result;
	}
	
	public Iterable<ProductResource> convertAll(Iterable<Product> products) {
		List<ProductResource> results = new ArrayList<>();
		for (Product product : products) {
			results.add(convert(product));
		}
		return results ;
	}
}
