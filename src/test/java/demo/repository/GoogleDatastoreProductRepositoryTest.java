package demo.repository;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;

import demo.model.Product;

@RunWith(MockitoJUnitRunner.class)
public class GoogleDatastoreProductRepositoryTest {

	private Closeable session;

	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
			new LocalDatastoreServiceTestConfig(),
			new LocalMemcacheServiceTestConfig());

	@InjectMocks
	private GoogleDatastoreProductRepository unit = new GoogleDatastoreProductRepository();

	@Mock
	private Cache<Product> cache;
	
	@Before
	public void setUp() {
		helper.setUp();
		session = ObjectifyService.begin();

		ObjectifyService.register(Product.class);
	}

	@After
	public void tearDown() {
		helper.tearDown();
		session.close();
	}
	
	@Test
	public void testUpdate() throws Exception {
		// Given
		String sku = "713288ff-7237-1389-6138-76713ae8s891";

		Product product = new Product();
		product.setCode(sku);
		product.setName("pinapple");
		product.setPrice(44.5);
		product.setDescription("fresh pinapples");
		
		ObjectifyService.ofy().save().entity(product).now();
		
		
		Product productToUpdate = new Product();
		productToUpdate.setCode(sku);
		productToUpdate.setName("oranges");
		productToUpdate.setPrice(88.5);
		
		// When
		unit.update(productToUpdate);
		
		// Then
		verify(cache).put(sku, productToUpdate);

//		Product productFromDatastore = ObjectifyService.ofy().load().type(Product.class).id(sku).now();
//		
//		assertEquals(productToUpdate, productFromDatastore);		
	}
}

