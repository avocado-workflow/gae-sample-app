package demo.loadtest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.Closeable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.googlecode.objectify.ObjectifyService;

import demo.EmbeddedDataStore;
import demo.LoadTests;
import demo.model.Address;
import demo.model.Order;
import demo.model.OrderItem;
import demo.model.Product;
import demo.web.BaseIntegrationTest;

@Category(LoadTests.class)
public class OrderCreationLoadTest extends BaseIntegrationTest {

	private static final Logger log = Logger.getLogger(OrderCreationLoadTest.class.getName());
//	private String baseUrl = "http://localhost:8080";
//	private String baseUrl = "https://psychic-city-78613.appspot.com";
	private String baseUrl = "https://avocado-perf-test.appspot.com";

	private Map<String, Long> threadStats = Collections.synchronizedMap(new TreeMap<String, Long>());

	Closeable session;
	@Before
	public void setUp() {
		session = ObjectifyService.begin();
		
		ObjectifyService.register(Product.class);
		ObjectifyService.register(Order.class);
		ObjectifyService.register(OrderItem.class);

		threadStats = Collections.synchronizedMap(new TreeMap<String, Long>());
	}
	
	@Rule
	public EmbeddedDataStore embeddedDataStore = new EmbeddedDataStore();
	
	@After
	public void tearDown() throws Exception {
		session.close();
	}
	
	@Ignore
	@Test
	public void testGetAllProducts() throws Exception {

		ExecutorService threadPool = Executors.newFixedThreadPool(20);
		for (int i = 0; i < 100; i++) {
			threadPool.submit(getProductsRunnable);
		}

		threadPool.shutdown();
		threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);

		System.out.println(threadStats);
	}
	
//	@Ignore
	@Test
	public void testOrderCreation() throws Exception {
		
		int NUM_OF_ORDERS_TO_CREATE = 1;
		ResponseEntity<Product[]> responseEntity = testRestTemplate.getForEntity(baseUrl + "/products", Product[].class);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		Product[] products = responseEntity.getBody();
		
		int NUM_OF_PRODUCTS_TO_USE = 10000 > products.length ? products.length : 10000;
		System.out.println("PRODUCTS USED: " + NUM_OF_PRODUCTS_TO_USE);
		final Queue<Order> ordersQueue = new ConcurrentLinkedQueue<Order>();

		for(int i = 0; i < NUM_OF_ORDERS_TO_CREATE; i++) {
			Order order = new Order();
	
			Address address = new Address();
			address.setLine1("address #" + i);
			address.setLine2("NY, USA");
			address.setZipCode("Mar-17");
			order.setAddress(address);

			Product productToOrder = products[i%NUM_OF_PRODUCTS_TO_USE];
	
			OrderItem orderItem1 = new OrderItem();
			orderItem1.setPrice(productToOrder.getPrice());
			orderItem1.setQty(4);
			Product product1 = new Product();
			product1.setCode(productToOrder.getCode());
			orderItem1.setProduct(product1);
			order.setOrderItems(Arrays.asList(orderItem1));
			ordersQueue.offer(order);
		}

		ExecutorService threadPool = Executors.newFixedThreadPool(20);
		for (int i = 0; i < NUM_OF_ORDERS_TO_CREATE; i++) {
			threadPool.submit(new Runnable() {
				public void run() {
					try {
						long start = System.currentTimeMillis();
						URI orderLocation = testRestTemplate.postForLocation(baseUrl + "/orders", ordersQueue.poll());
						assertNotNull(orderLocation);
						long end = System.currentTimeMillis();
						threadStats.put(Thread.currentThread().getName(), end - start);
					} catch (Exception e) {
						threadStats.put(Thread.currentThread().getName(), -1L);
						e.printStackTrace();
					}
				};
			});
		}

		threadPool.shutdown();
		threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);

		System.out.println(threadStats);
	}		
	
//	@Ignore
	@Test
	public void createOneOrderWithLotsOfItems() throws Exception {
		
		ResponseEntity<Product[]> responseEntity = testRestTemplate.getForEntity(baseUrl + "/products", Product[].class);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		Product[] products = responseEntity.getBody();
		
		int NUM_OF_PRODUCTS_TO_USE = 10000 > products.length ? products.length : 10000;
		System.out.println("PRODUCTS USED: " + NUM_OF_PRODUCTS_TO_USE);
//		for(int o=0;o<10;o++) {
			
		Order order = new Order();
		
		Address address = new Address();
		address.setLine1("address #BIG" );
		address.setLine2("2k items ORDER");
		address.setZipCode("2000");
		order.setAddress(address);
		List<OrderItem> orderItems = new ArrayList<>();
		
		for(int i = 0; i < NUM_OF_PRODUCTS_TO_USE; i++) {
			
			Product productToOrder = products[i];
			
			OrderItem orderItem1 = new OrderItem();
			orderItem1.setPrice(productToOrder.getPrice());
			orderItem1.setQty(i);
			Product product1 = new Product();
			product1.setCode(productToOrder.getCode());
			orderItem1.setProduct(product1);
			orderItems.add(orderItem1);
		}
		
		order.setOrderItems(orderItems);

		URI orderLocation = testRestTemplate.postForLocation(baseUrl + "/orders", order);
		assertNotNull(orderLocation);
		log.log(Level.INFO, orderLocation.toString());
		System.out.println(orderLocation);
//		}
	}		

	private Runnable getProductsRunnable = new Runnable() {

		@Override
		public void run() {
			try {
				long start = System.currentTimeMillis();
				ResponseEntity<Object> responseEntity = testRestTemplate.getForEntity(baseUrl + "/products", Object.class);
				assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
				long end = System.currentTimeMillis();
				threadStats.put(Thread.currentThread().getName(), end - start);
			} catch (Exception e) {
				threadStats.put(Thread.currentThread().getName(), -1L);
				e.printStackTrace();
			}
		}
	};
}
