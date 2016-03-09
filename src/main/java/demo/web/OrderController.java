package demo.web;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import demo.model.Order;
import demo.service.OrderService;

@RestController
@RequestMapping("/orders")
public class OrderController {
	
	@Resource
    private OrderService orderService;

    @RequestMapping(method = RequestMethod.GET)
    public Iterable<Order> orders() {
        return orderService.getAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Order order(@PathVariable Long id) {
        return orderService.getById(id);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public Order createOrder(@RequestBody Order product) {
        return orderService.save(product);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public void updateOrder(@PathVariable Long id, @RequestBody Order menu) {
        orderService.update(id, menu);
    }
    
    @RequestMapping(value = "/{sku}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteOrder(@PathVariable Long id) {
    	orderService.delete(id);
	}
}
