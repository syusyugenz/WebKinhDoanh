package com.asm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.asm.model.Customer;
import org.springframework.transaction.annotation.Transactional;

public interface CustomerRepository extends JpaRepository<Customer, Integer>{
	public Customer findByEmail(String email);
	public Customer findByPhone(String phone);
	
	
	 @Modifying
	 @Transactional
	 @Query("update Customer c set c.enabled = :enabled where c.customer_id = :customerId")
	 void updateCustomerStatus(@Param("customerId") Integer customerId, @Param("enabled") Boolean enabled);
}
