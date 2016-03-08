package demo.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;

import demo.EmbeddedDataStore;
import demo.IntegrationTests;
import demo.model.Address;
import demo.model.Order;
import demo.model.OrderItem;
import demo.model.Product;

@Category(IntegrationTests.class)
public class OrderControllerTest extends BaseIntegrationTest {

	@Rule
	public EmbeddedDataStore store = new EmbeddedDataStore();

	private int port = 8080;

	private String baseUrl = "http://localhost:" + port;

	Closeable session;

	private Product product1;
	private Product product2;

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

		product2 = new Product();
		product2.setDescription("Int test product 2 description");
		product2.setName("IT product 2");
		product2.setPrice(10.5);
		product2.setSku(UUID.randomUUID().toString());
		ObjectifyService.ofy().save().entities(product1, product2).now();
		System.out.println("SKU1" + product1.getSku());
	}

	//
	@After
	public void tearDown() {
		session.close();
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
		orderItem1.setPrice(19.99);
		orderItem1.setQty(6);
		Product product1 = new Product();
		product1.setSku(this.product1.getSku());
		orderItem1.setProduct(product1);
		System.out.println("SKU11" + this.product1.getSku());

		order.setOrderItems(Arrays.asList(orderItem1));

		// When
		ResponseEntity<Order> responseEntity = testRestTemplate.postForEntity(baseUrl + "/orders", order, Order.class);

		// Then
		assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

		
		Order savedOrder = responseEntity.getBody();
		
		List<OrderItem> savedOrderItems = savedOrder.getOrderItems();
		assertNotNull(savedOrderItems);
		OrderItem savedOrderItem = savedOrderItems.get(0);
		assertNotNull(savedOrderItem.getId());
		assertEquals(6, savedOrderItem.getQty());
		assertEquals(19.99, savedOrderItem.getPrice(), 0.0001);
		assertEquals(product1, savedOrderItem.getProduct());

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
		orderItem1.setPrice(product1.getPrice()-0.11);
		orderItem1.setQty(6);
		Product product1 = new Product();
		product1.setSku(this.product1.getSku());
		orderItem1.setProduct(product1);

		order.setOrderItems(Arrays.asList(orderItem1));

		// When
		ResponseEntity<Order> responseEntity = testRestTemplate.postForEntity(baseUrl + "/orders", order, Order.class);

		// Then
		assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
	}
}
