package demo.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderResource {

	private Long id;

	private Date updatedOn;

	private AddressResource address;

	private List<OrderItemResource> orderItems = new ArrayList<OrderItemResource>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	public AddressResource getAddress() {
		return address;
	}

	public void setAddress(AddressResource address) {
		this.address = address;
	}

	public List<OrderItemResource> getOrderItems() {
		return orderItems;
	}

	public void setOrderItems(List<OrderItemResource> orderItems) {
		this.orderItems = orderItems;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((orderItems == null) ? 0 : orderItems.hashCode());
		result = prime * result + ((updatedOn == null) ? 0 : updatedOn.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OrderResource other = (OrderResource) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (orderItems == null) {
			if (other.orderItems != null)
				return false;
		} else if (!orderItems.equals(other.orderItems))
			return false;
		if (updatedOn == null) {
			if (other.updatedOn != null)
				return false;
		} else if (!updatedOn.equals(other.updatedOn))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "OrderResource [id=" + id + ", updatedOn=" + updatedOn + ", address="
				+ address + ", orderItems=" + orderItems + "]";
	}

}
