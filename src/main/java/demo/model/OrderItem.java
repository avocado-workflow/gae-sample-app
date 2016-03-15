package demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Parent;

@Entity
public class OrderItem {

	@JsonIgnore
	@Parent
	private Key<Order> order;

	@Ignore
	private Product product;
	
	@JsonIgnore
	private String productSku;
	
	private int qty;
	private double price;

	@Id
	public Long id;

	public int getQty() {
		return qty;
	}

	public void setQty(int qty) {
		this.qty = qty;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public Key<Order> getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = Key.create(Order.class, order.getId());
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.productSku = product.getCode();
		this.product = product;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((order == null) ? 0 : order.hashCode());
		long temp;
		temp = Double.doubleToLongBits(price);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((product == null) ? 0 : product.hashCode());
		result = prime * result + qty;
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
		OrderItem other = (OrderItem) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (order == null) {
			if (other.order != null)
				return false;
		} else if (!order.equals(other.order))
			return false;
		if (Double.doubleToLongBits(price) != Double.doubleToLongBits(other.price))
			return false;
		if (product == null) {
			if (other.product != null)
				return false;
		} else if (!product.equals(other.product))
			return false;
		if (qty != other.qty)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "OrderItem [order=" + order + ", product=" + product + ", qty=" + qty + ", price=" + price + ", id=" + id
				+ "]";
	}

	public String getProductSku() {
		return productSku;
	}
}
