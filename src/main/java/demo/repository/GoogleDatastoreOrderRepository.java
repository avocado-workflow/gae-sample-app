package demo.repository;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;

import demo.model.Order;
import demo.model.OrderItem;

@Repository
public class GoogleDatastoreOrderRepository implements OrderRepository {

	@Override
	public Iterable<Order> findAll() {
		return ObjectifyService.ofy().load().type(Order.class).list();
	}

	@Override
	public Order findOne(Long id) {
		Order order = ObjectifyService.ofy().load().type(Order.class).id(id).now();

		List<OrderItem> orderItems = ObjectifyService.ofy().load().type(OrderItem.class).ancestor(order).list();
		order.setOrderItems(orderItems);

		return order;
	}

	@Override
	public Order save(Order order) { // TODO - wrap with transaction?
		order.setUpdatedOn(new Date());

		ObjectifyService.ofy().save().entity(order).now();

		List<OrderItem> orderItems = order.getOrderItems();

		for (OrderItem orderItem : orderItems) {
			orderItem.setOrder(order);
		}

		ObjectifyService.ofy().save().entities(orderItems).now();
		return order;
	}

	@Override
	public void delete(Long id) {
		ObjectifyService.ofy().delete().keys(ObjectifyService.ofy().load()
				.ancestor(Key.<Order> create(Order.class, id)).keys().list()).now(); 
	}
}
