package demo.web;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import demo.dto.ProductResource;
import demo.service.ProductService;

@RestController
@RequestMapping("/products")
public class ProductController {

	@Resource
    private ProductService productService;

    @RequestMapping(method = RequestMethod.GET)
    public Iterable<ProductResource> products() {
        return productService.getAll();
    }

    @RequestMapping(value = "/{sku}", method = RequestMethod.GET)
    public ProductResource product(@PathVariable String sku) {
        return productService.getBySku(sku);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResource createProduct( @RequestBody ProductResource product) {
        return productService.save(product);
    }

    @RequestMapping(value = "/{sku}", method = RequestMethod.PUT)
    public void updateProduct(@PathVariable String sku, @RequestBody ProductResource menu) {
        productService.update(sku, menu);
    }
}
