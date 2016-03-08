package demo.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.google.apphosting.api.ApiProxy;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;

import demo.EmbeddedDataStore;
import demo.IntegrationTests;
import demo.model.Product;

@Category(IntegrationTests.class)
// @RunWith(SpringJUnit4ClassRunner.class)
// @SpringApplicationConfiguration(classes = Application.class)
// @WebIntegrationTest("server.port=8889")
public class ProductControllerTest extends BaseIntegrationTest{

	@Rule
	public EmbeddedDataStore store = new EmbeddedDataStore();

	// @Resource(name = "googleDatastoreRepository")
	// private ProductRepository productRepository;

	// @Value("${local.server.port}")
	private int port = 8080;

	private String baseUrl = "http://localhost:" + port;
	
	Closeable session;

	@Before
	public void setUp() {
		System.out.println(ApiProxy.getCurrentEnvironment().getAppId());
		session = ObjectifyService.begin();
		
		ObjectifyService.register(Product.class);
//		
//		System.out.println("SIZE1: " + productsToDelete.size());
//		System.out.println(ObjectifyService.ofy().load().key(productsToDelete.get(0)).now());
//		System.out.println(ObjectifyService.ofy().load().key(productsToDelete.get(0)).now());
//		productsToDelete = ObjectifyService.ofy().load().type(Product.class).keys().list();
//		System.out.println("SIZE2: " + productsToDelete.size());
//		
//		productsToDelete = ObjectifyService.ofy().load().type(Product.class).keys().list();
//		System.out.println("SIZE3: " + productsToDelete.size());
		
	}

	//
	@After
	public void tearDown() {
		List<Key<Product>> productsToDelete = ObjectifyService.ofy().load().type(Product.class).keys().list();
		System.out.println(ObjectifyService.ofy().delete().keys(productsToDelete).now());
		session.close();
	}

	@Test
	public void testGetAllProducts() throws Exception {
		ResponseEntity<Product[]> responseEntity = testRestTemplate.getForEntity(baseUrl + "/products",
				Product[].class);

		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		Product[] returnedProducts = responseEntity.getBody();
		assertEquals(3, returnedProducts.length);
	}

	@Test
	public void testProductCreation() throws Exception {
		// Given
		Product product = new Product();
		product.setName("Lemon");
		product.setPrice(5.87);
		product.setDescription("Fresh tropic lemons");

		// When
		ResponseEntity<Product> responseEntity = testRestTemplate.postForEntity(baseUrl + "/products", product,
				Product.class);

		// Then
		assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

		Product createdProduct = responseEntity.getBody();
		assertEquals(product.getName(), createdProduct.getName());
		assertEquals(product.getDescription(), createdProduct.getDescription());
		assertEquals(product.getPrice(), createdProduct.getPrice());
		assertNotNull(createdProduct.getSku());
	}

	@Test
	public void testGetBySku() throws Exception {
		// Given
		Product expectedProduct = new Product();
		expectedProduct.setName("Lemon");
		expectedProduct.setPrice(5.87);
		expectedProduct.setDescription("Fresh tropic lemons");
		expectedProduct.setSku("0c7cbf98-7c03-44b4-bb8c-867d8aebac83");
		// product = productRepository.save(product);

		String skuToFind = "0c7cbf98-7c03-44b4-bb8c-867d8aebac83"; // TODO:
																	// where to
																	// get it???

		// When
		Product returnedProduct = testRestTemplate.getForObject(baseUrl + "/products/" + skuToFind,
				Product.class);

		// Then
		assertEquals(expectedProduct, returnedProduct);
	}
}
