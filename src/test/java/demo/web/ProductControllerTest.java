package demo.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import demo.IntegrationTests;
import demo.model.Product;

@Category(IntegrationTests.class)
public class ProductControllerTest extends BaseIntegrationTest {

	private int port = 8080;

	private String baseUrl = "http://localhost:" + port;

	private Product product1;
	
	private Product product2;
	
	@Before
	public void setUp() {
		initDataStore();
	}

	private void initDataStore() {
		// TODO:replace these http requests with ofy() calls;
		product1 = new Product();
		product1.setName("Lemon1");
		product1.setPrice(51.87);
		product1.setDescription("Fresh tropic lemons 1");
		
		ResponseEntity<Product> responseEntity = testRestTemplate.postForEntity(baseUrl + "/products", product1, Product.class);
		assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

		product1 = responseEntity.getBody();
		
		product2 = new Product();
		product2.setName("Lemon2");
		product2.setPrice(15.87);
		product2.setDescription("Fresh tropic lemons 2");
		responseEntity = testRestTemplate.postForEntity(baseUrl + "/products", product2, Product.class);
		assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

		product2 = responseEntity.getBody();
	}

	@After
	public void tearDown() {
		testRestTemplate.delete(baseUrl + "/products/" + product1.getSku());
		testRestTemplate.delete(baseUrl + "/products/" + product2.getSku());
	}

	@Test
	public void testGetAllProducts() throws Exception {
		ResponseEntity<Product[]> responseEntity = testRestTemplate.getForEntity(baseUrl + "/products", Product[].class);

		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		Product[] returnedProducts = responseEntity.getBody();
//		assertEquals(2, returnedProducts.length);
		List<Product> productsList = Arrays.asList(returnedProducts);
		assertTrue(productsList.size() > 1);
		assertTrue(productsList.contains(product1));
		assertTrue(productsList.contains(product2));
	}

	@Test
	public void testProductCreation() throws Exception {
		// Given
		Product product = new Product();
		product.setName("Lemon");
		product.setPrice(5.87);
		product.setDescription("Fresh tropic lemons");

		// When
		ResponseEntity<Product> responseEntity = testRestTemplate.postForEntity(baseUrl + "/products", product, Product.class);

		// Then
		assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

		Product createdProduct = responseEntity.getBody();
		assertEquals(product.getName(), createdProduct.getName());
		assertEquals(product.getDescription(), createdProduct.getDescription());
		assertEquals(product.getPrice(), createdProduct.getPrice());
		assertNotNull(createdProduct.getSku());
		
		// Cleanup
		testRestTemplate.delete(baseUrl + "/products/" + createdProduct.getSku());
	}

	@Test
	public void testProductDeletion() throws Exception {
		ResponseEntity<Product> responseEntity = testRestTemplate.getForEntity(baseUrl + "/products/" + product1.getSku(), Product.class);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

		testRestTemplate.delete(baseUrl + "/products/" + product1.getSku());
		
		ResponseEntity<String> responseWithNotFound = testRestTemplate.getForEntity(baseUrl + "/products/" + product1.getSku(), String.class);
		assertEquals(HttpStatus.NOT_FOUND, responseWithNotFound.getStatusCode());
		assertEquals("Product not found", responseWithNotFound.getBody());

	}

	@Test
	public void testProductUpdate() throws Exception {
		// Given
		Product product = new Product();
		product.setName("Updated Lemon 1 ");
		product.setPrice(15.87);
		product.setDescription("Updated - Fresh tropic lemons 1");
		
		String skuToUpdate = product1.getSku();
		// When
		testRestTemplate.put(baseUrl + "/products/" + product1.getSku(), product);

		// Then
		ResponseEntity<Product> responseEntity = testRestTemplate.getForEntity(baseUrl + "/products/" + skuToUpdate, Product.class);

		// Then
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		
		Product createdProduct = responseEntity.getBody();
		
		assertEquals(product.getName(), createdProduct.getName());
		assertEquals(product.getDescription(), createdProduct.getDescription());
		assertEquals(product.getPrice(), createdProduct.getPrice());
		assertEquals(skuToUpdate, createdProduct.getSku());
		
		// Cleanup
		testRestTemplate.delete(baseUrl + "/products/" + createdProduct.getSku());
	}
	
	@Test
	public void testGetBySku() throws Exception {
		// Given	
		String skuToFind = product1.getSku();

		// When
		ResponseEntity<Product> responseEntity = testRestTemplate.getForEntity(baseUrl + "/products/" + skuToFind, Product.class);

		// Then
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertEquals(product1, responseEntity.getBody());
	}
}
