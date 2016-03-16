package demo.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;

import demo.model.Address;
import demo.model.Order;
import demo.model.OrderItem;
import demo.model.Product;
import demo.util.Profiler;

@RunWith(MockitoJUnitRunner.class)
public class GoogleDatastoreOrderRepositoryTest {

	private Closeable session;

	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

	@Mock
	private Profiler profiler;
	
	@InjectMocks
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

		product1.setCode("23d2680c-141f-4220-9444-ab076df3bf58");
		product1.setName("orange");
		product1.setPrice(4.5);
		product1.setDescription("fresh oranges");

		product2.setCode("8981304a-12f4-3122-9891-fed8a8d776e0");
		product2.setName("lemon");
		product2.setPrice(5.0);
		product2.setDescription("fresh lemons");

		product3.setCode("713288ff-7237-1389-6138-76713ae8s891");
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
		product.setCode(product1.getCode());
		orderItem1.setProduct(product);

		order.setOrderItems(Arrays.asList(orderItem1));

		Long orderId = unit.save(order);

		Date creationTimeUpperBound = new Date();

		assertNotNull(orderId);

		Order savedOrder = ObjectifyService.ofy().load().key(Key.<Order>create(Order.class, orderId)).now();
		
		List<OrderItem> orderItemsFromDatastore = ObjectifyService.ofy().load().type(OrderItem.class).ancestor(savedOrder).list();
		assertNotNull(orderItemsFromDatastore);

		OrderItem orderItemFromDatastore = orderItemsFromDatastore.get(0);
		assertNotNull(orderItemFromDatastore.getId());
		assertNotNull(orderItemFromDatastore.getOrder());
		assertEquals(4, orderItemFromDatastore.getQty());
		assertEquals(6.8, orderItemFromDatastore.getPrice(), 0.0001);
		assertEquals(product.getCode(), orderItemFromDatastore.getProductSku());

		assertNotNull(savedOrder);

		assertEquals(address, savedOrder.getAddress());

		assertNotNull(savedOrder.getCreatedOn());
		assertTrue(savedOrder.getCreatedOn().after(creationTimeLowerBound));
		assertTrue(savedOrder.getCreatedOn().before(creationTimeUpperBound));
	}

	@Test
	public void testGetById() {
		
		Order order = new Order();
		order.setUpdatedOn(new Date());

		Address address = new Address();
		address.setLine1("4823, james long str.");
		address.setLine2("NY, USA");
		address.setZipCode("10010");
		order.setAddress(address);

		OrderItem orderItem1 = new OrderItem();
		orderItem1.setPrice(6.8);
		orderItem1.setQty(4);
		Product product = new Product();
		product.setCode(product1.getCode());
		orderItem1.setProduct(product);

		order.setOrderItems(Arrays.asList(orderItem1));
		storeOrderInDB(order);
		
		assertNotNull(order.getId());
		Order actualOrder = unit.findOne(order.getId());

		List<OrderItem> orderItemsFromDatastore = actualOrder.getOrderItems();
		assertNotNull(orderItemsFromDatastore);
		OrderItem orderItemFromDatastore = orderItemsFromDatastore.get(0);
		assertNotNull(orderItemFromDatastore.getId());
		assertNotNull(orderItemFromDatastore.getOrder());
		assertEquals(4, orderItemFromDatastore.getQty());
		assertEquals(6.8, orderItemFromDatastore.getPrice(), 0.0001);
		assertEquals(product1, orderItemFromDatastore.getProduct());

		assertNotNull(actualOrder);

		assertEquals(address, actualOrder.getAddress());

		assertNotNull(actualOrder.getCreatedOn());
	}

	private Order storeOrderInDB(Order order) {
		ObjectifyService.ofy().save().entity(order).now();

		List<OrderItem> orderItems = order.getOrderItems();

		for (OrderItem orderItem : orderItems) {
			orderItem.setOrder(order);
		}

		ObjectifyService.ofy().save().entities(orderItems).now();
		return order;
	}
}
