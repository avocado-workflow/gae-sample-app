package demo.service;

import demo.dto.OrderResource;

public interface OrderService {

	Iterable<OrderResource> getAll();

	OrderResource getById(Long id);

	OrderResource save(OrderResource product);

	void update(Long id, OrderResource menu);

}
