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

@Component
public class OrderResourceConverter {

	@Resource
	private ProductResourceConverter productResourceConverter;

	public Order convert(OrderResource orderResource) {
		Order order = new Order();
		order.setId(orderResource.getId());

		Address address = convertAddressResource(orderResource.getAddress());
		order.setAddress(address);

		List<OrderItem> orderItems = convertOrderItemResources(orderResource.getOrderItems());
		order.setOrderItems(orderItems);
		order.setUpdatedOn(orderResource.getUpdatedOn());
		return order;
	}

	private List<OrderItem> convertOrderItemResources(List<OrderItemResource> orderItemResources) {
		List<OrderItem> orderItems = new ArrayList<>();
		
		for (OrderItemResource orderItemResource : orderItemResources) {
			orderItems.add(convertOrderItemResource(orderItemResource));
		}
		
		return orderItems;
	}

	private OrderItem convertOrderItemResource(OrderItemResource orderItemResource) {
		OrderItem orderItem = new OrderItem();
		orderItem.setId(orderItemResource.getId());
		orderItem.setPrice(orderItemResource.getPrice());
		orderItem.setProduct(productResourceConverter.convert(orderItemResource.getProduct()));
		orderItem.setQty(orderItemResource.getQty());
		return orderItem;
	}

	private Address convertAddressResource(AddressResource addressResource) {
		Address address = new Address();
		address.setLine1(addressResource.getLine1());
		address.setLine2(addressResource.getLine2());
		address.setZipCode(addressResource.getZipCode());
		return address;
	}

}
