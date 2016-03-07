package demo.converter;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;

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
public class OrderResourceConverterTest {

	@InjectMocks
	private OrderResourceConverter unit = new OrderResourceConverter();

	@Mock
	private ProductResourceConverter productResourceConverter;

	private Closeable session;

	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

	private Product product = new Product();

	private String productSKu = "23d2680c-141f-4220-9444-ab076df3bf58";

	@Before
	public void setUp() {
		helper.setUp();
		session = ObjectifyService.begin();

		when(productResourceConverter.convert(any(ProductResource.class))).thenReturn(product);

		product.setName("watermelon");
		product.setSku(productSKu);

		ObjectifyService.register(Product.class);
		ObjectifyService.register(Order.class);
		ObjectifyService.register(OrderItem.class);
	}

	@After
	public void tearDown() {
		helper.tearDown();
		session.close();
	}

	@Test
	public void testConvert() throws Exception {
		// Given
		OrderResource orderResource = new OrderResource();

		orderResource.setId(432L);

		AddressResource addressRes = new AddressResource();
		addressRes.setLine1("4823, james long str.");
		addressRes.setLine2("NY, USA");
		addressRes.setZipCode("10010");
		orderResource.setAddress(addressRes);

		OrderItemResource orderItemRes1 = new OrderItemResource();
		orderItemRes1.setPrice(6.8);
		orderItemRes1.setQty(4);

		ProductResource productRes1 = new ProductResource();
		productRes1.setSku("23d2680c-141f-4220-9444-ab076df3bf58");
		orderItemRes1.setProduct(productRes1);

		orderResource.setOrderItems(Arrays.asList(orderItemRes1));
		Date updateTime = new Date();
		orderResource.setUpdatedOn(updateTime);

		// When
		Order actualOrder = unit.convert(orderResource);

		// Then
		verify(productResourceConverter).convert(productRes1);

		Order expectedOrder = new Order();
		expectedOrder.setId(432L);
		Address address = new Address();
		address.setLine1("4823, james long str.");
		address.setLine2("NY, USA");
		address.setZipCode("10010");
		expectedOrder.setAddress(address);

		OrderItem orderItem1 = new OrderItem();
		orderItem1.setPrice(6.8);
		orderItem1.setQty(4);
		Product product1 = new Product();
		product1.setSku("23d2680c-141f-4220-9444-ab076df3bf58");
		orderItem1.setProduct(product1);
		expectedOrder.setUpdatedOn(updateTime);

		expectedOrder.setOrderItems(Arrays.asList(orderItem1));

		assertEquals(expectedOrder, actualOrder);
	}
}
