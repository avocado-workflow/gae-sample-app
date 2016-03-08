package demo.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;

import demo.model.Address;
import demo.model.Order;
import demo.model.OrderItem;
import demo.model.Product;

public class GoogleDatastoreOrderRepositoryTest {

	private Closeable session;

	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

	private GoogleDatastoreOrderRepository unit = new GoogleDatastoreOrderRepository();

	private Product product1 = new Product();
	private Product product2 = new Product();
	private Product product3 = new Product();

	@Before
	public void setUp() {
		helper.setUp();
		session = ObjectifyService.begin();

		ObjectifyService.register(Product.class);
		ObjectifyService.register(Order.class);
		ObjectifyService.register(OrderItem.class);

		product1.setSku("23d2680c-141f-4220-9444-ab076df3bf58");
		product1.setName("orange");
		product1.setPrice(4.5);
		product1.setDescription("fresh oranges");

		product2.setSku("8981304a-12f4-3122-9891-fed8a8d776e0");
		product2.setName("lemon");
		product2.setPrice(5.0);
		product2.setDescription("fresh lemons");

		product3.setSku("713288ff-7237-1389-6138-76713ae8s891");
		product3.setName("pinapple");
		product3.setPrice(6.4);
		product3.setDescription("fresh pinapples");

		ObjectifyService.ofy().save().entities(product1, product2, product3);
	}

	@After
	public void tearDown() {
		helper.tearDown();
		session.close();
	}

	@Test
	public void testSave() throws Exception {
		Date creationTimeLowerBound = new Date();
		Thread.sleep(10); // Just to make sure the creation time is after creationTimeLowerBound.
		Order order = new Order();

		Address address = new Address();
		address.setLine1("4823, james long str.");
		address.setLine2("NY, USA");
		address.setZipCode("10010");
		order.setAddress(address);

		OrderItem orderItem1 = new OrderItem();
		orderItem1.setPrice(6.8);
		orderItem1.setQty(4);
		Product product = new Product();
		product.setSku(product1.getSku());
		orderItem1.setProduct(product);

		order.setOrderItems(Arrays.asList(orderItem1));

		Order savedOrder = unit.save(order);

		Date creationTimeUpperBound = new Date();

		assertNotNull(savedOrder.getId());

		Order orderFromDatastore = unit.findOne(savedOrder.getId());

		List<OrderItem> orderItemsFromDatastore = orderFromDatastore.getOrderItems();
		assertNotNull(orderItemsFromDatastore);
		OrderItem orderItemFromDatastore = orderItemsFromDatastore.get(0);
		assertNotNull(orderItemFromDatastore.getId());
		assertNotNull(orderItemFromDatastore.getOrder());
		assertEquals(4, orderItemFromDatastore.getQty());
		assertEquals(6.8, orderItemFromDatastore.getPrice(), 0.0001);
		assertEquals(product1, orderItemFromDatastore.getProduct());

		assertNotNull(orderFromDatastore);

		assertEquals(address, orderFromDatastore.getAddress());

		assertNotNull(orderFromDatastore.getCreatedOn());
		assertTrue(orderFromDatastore.getCreatedOn().after(creationTimeLowerBound));
		assertTrue(orderFromDatastore.getCreatedOn().before(creationTimeUpperBound));

	}
}
