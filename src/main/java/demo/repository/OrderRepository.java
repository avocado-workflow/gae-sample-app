package demo.repository;

import demo.model.Order;

public interface OrderRepository {

	Iterable<Order> findAll();

	Order findOne(Long id);

	Order save(Order order);

	void delete(Long id);
}
