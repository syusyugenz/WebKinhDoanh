package com.asm.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.asm.model.Category;
import com.asm.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

	@Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
	Page<Product> findProductByName(@Param("name") String name, Pageable pageable);
//	@Query("SELECT p FROM Product p WHERE p.name LIKE %:name% ORDER BY p.id DESC")
//	Page<Product> findProductByName(@Param("name") String name, Pageable pageable);

	@Query(value = "SELECT * FROM products WHERE name LIKE :name", nativeQuery = true)
	List<Product> findProductByName(@Param("name") String name);

	List<Product> findByPriceBetweenOrderByPriceAsc(int minPrice, int maxPrice);

	List<Product> findByPriceBetweenOrderByPriceDesc(int minPrice, int maxPrice);

	Page<Product> findByPriceBetween(int minPrice, int maxPrice, Pageable pageable);

	Page<Product> findByPriceLessThan(int price, Pageable pageable);

	Page<Product> findByPriceGreaterThan(int price, Pageable pageable);

	@Query("SELECT p.product_id, p.name, p.image, p.description, p.price, p.stock_quantity\r\n" + "FROM Product p\r\n"
			+ "JOIN p.orderItems o\r\n"
			+ "GROUP BY p.product_id, p.name, p.image, p.description, p.price, p.stock_quantity\r\n"
			+ "ORDER BY COUNT(o.order) DESC LIMIT 10")
	List<Object[]> findTopProducts();

	@Query("SELECT p.product_id, p.name, p.image, p.description, p.price, p.stock_quantity\r\n" + "FROM Product p\r\n"
			+ "JOIN p.orderItems o\r\n"
			+ "GROUP BY p.product_id, p.name, p.image, p.description, p.price, p.stock_quantity\r\n"
			+ "ORDER BY COUNT(o.order) ASC LIMIT 4")
	List<Object[]> findTop1Products();

	@Query("SELECT p.product_id, p.name, p.image, p.description, p.price, p.stock_quantity " + "FROM Product p "
			+ "ORDER BY RAND() LIMIT 10")
	List<Object[]> findRandomProducts();

	@Query("SELECT p FROM Product p WHERE p.category.category_name = :categoryName")
	Page<Product> findProductsByCategoryName(@Param("categoryName") String categoryName, Pageable pageable);

	List<Product> findAllByCategory(Category category);

}
