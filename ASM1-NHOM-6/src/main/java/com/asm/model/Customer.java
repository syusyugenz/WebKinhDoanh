package com.asm.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Data
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Entity
@Table(name = "Customers")
public class Customer {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer customer_id;
	private String name;
	@Column(unique = true)
	private String email;
	private String password;
	@Column(unique = true,length = 10)
	private String phone;
	private String address;
	private boolean admin;
	private String otp;
	private Boolean enabled;
	private LocalDateTime otp_expiry;
	
	
	@OneToMany(mappedBy = "customer")
	@JsonManagedReference
    @ToString.Exclude
    @JsonIgnore
	List<Order> orders;
	
	@OneToMany(mappedBy = "customer")
	@JsonManagedReference
    @ToString.Exclude
    @JsonIgnore
	List<CartItem> cartsItems;
	
	@OneToMany(mappedBy = "customer")
	@JsonManagedReference
    @ToString.Exclude
    @JsonIgnore
	List<Favorite> favorites;
	
	public Customer() {
		// TODO Auto-generated constructor stub
	}

	public Integer getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(Integer customer_id) {
		this.customer_id = customer_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public LocalDateTime getOtp_expiry() {
		return otp_expiry;
	}

	public void setOtp_expiry(LocalDateTime otp_expiry) {
		this.otp_expiry = otp_expiry;
	}

	public List<Order> getOrders() {
		return orders;
	}

	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}

	public List<CartItem> getCartsItems() {
		return cartsItems;
	}

	public void setCartsItems(List<CartItem> cartsItems) {
		this.cartsItems = cartsItems;
	}

	public List<Favorite> getFavorites() {
		return favorites;
	}

	public void setFavorites(List<Favorite> favorites) {
		this.favorites = favorites;
	}
	
	
}