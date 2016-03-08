package demo.service;

import demo.model.Order;

public interface OrderService {

	Iterable<Order> getAll();

	Order getById(Long id);

	Order save(Order product);

	void update(Long id, Order menu);

}
