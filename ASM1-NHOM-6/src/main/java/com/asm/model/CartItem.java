package com.asm.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Entity
@Table(name = "cartitems")
public class CartItem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer cart_item_id;
	private int quantity;

	@ManyToOne @JoinColumn(name = "customer_id")
    @JsonBackReference
	private Customer customer;	
	@ManyToOne @JoinColumn(name = "product_id")
	private Product product;
	
	public CartItem() {
		// TODO Auto-generated constructor stub
	}
	
	public Integer getCart_item_id() {
		return cart_item_id;
	}
	public void setCart_item_id(Integer cart_item_id) {
		this.cart_item_id = cart_item_id;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	
	
}


