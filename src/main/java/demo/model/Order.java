package demo.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class Order {

	@Id
	private Long id;

	@Index
	private Date createdOn = new Date();

	private Date updatedOn;

	private Address address;

	private List<OrderItem> items = new ArrayList<OrderItem>();
	// @Load
	// private List<Ref<OrderItem>> items = new ArrayList<Ref<OrderItem>>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public List<OrderItem> getOrderItems() {
		List<OrderItem> itemsToReturn = this.items;// ArrayList<>();
		//
		// for(Ref<OrderItem> item: this.items) {
		// itemsToReturn.add(item.get());
		// }

		return itemsToReturn;
	}

	public void setOrderItems(List<OrderItem> items) {
		this.items = items;// new ArrayList<>();
		// for(OrderItem item: items) {
		//// item.setOrder(this);
		// this.items.add(Ref.create(item));
		// }
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((items == null) ? 0 : items.hashCode());
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
		Order other = (Order) obj;
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
		if (items == null) {
			if (other.items != null)
				return false;
		} else if (!items.equals(other.items))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Order [id=" + id + ", createdOn=" + createdOn + ", updatedOn=" + updatedOn + ", address=" + address
				+ ", items=" + items + "]";
	}

}
