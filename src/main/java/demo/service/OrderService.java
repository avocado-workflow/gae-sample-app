package demo.service;

import demo.model.Order;

public interface OrderService {

	Iterable<Order> getAll();

	Order getById(Long id);

	Long save(Order order);

	void update(Long id, Order order);

	void delete(Long id);
}
