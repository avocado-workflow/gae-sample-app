package demo.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.Closeable;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.googlecode.objectify.ObjectifyService;

import demo.EmbeddedDataStore;
import demo.IntegrationTests;
import demo.model.Address;
import demo.model.Order;
import demo.model.OrderItem;
import demo.model.Product;

@Category(IntegrationTests.class)
public class OrderControllerTest extends BaseIntegrationTest {

	@Rule
	public EmbeddedDataStore embeddedDataStore = new EmbeddedDataStore();
	
	private int port = 8080;

	private String baseUrl = "http://localhost:" + port;

	private Product product1;
	private Product product2;

	Closeable session;

	@Before
	public void setUp() {

		session = ObjectifyService.begin();

		ObjectifyService.register(Product.class);
		ObjectifyService.register(Order.class);
		ObjectifyService.register(OrderItem.class);

		product1 = new Product();
		product1.setDescription("Int test product 1 description");
		product1.setPrice(20.1);
		product1.setName("IT product 1");
		product1.setSku(UUID.randomUUID().toString());

		ResponseEntity<Product> responseEntity = testRestTemplate.postForEntity(baseUrl + "/products", product1, Product.class);
		assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

		product1 = responseEntity.getBody();

		product2 = new Product();
		product2.setDescription("Int test product 2 description");
		product2.setName("IT product 2");
		product2.setPrice(10.5);
		product2.setSku(UUID.randomUUID().toString());

		responseEntity = testRestTemplate.postForEntity(baseUrl + "/products", product2, Product.class);
		assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

		product2 = responseEntity.getBody();
	}

	//
	@After
	public void tearDown() throws Exception {
		session.close();
		testRestTemplate.delete(baseUrl + "/products/" + product1.getSku());
		testRestTemplate.delete(baseUrl + "/products/" + product2.getSku());
	}

	@Test
	public void testOrderCreation() throws Exception {
		Order order = new Order();

		Address address = new Address();
		address.setLine1("4823, james long str.");
		address.setLine2("NY, USA");
		address.setZipCode("10010");
		order.setAddress(address);

		OrderItem orderItem1 = new OrderItem();
		orderItem1.setPrice(29.99);
		orderItem1.setQty(6);
		Product product1 = new Product();
		product1.setSku(this.product1.getSku());
		orderItem1.setProduct(product1);

		order.setOrderItems(Arrays.asList(orderItem1));

		// When
		ResponseEntity<String> orderCreatedResponse = testRestTemplate.postForEntity(baseUrl + "/orders", order, null);
		assertEquals(HttpStatus.CREATED, orderCreatedResponse.getStatusCode());
		URI orderLocation = orderCreatedResponse.getHeaders().getLocation();
		assertNotNull(orderLocation);

		ResponseEntity<Order> responseEntity = testRestTemplate.getForEntity(orderLocation, Order.class);
		// Then
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

		Order savedOrder = responseEntity.getBody();

		List<OrderItem> savedOrderItems = savedOrder.getOrderItems();
		assertNotNull(savedOrderItems);
		OrderItem savedOrderItem = savedOrderItems.get(0);
		assertNotNull(savedOrderItem.getId());
		assertEquals(6, savedOrderItem.getQty());
		assertEquals(29.99, savedOrderItem.getPrice(), 0.0001);
		assertEquals(this.product1, savedOrderItem.getProduct());

		assertEquals(address, savedOrder.getAddress());

		assertNotNull(savedOrder.getCreatedOn());
	}

	@Test
	public void testOrderCreationWithBadPrices() {
		Order order = new Order();

		Address address = new Address();
		address.setLine1("4823, james long str.");
		address.setLine2("NY, USA");
		address.setZipCode("10010");
		order.setAddress(address);

		OrderItem orderItem1 = new OrderItem();
		orderItem1.setPrice(product1.getPrice() - 0.11);
		orderItem1.setQty(6);
		Product product1 = new Product();
		product1.setSku(this.product1.getSku());
		orderItem1.setProduct(product1);

		order.setOrderItems(Arrays.asList(orderItem1));

		// When
		ResponseEntity<Order> responseEntity = testRestTemplate.postForEntity(baseUrl + "/orders", order, null);

		// Then
		assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
	}
}
