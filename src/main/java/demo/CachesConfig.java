package demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import demo.model.Product;
import demo.repository.BasicCacheImpl;
import demo.repository.Cache;

@Configuration
public class CachesConfig {

	// Just a workaround for DI with Class object.
	@Bean(name="productsCache")
	public Cache<Product> productsCache() {
		return new BasicCacheImpl<Product>(Product.class);
	}
}
