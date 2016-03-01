package demo.web;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.UUID;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import demo.Application;
import demo.model.Product;
import demo.repository.ProductRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebIntegrationTest("server.port=8889")
public class ProductControllerTest {

	@Resource
	private ProductRepository productRepository;
	
	@Value("${local.server.port}")
	private int port;
	
	@Test
	public void testGetBySku() throws Exception {
		// Given
		String skuToFind = UUID.randomUUID().toString();

		Product product = new Product();
		product.setName("Lemon");
		product.setPrice(new BigDecimal("5.87"));
		product.setDescription("Fresh tropic lemons");
		product.setSku(skuToFind);
		
		productRepository.save(product);

		// When
		Product returnedProduct = new TestRestTemplate().getForObject("http://localhost:" + port
				+ "/products/" + skuToFind, Product.class);

		// Then
		assertEquals(product, returnedProduct);
	}
}
