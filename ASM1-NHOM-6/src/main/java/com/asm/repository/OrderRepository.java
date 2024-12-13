package com.asm.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.asm.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer>{
    
	
	Page<Order> findByStatus(String status, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Order o SET o.status = :newStatus WHERE o.orderId = :orderId")
    void updateStatus(@Param("orderId") Integer orderId, @Param("newStatus") String newStatus);
    
    @Query("SELECT o " +
    		"FROM Order o " +
    		"INNER JOIN o.orderItems oi " +
    		"INNER JOIN oi.product p " +
    		"WHERE o.customer.customer_id = :customerId " +
    		"AND o.status = :statusId " +
    		"ORDER BY o.id DESC")
     List<Order> findByCustomerIdAndStatus(@Param("customerId") Integer customerId, @Param("statusId") String statusId);
    
    
    
    //Thống kê
    
    @Query("SELECT c.name AS customerName, " +
	           "p.name AS productName, " +
	           "p.price AS productPrice, " +
	           "o.orderDate AS orderDate, " +
	           "oi.quantity AS quantity, " +
	           "(oi.quantity * oi.price) AS totalAmount " +
	           "FROM Customer c " +
	           "JOIN c.orders o " +
	           "JOIN o.orderItems oi " +
	           "JOIN oi.product p " +
	           "ORDER BY c.name, o.orderDate")
	
    Page<Object[]> findTotal(Pageable pageable);	 
    @Query("SELECT SUM(oi.quantity * oi.price) " +
	           "FROM Order o " +
	           "JOIN o.orderItems oi")
	    Double findTotalRevenue();
	 @Query("SELECT c.name AS customerName, p.name AS productName, p.price AS productPrice, "
	            + "o.orderDate AS orderDate, oi.quantity AS quantity, (oi.quantity * oi.price) AS totalAmount "
	            + "FROM Customer c "
	            + "JOIN Order o ON c.customer_id = o.customer.customer_id "
	            + "JOIN OrderItem oi ON o.orderId = oi.order.orderId "
	            + "JOIN Product p ON oi.product.product_id = p.product_id "
	            + "WHERE o.orderDate BETWEEN :startDate AND :endDate "
	            + "ORDER BY c.name, o.orderDate")
	    List<Object[]> findOrderDetailsByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

	    @Query("SELECT SUM(oi.quantity * oi.price) FROM Order o "
	            + "JOIN o.orderItems oi "
	            + "WHERE o.orderDate BETWEEN :startDate AND :endDate")
	    Double findTotalRevenueByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

	    @Query("SELECT c.name AS customerName, p.name AS productName, p.price AS productPrice, "
	            + "o.orderDate AS orderDate, oi.quantity AS quantity, (oi.quantity * oi.price) AS totalAmount "
	            + "FROM Customer c "
	            + "JOIN Order o ON c.customer_id = o.customer.customer_id "
	            + "JOIN OrderItem oi ON o.orderId = oi.order.orderId "
	            + "JOIN Product p ON oi.product.product_id = p.product_id "
	            + "ORDER BY c.name, o.orderDate")
	    List<Object[]> findOrderDetails();
	    
	    
	    @Query("SELECT o FROM Order o ORDER BY o.totalAmount DESC")
	    List<Order> findTopOrders(Pageable pageable);
}

