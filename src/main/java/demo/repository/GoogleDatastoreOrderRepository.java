package demo.repository;

import java.util.Date;

import org.springframework.stereotype.Repository;

import com.googlecode.objectify.ObjectifyService;

import demo.model.Order;

@Repository
public class GoogleDatastoreOrderRepository implements OrderRepository {

	@Override
	public Iterable<Order> findAll() {
		return ObjectifyService.ofy().load().type(Order.class).list();
	}

	@Override
	public Order findOne(Long id) {
		return ObjectifyService.ofy().load().type(Order.class).id(id).now();
	}

	@Override
	public Order save(Order order) {  // TODO - wrap with transaction!!!
		order.setUpdatedOn(new Date());
		ObjectifyService.ofy().save().entity(order).now();
		ObjectifyService.ofy().save().entities(order.getOrderItems()).now();
		return order;
	}

}
