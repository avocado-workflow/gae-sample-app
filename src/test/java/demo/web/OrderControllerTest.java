package demo.web;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;

import demo.EmbeddedDataStore;
import demo.IntegrationTests;
import demo.dto.AddressResource;
import demo.dto.OrderItemResource;
import demo.dto.OrderResource;
import demo.dto.ProductResource;
import demo.model.Order;
import demo.model.OrderItem;
import demo.model.Product;

@Category(IntegrationTests.class)
public class OrderControllerTest {

	@Rule
	public EmbeddedDataStore store = new EmbeddedDataStore();

	private int port = 8080;

	private String baseUrl = "http://localhost:" + port;

	Closeable session;

	@Before
	public void setUp() {
		session = ObjectifyService.begin();

		ObjectifyService.register(Product.class);
		ObjectifyService.register(Order.class);
		ObjectifyService.register(OrderItem.class);
	}

	//
	@After
	public void tearDown() {
		session.close();
	}

	// @Ignore
	@Test
	public void testOrderCreation() throws Exception {
		OrderResource order = new OrderResource();

		AddressResource address = new AddressResource();
		address.setLine1("4823, james long str.");
		address.setLine2("NY, USA");
		address.setZipCode("10010");
		order.setAddress(address);

		OrderItemResource orderItem1 = new OrderItemResource();
		orderItem1.setPrice(6.8);
		orderItem1.setQty(4);
		ProductResource product1 = new ProductResource();
		product1.setSku("23d2680c-141f-4220-9444-ab076df3bf58");
		orderItem1.setProduct(product1);

		OrderItemResource orderItem2 = new OrderItemResource();
		orderItem2.setPrice(6.8);
		orderItem2.setQty(4);
		ProductResource product2 = new ProductResource();
		product2.setSku("0c7cbf98-7c03-44b4-bb8c-867d8aebac83");
		orderItem2.setProduct(product2);

		order.setOrderItems(Arrays.asList(orderItem1, orderItem2));

		// When
		ResponseEntity<OrderResource> responseEntity = new TestRestTemplate().postForEntity(baseUrl + "/orders", order,
				OrderResource.class);

		// Then
		assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

		// TODO - add the rest verification logic
	}
}
