package demo.web;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import demo.model.Measurement;
import demo.model.Product;
import demo.service.ProductService;
import demo.util.Profiler;

@RestController
@RequestMapping("/products")
public class ProductController {

	@Resource
	private ProductService productService;

	@Resource
	private Profiler profiler;
	
	@RequestMapping(method = RequestMethod.GET)
	public Iterable<Product> getAllProductsUnordered() {
		Measurement m = new Measurement("ProductController", "getAllProductsUnordered");
		
		Iterable<Product> products = productService.getAllUnordered();
		
		profiler.submitMeasurementAsync(m);
		return products;
	}

	@RequestMapping(method = RequestMethod.GET, params="sort")
	public Iterable<Product> getAllProductsOrdered() {
		Measurement m = new Measurement("ProductController", "getAllProductsOrdered");

		Iterable<Product> products = productService.getAllOrdered();

		profiler.submitMeasurementAsync(m);
		return products;
	}
	
	@RequestMapping(method = RequestMethod.GET, params="keysfirst")
	public Iterable<Product> getAllProductsUnorderedKeysFirstApproach() {
		Measurement m = new Measurement("ProductController", "getAllProductsUnorderedKeysFirstApproach");
		
		Iterable<Product> products = productService.getAllUnorderedKeysFirstApproach();
		
		profiler.submitMeasurementAsync(m);
		return products;
	}

	@RequestMapping(method = RequestMethod.GET, params={"sort","keysfirst"})
	public Iterable<Product> getAllProductsOrderedKeysFirstApproach() {

		Measurement m = new Measurement("ProductController", "getAllProductsOrderedKeysFirstApproach");

		Iterable<Product> products = productService.getAllOrderedKeysFirstApproach();

		profiler.submitMeasurementAsync(m);
		return products;
	}

	@RequestMapping(value = "/{code}", method = RequestMethod.GET)
	public ResponseEntity<?> getProductByCode(@PathVariable String code) {
		Measurement m = new Measurement("ProductController", "getProductByCode");

		Product product = productService.getByCode(code);
		if (product == null) {
			return new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);
		}

		ResponseEntity<Product> responseEntity = new ResponseEntity<>(product, HttpStatus.OK);

		profiler.submitMeasurementAsync(m);

		return responseEntity;
	}

	@RequestMapping(value = "/{sku}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable String sku) {
		productService.deleteProduct(sku);
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public Product createProduct(@RequestBody Product product) {
		Measurement m = new Measurement("ProductController", "createProduct");

		Product savedProduct = productService.save(product);

		profiler.submitMeasurementAsync(m);

		return savedProduct;
	}

	@RequestMapping(value = "/{sku}", method = RequestMethod.PUT)
	public void updateProduct(@PathVariable String sku, @RequestBody Product menu) {
		productService.update(sku, menu);
	}
}
