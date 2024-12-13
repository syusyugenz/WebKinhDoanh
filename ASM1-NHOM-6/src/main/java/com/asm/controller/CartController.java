package com.asm.controller;

import java.util.List;

import org.aspectj.weaver.patterns.IfPointcut.IfFalsePointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.asm.model.CartItem;
import com.asm.model.Customer;
import com.asm.model.Product;
import com.asm.repository.CartItemRepository;
import com.asm.repository.CustomerRepository;
import com.asm.repository.ProductRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class CartController {
	@Autowired
	CustomerRepository customerRepository;
	@Autowired
	ProductRepository productRepository;
	@Autowired
	CartItemRepository cartItemRepository;
	
		@RequestMapping("/cart/views")
		public String viewCart(Model model, HttpSession session) {
		Integer customerId = (Integer) session.getAttribute("customer_id");
		if(customerId == null) {
			return "redirect:/auth/login";
		}
		List<CartItem> listCartItems = cartItemRepository.findByCustomerId(customerId);
	 	model.addAttribute("listCartItems",listCartItems);
	    return updateCartView(model, customerId);
		}
	
		@RequestMapping("/cart/add/{productId}")
		public String addcart(Model model, 
		                      @PathVariable("productId") Integer productId, 
		                      @RequestParam(value = "quantity", required = false) Integer quantity, 
		                      HttpSession session) {
		    Integer customerId = (Integer) session.getAttribute("customer_id");
		    if (customerId == null) {
		        return "redirect:/auth/login";
		    }
		    Customer customer = customerRepository.findById(customerId).orElse(null);
		    if (customer == null) {
		        model.addAttribute("message", "Không tìm thấy thông tin khách hàng!");
		        return "viewcart";
		    }
		    Product product = productRepository.findById(productId).orElse(null);
		    if (product == null) {
		        model.addAttribute("message", "Sản phẩm không tồn tại!");
		        return "viewcart";
		    }

		    int requestedQuantity = (quantity != null) ? quantity : 1;
		    int availableQuantity = product.getStock_quantity();

		    if (requestedQuantity > availableQuantity) {
		        model.addAttribute("message", "Không đủ số lượng sản phẩm!");
		        return updateCartView(model, customerId);
		    }

		    CartItem cartItem = cartItemRepository.findByAccountIdAndProductId(customerId, productId);
		    if (cartItem == null) {
		        cartItem = new CartItem();
		        cartItem.setCustomer(customer);
		        cartItem.setProduct(product);
		        cartItem.setQuantity(requestedQuantity);
		    } else {
		        int newQuantity = cartItem.getQuantity() + requestedQuantity;
		        if (newQuantity > availableQuantity) {
		            model.addAttribute("message", "Không đủ số lượng sản phẩm!");
		            return updateCartView(model, customerId);
		        }
		        cartItem.setQuantity(newQuantity);
		    }
		    cartItemRepository.saveAndFlush(cartItem);

		    return updateCartView(model, customerId);
		}

		@PostMapping("/cart/update")
		public String updateCart(Model model, @RequestParam("id") Integer id, @RequestParam("qty") int qty, HttpSession session) {
		    CartItem cartItem = cartItemRepository.findById(id).orElse(null);
		    if (cartItem == null) {
		        return "redirect:/cart/view";
		    }
		    if (qty <= 0) {
		        cartItemRepository.delete(cartItem);
		    } else {
		        cartItem.setQuantity(qty);
		        cartItemRepository.saveAndFlush(cartItem);
		    }

		    Integer customerId = (Integer) session.getAttribute("customer_id");
		    if (customerId == null) {
		        return "redirect:/auth/login";
		    }

		    return updateCartView(model, customerId);
		}

		private String updateCartView(Model model, Integer customerId) {
		    List<CartItem> listCartItems = cartItemRepository.findByCustomerId(customerId);
		    double totalAmount = 0.0;
		    for (CartItem item : listCartItems) {
		        totalAmount += item.getProduct().getPrice() * item.getQuantity();
		    }
		    model.addAttribute("totalAmount", totalAmount);
		    model.addAttribute("listCartItems", listCartItems);
		    return "viewcart";
	}
	 
	 @GetMapping("/cart/remove/{id}")
		public String removeCart(Model model, @PathVariable("id")Integer id,HttpSession session) {
			CartItem cartItem = cartItemRepository.findById(id).get();
			cartItemRepository.delete(cartItem);
			Integer customerId = (Integer) session.getAttribute("customer_id");
			if(customerId == null) {
				return "redirect:/auth/login";
			}
			List<CartItem> listCartItems = cartItemRepository.findByCustomerId(customerId);
		 	model.addAttribute("listCartItems",listCartItems);
		    return updateCartView(model, customerId);
		}
	 
	 @RequestMapping("/cart/removeAll")
	 public String removeAllCartItems(Model model, HttpSession session) {
	     Integer customerId = (Integer) session.getAttribute("customer_id");
	     if (customerId == null) {
	         return "redirect:/auth/login";
	     }
	     List<CartItem> listCartItems = cartItemRepository.findByCustomerId(customerId);
	     cartItemRepository.deleteAll(listCartItems);

	     return updateCartView(model, customerId);
	 }
}
