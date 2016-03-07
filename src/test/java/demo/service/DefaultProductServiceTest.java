package demo.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import demo.converter.ProductConverter;
import demo.dto.ProductResource;
import demo.model.Product;
import demo.repository.ProductRepository;

@RunWith(MockitoJUnitRunner.class)
public class DefaultProductServiceTest {

	@InjectMocks
	private DefaultProductService unit;
	
	@Mock
	private ProductRepository productRepository;

	@Mock
	private ProductConverter productConverter;
	
	@Mock
	private ProductConverter productResourceConverter;
	
	@Test
	public void testGetBySkuWhenProductExist() throws Exception {
		// Given
		Product product = new Product();
		product.setName("oranges");
		when(productRepository.findOne(anyString())).thenReturn(product);
		
		ProductResource expectedProduct = new ProductResource();
		expectedProduct.setName("oranges");
		when(productConverter.convert(any(Product.class))).thenReturn(expectedProduct);
		// When
		ProductResource returnedValue = unit.getBySku("12123");
		
		// Then
		verify(productRepository).findOne("12123");
		verify(productConverter).convert(product);
		assertEquals(expectedProduct, returnedValue);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testGetBySkuWhenProductDoesntExist() throws Exception {
		// Given
		Product product = new Product();
		product.setName("oranges");
		when(productRepository.findOne(anyString())).thenReturn(null);
		
		// When
		ProductResource returnedValue = unit.getBySku("12123");
		
		// Then
		verify(productRepository).findOne("12123");
		assertNull(returnedValue);
	}
}
