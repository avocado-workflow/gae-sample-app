package demo.service;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import demo.model.Product;
import demo.repository.ProductRepository;

@RunWith(MockitoJUnitRunner.class)
public class DefaultProductServiceTest {

	@InjectMocks
	private DefaultProductService unit;

	@Mock
	private ProductRepository productRepository;

	@Test
	public void testGetBySkuWhenProductExist() throws Exception {
		// Given
		Product product = new Product();
		product.setName("oranges");
		when(productRepository.findOne(anyString())).thenReturn(product);

		// When
		Product returnedValue = unit.getBySku("12123");

		// Then
		verify(productRepository).findOne("12123");
		assertEquals(product, returnedValue);
	}

	@Test
	public void testGetBySkuWhenProductDoesntExist() throws Exception {
		// Given
		Product product = new Product();
		product.setName("oranges");
		when(productRepository.findOne(anyString())).thenReturn(null);

		// When
		Product returnedValue = unit.getBySku("12123");

		// Then
		verify(productRepository).findOne("12123");
		assertNull(returnedValue);
	}

	@Test
	public void testUpdate() throws Exception {
		// Given
		Product product = new Product();
		product.setName("oranges");
		when(productRepository.findOne(anyString())).thenReturn(product);

		// When
		unit.update("12123", product);

		// Then
		Product productToSave = new Product();
		productToSave.setName("oranges");
		productToSave.setCode("12123");
		verify(productRepository).update(eq(productToSave));
	}
}
