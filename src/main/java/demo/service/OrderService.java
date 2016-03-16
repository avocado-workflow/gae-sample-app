package demo.service;

import java.util.Date;

import demo.model.Order;

public interface OrderService {

	Iterable<Order> getAll();

	Order getById(Long id);

	Long save(Order order);

	void update(Long id, Order order);

	void delete(Long id);

	Iterable<Order> getByOrderDate(Date orderDate);
}
