package demo.loadtest;

import static org.junit.Assert.assertEquals;

import java.io.Closeable;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.googlecode.objectify.ObjectifyService;

import demo.EmbeddedDataStore;
import demo.model.Address;
import demo.model.Order;
import demo.model.OrderItem;
import demo.model.Product;
import demo.web.BaseIntegrationTest;

public class OrderCreationLoadTest extends BaseIntegrationTest {

	private static final Logger log = Logger.getLogger(OrderCreationLoadTest.class.getName());
	private String baseUrl = "http://localhost:8080";

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
	
	@Ignore
	@Test
	public void testOrderCreation() throws Exception {
		
		int NUM_OF_ORDERS_TO_CREATE = 1;
		ResponseEntity<Product[]> responseEntity = testRestTemplate.getForEntity(baseUrl + "/products", Product[].class);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		Product[] products = responseEntity.getBody();
		
		int NUM_OF_PRODUCTS_TO_USE = 5 > products.length ? products.length : 5;
		
		
		final Queue<Order> ordersQueue = new ConcurrentLinkedQueue<Order>();

		for(int i = 0; i < NUM_OF_ORDERS_TO_CREATE; i++) {
			Order order = new Order();
	
			Address address = new Address();
			address.setLine1("address #" + i);
			address.setLine2("NY, USA");
			address.setZipCode("10010");
			order.setAddress(address);
	
			OrderItem orderItem1 = new OrderItem();
			orderItem1.setPrice(19.99);
			orderItem1.setQty(6);
			Product product1 = new Product();
			product1.setSku(products[i%NUM_OF_PRODUCTS_TO_USE].getSku());
			orderItem1.setProduct(product1);
			orderItem1.getProduct();
			order.setOrderItems(Arrays.asList(orderItem1));
			ordersQueue.offer(order);
		}

		ExecutorService threadPool = Executors.newFixedThreadPool(20);
		for (int i = 0; i < NUM_OF_ORDERS_TO_CREATE; i++) {
			threadPool.submit(new Runnable() {
				public void run() {
					try {
						long start = System.currentTimeMillis();
						URI responseEntity = testRestTemplate.postForLocation(baseUrl + "/orders", ordersQueue.poll());
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
