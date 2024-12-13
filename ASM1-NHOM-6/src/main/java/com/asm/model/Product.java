package com.asm.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Entity
@Table(name = "Products")
public class Product{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer product_id;
	String name;
	String image;
	String description;
	double price;
	int stock_quantity;
	
	@ManyToOne @JoinColumn(name = "category_id")
	@JsonBackReference
    @ToString.Exclude
	Category category;
	
	
	@OneToMany(mappedBy = "product")
	@JsonBackReference
    @ToString.Exclude
	List<OrderItem> orderItems;
	
	@OneToMany(mappedBy = "product")
	@JsonBackReference
    @ToString.Exclude
	List<CartItem> cartItems;
	
	@OneToMany(mappedBy = "product")
	@JsonBackReference
    @ToString.Exclude
	List<Favorite> favorites;
	
	public Product() {
		// TODO Auto-generated constructor stub
	}

	public Integer getProduct_id() {
		return product_id;
	}

	public void setProduct_id(Integer product_id) {
		this.product_id = product_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getStock_quantity() {
		return stock_quantity;
	}

	public void setStock_quantity(int stock_quantity) {
		this.stock_quantity = stock_quantity;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public List<OrderItem> getOrderItems() {
		return orderItems;
	}

	public void setOrderItems(List<OrderItem> orderItems) {
		this.orderItems = orderItems;
	}

	public List<CartItem> getCartItems() {
		return cartItems;
	}

	public void setCartItems(List<CartItem> cartItems) {
		this.cartItems = cartItems;
	}

	public List<Favorite> getFavorites() {
		return favorites;
	}

	public void setFavorites(List<Favorite> favorites) {
		this.favorites = favorites;
	}
	
	
}
