package com.asm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.asm.model.Favorite;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {
		@Query("select f from Favorite f where f.customer.customer_id=:customerId and f.product.product_id = :productId")
	    public Favorite findByCustomerIdAndProductId(Integer customerId, Integer productId);

	    // Sử dụng truy vấn tùy chỉnh để lấy danh sách yêu thích dựa trên id của khách hàng
	    @Query("Select f from Favorite f where f.customer.customer_id=:customerId")
	    List<Favorite> findByCustomerId(Integer customerId);
	    
//	    // Truy vấn để lấy ra danh sách sản phẩm được yêu thích và số lượt yêu thích của chúng
//	    @Query("SELECT p, COUNT(f) FROM Product p LEFT JOIN Favorite f ON p.id = f.product.id GROUP BY p")
//	    List<Object[]> findProductsAndFavoritesCount();
	    @Query("SELECT p, COUNT(f) FROM Product p LEFT JOIN Favorite f ON p.id = f.product.id GROUP BY p HAVING COUNT(f) > 0")
	    List<Object[]> findProductsWithFavorites();
	    
}