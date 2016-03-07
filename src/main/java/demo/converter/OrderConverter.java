package demo.converter;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import demo.dto.AddressResource;
import demo.dto.OrderItemResource;
import demo.dto.OrderResource;
import demo.model.Address;
import demo.model.Order;
import demo.model.OrderItem;

@Component("orderConverter")
public class OrderConverter {

	@Resource(name = "productConverter")
	private ProductConverter productConverter;

	public OrderResource convert(Order order) {
		OrderResource orderResource = new OrderResource();

		orderResource.setId(order.getId());

		AddressResource addressResource = convertAddress(order.getAddress());
		orderResource.setAddress(addressResource);

		List<OrderItemResource> orderItemResources = new ArrayList<>();

		for (OrderItem orderItem : order.getOrderItems()) {
			orderItemResources.add(convertOrderItem(orderItem));
		}
		orderResource.setOrderItems(orderItemResources);
		
		orderResource.setUpdatedOn(order.getUpdatedOn());
		
		return orderResource;
	}

	public Iterable<OrderResource> convertAll(Iterable<Order> orders) {

		List<OrderResource> orderResources = new ArrayList<>();

		for (Order order : orders) {
			orderResources.add(convert(order));
		}

		return orderResources;
	}

	private OrderItemResource convertOrderItem(OrderItem orderItem) {
		OrderItemResource orderItemResource = new OrderItemResource();
		orderItemResource.setId(orderItem.getId());
		orderItemResource.setPrice(orderItem.getPrice());
		orderItemResource.setProduct(productConverter.convert(orderItem.getProduct()));
		orderItemResource.setQty(orderItem.getQty());
		return orderItemResource;
	}

	private AddressResource convertAddress(Address address) {
		AddressResource addressResource = new AddressResource();
		addressResource.setLine1(address.getLine1());
		addressResource.setLine2(address.getLine2());
		addressResource.setZipCode(address.getZipCode());
		return addressResource;
	}

}
