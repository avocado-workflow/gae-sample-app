package demo.converter;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;

import demo.dto.AddressResource;
import demo.dto.OrderItemResource;
import demo.dto.OrderResource;
import demo.dto.ProductResource;
import demo.model.Address;
import demo.model.Order;
import demo.model.OrderItem;
import demo.model.Product;

@RunWith(MockitoJUnitRunner.class)
public class OrderConverterTest {

	@InjectMocks
	private OrderConverter unit = new OrderConverter();

	@Mock
	private ProductConverter productConverter;
	
	private Closeable session;

	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
	private ProductResource productResource = new ProductResource();

	private String productSKu = "23d2680c-141f-4220-9444-ab076df3bf58";
	@Before
	public void setUp() {
		helper.setUp();
		session = ObjectifyService.begin();

		when(productConverter.convert(any(Product.class))).thenReturn(productResource );
		
		productResource.setName("dummy product");

		Product product = new Product();
		product.setName("watermelon");
		product.setSku(productSKu);
		
		ObjectifyService.register(Product.class);
		ObjectifyService.register(Order.class);
		ObjectifyService.register(OrderItem.class);

		ObjectifyService.ofy().save().entity(product).now();
	}

	@After
	public void tearDown() {
		helper.tearDown();
		session.close();
	}

	@Test
	public void testConvert() throws Exception {
		// Given
		Order order = new Order();

		Address address = new Address();
		address.setLine1("4823, james long str.");
		address.setLine2("NY, USA");
		address.setZipCode("10010");
		order.setAddress(address);

		OrderItem orderItem1 = new OrderItem();
		orderItem1.setPrice(6.8);
		orderItem1.setQty(4);
		Product product1 = new Product();
		product1.setSku("23d2680c-141f-4220-9444-ab076df3bf58");
		orderItem1.setProduct(product1);

		order.setOrderItems(Arrays.asList(orderItem1));

		// When
		OrderResource actualOrderResource = unit.convert(order);

		// Then
		Product expectedProduct = ObjectifyService.ofy().load().type(Product.class).id(productSKu).now();
		verify(productConverter).convert(expectedProduct);
		
		OrderResource expectedOrderResource = new OrderResource();

		AddressResource addressRes = new AddressResource();
		addressRes.setLine1("4823, james long str.");
		addressRes.setLine2("NY, USA");
		addressRes.setZipCode("10010");
		expectedOrderResource.setAddress(addressRes);

		OrderItemResource orderItemRes1 = new OrderItemResource();
		orderItemRes1.setPrice(6.8);
		orderItemRes1.setQty(4);
		ProductResource productRes1 = new ProductResource();
		productRes1.setSku("23d2680c-141f-4220-9444-ab076df3bf58");
		orderItemRes1.setProduct(productResource);

		expectedOrderResource.setOrderItems(Arrays.asList(orderItemRes1));

		assertEquals(expectedOrderResource, actualOrderResource);
	}
}
