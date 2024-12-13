package com.asm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.asm.model.CartItem;
import com.asm.model.Product;

public interface CartItemRepository extends JpaRepository<CartItem, Integer>{
	@Query("select c from CartItem c where c.customer.customer_id=:customerId and c.product.product_id = :productId")
	public CartItem findByAccountIdAndProductId(Integer customerId, Integer productId);
	@Query("Select c from CartItem c where c.customer.customer_id=:customerId")
	List<CartItem> findByCustomerId(Integer customerId);
}
