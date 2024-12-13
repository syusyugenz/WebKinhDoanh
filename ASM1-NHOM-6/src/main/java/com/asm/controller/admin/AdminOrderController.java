package com.asm.controller.admin;

import com.asm.model.Order;
import com.asm.model.OrderItem;
import com.asm.model.Product;
import com.asm.repository.OrderRepository;
import com.asm.repository.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("admin")
public class AdminOrderController {

	 	@Autowired
	    OrderRepository orderRepository;
	 	@Autowired
	 	ProductRepository productRepository;
	    @GetMapping("/orderwait")
	    public String showOrdersWait(Model model, @RequestParam(defaultValue = "0") int page) {
	    	Pageable pageable = PageRequest.of(page, 5, Sort.by(Sort.Order.desc("orderId"))); // 5 đơn hàng mỗi trang, sắp xếp theo ID giảm dần
	    	Page<Order> orderPage = orderRepository.findByStatus("Đang chờ", pageable);
	        model.addAttribute("orderPage", orderPage);
	        return "admin/orderwait";
	    }
	    
	    @GetMapping("/orderdeliver")
	    public String showOrdersDeliver(Model model, @RequestParam(defaultValue = "0") int page) {
	    	Pageable pageable = PageRequest.of(page, 5, Sort.by(Sort.Order.desc("orderId"))); // 5 đơn hàng mỗi trang, sắp xếp theo ID giảm dần
	        Page<Order> orderPage = orderRepository.findByStatus("Đang giao", pageable);
	        model.addAttribute("orderPage", orderPage);
	        return "admin/orderdeliver";
	    }

	    @GetMapping("/ordersuccess")
	    public String showOrdersSuccess(Model model, @RequestParam(defaultValue = "0") int page) {
	    	Pageable pageable = PageRequest.of(page, 5, Sort.by(Sort.Order.desc("orderId"))); // 5 đơn hàng mỗi trang, sắp xếp theo ID giảm dần
	        Page<Order> orderPage = orderRepository.findByStatus("Giao thành công", pageable);
	        model.addAttribute("orderPage", orderPage);
	        return "admin/ordersuccess";
	    }

	    @GetMapping("/ordercancel")
	    public String showOrdersCancel(Model model, @RequestParam(defaultValue = "0") int page) {
	    	Pageable pageable = PageRequest.of(page, 5, Sort.by(Sort.Order.desc("orderId"))); // 5 đơn hàng mỗi trang, sắp xếp theo ID giảm dần
	        Page<Order> orderPage = orderRepository.findByStatus("Đã hủy", pageable);
	        model.addAttribute("orderPage", orderPage);
	        return "admin/ordercancel";
	    }

	    
	    @PostMapping("/orderdeliver/{orderId}")
	    public String deliverOrder(@PathVariable Integer orderId, RedirectAttributes redirectAttributes) {
	        orderRepository.updateStatus(orderId, "Đang giao");
	        return "redirect:/admin/orderdeliver";
	    }
	    
	    @PostMapping("/ordercancel/{orderId}")
	    public String cancelOrder(@PathVariable Integer orderId, RedirectAttributes redirectAttributes) {
	    	Order order = orderRepository.findById(orderId).orElse(null);
	        if (order == null) {
	            return "error"; // Return an error page if the order is not found
	        }
	        if ("Đang chờ".equals(order.getStatus())) {
	            // Update stock quantity for each product in the order
	            for (OrderItem orderItem : order.getOrderItems()) {
	                Product product = orderItem.getProduct();
	                int newStockQuantity = product.getStock_quantity() + orderItem.getQuantity();
	                product.setStock_quantity(newStockQuantity);
	                productRepository.save(product);
	            }
	            order.setStatus("Đã hủy");
	            orderRepository.save(order);
	        }
	        return "redirect:/admin/ordercancel";
	    }
	    
	    
	    @PostMapping("/orderview/{orderId}")
	    public String viewOrderDetails(@PathVariable Integer orderId, Model model) {
	        Order order = orderRepository.findById(orderId).orElse(null);
	        if (order == null) {
	            return "error"; // Return an error page if the order is not found
	        }
	        model.addAttribute("order", order);
	        model.addAttribute("orderItems", order.getOrderItems());
	        return "admin/adminorderdetails";
	    }
}
