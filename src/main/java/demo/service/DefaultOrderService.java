package demo.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import demo.converter.OrderConverter;
import demo.converter.OrderResourceConverter;
import demo.dto.OrderResource;
import demo.model.Order;
import demo.repository.OrderRepository;

@Service
public class DefaultOrderService implements OrderService {

	@Resource
	private OrderRepository orderRepository;

	@Resource
	private OrderConverter orderConverter;

	@Resource
	private OrderResourceConverter orderResourceConverter;
	
	@Override
	public Iterable<OrderResource> getAll() {
		Iterable<Order> orders = orderRepository.findAll();
		return orderConverter.convertAll(orders);
	}

	@Override
	public OrderResource getById(Long id) {
		Order order = orderRepository.findOne(id);
		Assert.notNull(order);
		return orderConverter.convert(order);
	}

	@Override
	public OrderResource save(OrderResource orderResource) {
		Order order = orderResourceConverter.convert(orderResource);
		Order savedOrder = orderRepository.save(order);
		return orderConverter.convert(savedOrder);
	}

	@Override
	public void update(Long id, OrderResource orderResource) {
		orderResource.setId(id);
		Order order = orderResourceConverter.convert(orderResource);
		orderRepository.save(order);
	}

}
