package demo.repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;

import demo.model.Measurement;
import demo.model.Order;
import demo.model.OrderItem;
import demo.model.Product;
import demo.util.Profiler;

@Repository
public class GoogleDatastoreOrderRepository implements OrderRepository {

	@Resource
	private Profiler profiler;

	@Override
	public Iterable<Order> findAll() {
		return ObjectifyService.ofy().load().type(Order.class).list();
	}

	@Override
	public Order findOne(Long id) {
		Order order = ObjectifyService.ofy().load().type(Order.class).id(id).now();

		Measurement m = new Measurement("OrderRepository", "getAllOrderItemsByAncestor");
		List<OrderItem> orderItems = ObjectifyService.ofy().cache(false).load().type(OrderItem.class).ancestor(order).list();
		profiler.submitMeasurementAsync(m);

		m = new Measurement("OrderRepository", "loopingToMaterialize");
		for (OrderItem orderItem : orderItems) {
			orderItem.hashCode();
		}
		profiler.submitMeasurementAsync(m);
		
		m = new Measurement("OrderRepository", "loopingOverMaterialized");
		for (OrderItem orderItem : orderItems) {
			orderItem.hashCode();
		}
		profiler.submitMeasurementAsync(m);
		
		
		order.setOrderItems(orderItems);

		Map<Key<Product>, OrderItem> itemsToProductMap = new HashMap<>();
		for (OrderItem orderItem : orderItems) {
			itemsToProductMap.put(Key.create(Product.class, orderItem.getProductSku()), orderItem);
		}

		m = new Measurement("OrderRepository", "getProductsForAllOrderItemsByKeys");

		Map<Key<Product>, Product> products = ObjectifyService.ofy().cache(false).load().keys(itemsToProductMap.keySet());

		profiler.submitMeasurementAsync(m);

		for (Entry<Key<Product>, OrderItem> entry : itemsToProductMap.entrySet()) {
			entry.getValue().setProduct(products.get(entry.getKey()));
		}

		return order;
	}

	@Override
	public Long save(Order order) { // TODO - wrap with transaction?
		order.setUpdatedOn(new Date());

		ObjectifyService.ofy().save().entity(order).now();

		List<OrderItem> orderItems = order.getOrderItems();

		for (OrderItem orderItem : orderItems) {
			orderItem.setOrder(order);
		}

		ObjectifyService.ofy().save().entities(orderItems).now();
		return order.getId();
	}

	@Override
	public void delete(Long id) {
		ObjectifyService.ofy().delete().keys(ObjectifyService.ofy().load().ancestor(Key.<Order> create(Order.class, id)).keys().list())
				.now();
	}

	@Override
	public Iterable<Order> findAllByCreatedOn(Date orderDate) {
		Date orderDateStart = new DateTime(orderDate).withTimeAtStartOfDay().toDate();
		Date orderDateEnd = new DateTime(orderDate).plusDays(1).withTimeAtStartOfDay().toDate();

		Measurement m = new Measurement("OrderRepository", "findAllByCreatedOn");
		List<Order> orders = ObjectifyService.ofy().load().type(Order.class).filter("createdOn >=", orderDateStart)
				.filter("createdOn <", orderDateEnd).order("-createdOn").list();
		profiler.submitMeasurementAsync(m);
		
		m = new Measurement("OrderRepository", "loopingToMaterialize");
		for (Order order : orders) {
			order.hashCode();
		}
		profiler.submitMeasurementAsync(m);

		m = new Measurement("OrderRepository", "loopingOverMaterialized");
		for (Order order : orders) {
			order.hashCode();
		}
		profiler.submitMeasurementAsync(m);

		return orders;
	}
}
