package demo.web;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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

	@RequestMapping(method = RequestMethod.GET, params="orderDate")
	public Iterable<Order> ordersByDate(@RequestParam("orderDate") @DateTimeFormat(pattern = "MM/dd/yyyy") Date orderDate) {
		return orderService.getByOrderDate(orderDate);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Order order(@PathVariable Long id) {
		return orderService.getById(id);
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public void createOrder(@RequestBody Order order, HttpServletRequest request, HttpServletResponse response) {
		orderService.save(order);
		response.addHeader(HttpHeaders.LOCATION, request.getRequestURL().append("/").append(order.getId()).toString());
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public void updateOrder(@PathVariable Long id, @RequestBody Order order) {
		orderService.update(id, order);
	}

	@RequestMapping(value = "/{sku}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteOrder(@PathVariable Long id) {
		orderService.delete(id);
	}
}
