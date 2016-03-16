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
import demo.util.RequestIdProvider;

@RestController
@RequestMapping("/products")
public class ProductController {

	@Resource
	private ProductService productService;

	@Resource
	private Profiler profiler;
	
	@RequestMapping(method = RequestMethod.GET)
	public Iterable<Product> getAllProducts() {
		Measurement m = new Measurement("ProductController", "getAllProducts");
		
		m.setStartTime(System.currentTimeMillis());
		Iterable<Product> products = productService.getAll();
		m.setEndTime(System.currentTimeMillis());
		
		profiler.submitMeasurementAsync(m);
		return products;
	}

	@RequestMapping(value="/keysfirst", method = RequestMethod.GET)
	public Iterable<Product> getAllKeysFirstApproach() {
		Measurement m = new Measurement("ProductController", "getAllKeysFirstApproach");
		
		m.setStartTime(System.currentTimeMillis());
		Iterable<Product> products = productService.getAllKeysFirstApproach();
		m.setEndTime(System.currentTimeMillis());
		
		profiler.submitMeasurementAsync(m);
		return products;
		
	}
	@RequestMapping(value = "/{code}", method = RequestMethod.GET)
	public ResponseEntity<?> getProductByCode(@PathVariable String code) {
		Measurement m = new Measurement("ProductController", "getProductByCode");
		m.setStartTime(System.currentTimeMillis());

		Product product = productService.getByCode(code);
		if (product == null) {
			return new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);
		}
		
		ResponseEntity<Product> responseEntity = new ResponseEntity<>(product, HttpStatus.OK);

		m.setEndTime(System.currentTimeMillis());
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
		m.setStartTime(System.currentTimeMillis());
		
		Product savedProduct = productService.save(product);
		
		m.setEndTime(System.currentTimeMillis());
		profiler.submitMeasurementAsync(m);
		
		return savedProduct;
	}

	@RequestMapping(value = "/{sku}", method = RequestMethod.PUT)
	public void updateProduct(@PathVariable String sku, @RequestBody Product menu) {
		productService.update(sku, menu);
	}
}
