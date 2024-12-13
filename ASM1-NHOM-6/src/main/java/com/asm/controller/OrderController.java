	package com.asm.controller;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.asm.model.CartItem;
import com.asm.model.Customer;
import com.asm.model.Order;
import com.asm.model.OrderItem;
import com.asm.model.Product;
import com.asm.repository.CartItemRepository;
import com.asm.repository.CustomerRepository;
import com.asm.repository.OrderItemRepository;
import com.asm.repository.OrderRepository;
import com.asm.repository.ProductRepository;
import com.asm.service.SessionService;
import com.asm.service.VNPayService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class OrderController {
	@Autowired
	CustomerRepository customerRepository;
	@Autowired
	OrderRepository orderRepository;
	@Autowired
	OrderItemRepository orderItemRepository;
	@Autowired
	SessionService sessionService;
	@Autowired
	CartItemRepository cartItemRepository;
	@Autowired
	ProductRepository productRepository;
	@Autowired
	VNPayService vnPayService;
	@RequestMapping("/orders/pay")
	public String order(@RequestParam("ids")String ids, Model model,HttpServletRequest req) {
		String[]idStrings = ids.split(",");
		double total = 0;
		List<CartItem> cartItems = new ArrayList<>();
		for (String id : idStrings) {
			CartItem cartItem = cartItemRepository.findById(Integer.valueOf(id)).get();
			total = total + cartItem.getQuantity() * cartItem.getProduct().getPrice();
			cartItems.add(cartItem);
		}
		
		model.addAttribute("cartItems", cartItems);
		model.addAttribute("total", total);
		sessionService.setAttribute("cartItems", cartItems);
		return "order";
	}
	
	@PostMapping("/order/pay")
	public String pay(@RequestParam("payMethod") int payMethod, Model model, HttpSession session, RedirectAttributes redirectAttributes, HttpServletRequest req) {
	    Integer customerId = (Integer) session.getAttribute("customer_id");
	    Customer customer = customerRepository.findById(customerId).orElse(null);
	    if (customer == null) {
	        // Handle the case where customer is not found
	        return "error";
	    }

	    Order order = new Order();
	    order.setCustomer(customer);
	    order.setOrderDate(new Date());
	    order.setStatus("Đang chờ");
	    order.setTotalAmount(0.0);

	    Order savedOrder = orderRepository.save(order);

	    List<CartItem> cartItems = sessionService.getAttribute("cartItems");
	    double totalAmount = savedOrder.getTotalAmount();
	    for (CartItem cartItem : cartItems) {
	        OrderItem orderItem = new OrderItem();
	        orderItem.setOrder(order);
	        orderItem.setPrice(cartItem.getProduct().getPrice());
	        orderItem.setProduct(cartItem.getProduct());
	        orderItem.setQuantity(cartItem.getQuantity());
	        orderItemRepository.saveAndFlush(orderItem);

	        double subtotal = orderItem.getPrice() * orderItem.getQuantity();
	        totalAmount += subtotal;

	        // Deduct stock_quantity from the product
	        Product product = cartItem.getProduct();
	        int remainingQuantity = product.getStock_quantity() - cartItem.getQuantity();
	        product.setStock_quantity(remainingQuantity);
	        productRepository.save(product);

	        cartItemRepository.delete(cartItem);
	    }

	    savedOrder.setTotalAmount(totalAmount);
	    orderRepository.saveAndFlush(savedOrder);

	    sessionService.removeAttribute("cartItems");

	    List<OrderItem> orderItems = savedOrder.getOrderItems();
	    BigInteger totalPay = new BigInteger(String.valueOf(totalAmount).replace(".0", ""));
	    String baseUrl = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort();

	    if (payMethod == 1) {
	        // Handle cash on delivery logic here
	        // You can redirect to a specific page or display a message
	        redirectAttributes.addFlashAttribute("order", savedOrder);
	        redirectAttributes.addFlashAttribute("orderItems", orderItems);
	        redirectAttributes.addFlashAttribute("totalAmount", totalAmount);
	        return "redirect:/order/view";
	    } else if (payMethod == 2) {
	        // Handle VNPay logic here
	        String vnpayUrl = vnPayService.createOrder(totalPay, "Thanh toan hoa don mua hang", baseUrl);
	        redirectAttributes.addFlashAttribute("order", savedOrder);
	        redirectAttributes.addFlashAttribute("orderItems", orderItems);
	        redirectAttributes.addFlashAttribute("totalAmount", totalAmount);
	        return "redirect:" + vnpayUrl;
	    }
	    return "error";
	}

	
	@RequestMapping("/order/view")
	public String orderView() {
		
		return "ordersuccess";
	}
	
	@RequestMapping("/order/view/{orderId}")
	public String viewOrderDetails(@PathVariable Integer orderId, Model model) {
	    Order order = orderRepository.findById(orderId).orElse(null);
	    if (order == null) {
	        return "error";
	    }
	    model.addAttribute("order", order);
	    List<OrderItem> orderItems = order.getOrderItems();
	    model.addAttribute("orderItems", orderItems);
	    return "orderdetails";
	}
	
	@RequestMapping("/order/history")
	public String orderHistory(Model model,HttpSession session) {
		Integer customerId = (Integer) session.getAttribute("customer_id");
	    List<Order> orderswait = orderRepository.findByCustomerIdAndStatus(customerId, "Đang chờ");
	    model.addAttribute("orderswait", orderswait);
	    
	    List<Order> ordersdelivery = orderRepository.findByCustomerIdAndStatus(customerId, "Đang giao");
	    model.addAttribute("ordersdelivery", ordersdelivery);
	    
	    List<Order> orderssuccess = orderRepository.findByCustomerIdAndStatus(customerId, "Giao thành công");
	    model.addAttribute("orderssuccess", orderssuccess);
	    
	    List<Order> orderscancel = orderRepository.findByCustomerIdAndStatus(customerId, "Đã hủy");
	    model.addAttribute("orderscancel", orderscancel);
	    return "user/orderhistory";
	}
	
	@PostMapping("/order/accept/{orderId}")
    public String acceptOrder(@PathVariable Integer orderId) {
        Order order = orderRepository.findById(orderId).get();
        if (order.getStatus().equals("Đang giao")) {
            order.setStatus("Giao thành công");
            orderRepository.save(order);
        }
        return "redirect:/order/history";
    }
	
	 @PostMapping("/order/cancel/{orderId}")
	    public String cancelOrder(@PathVariable Integer orderId) {
	        Order order = orderRepository.findById(orderId).orElse(null);
	        if (order != null && order.getStatus().equals("Đang chờ")) {
	            List<OrderItem> orderItems = order.getOrderItems();

	            for (OrderItem orderItem : orderItems) {
	                Product product = orderItem.getProduct();
	                int restoredQuantity = product.getStock_quantity() + orderItem.getQuantity();
	                product.setStock_quantity(restoredQuantity);
	                productRepository.save(product);
	            }

	            order.setStatus("Đã hủy");
	            orderRepository.save(order);
	        }
	        return "redirect:/order/history";
	    }
	 
	 @PostMapping("/order/buyrepeat/{orderId}")
	 public String buyAgain(@PathVariable Integer orderId) {
	     Order order = orderRepository.findById(orderId).orElse(null);
	     if (order != null && order.getStatus().equals("Đã hủy")) {
	         List<OrderItem> orderItems = order.getOrderItems();

	         for (OrderItem orderItem : orderItems) {
	             Product product = orderItem.getProduct();
	             int newQuantity = product.getStock_quantity() - orderItem.getQuantity();
	             if (newQuantity < 0) {
	                 return "error"; 
	             }
	             product.setStock_quantity(newQuantity);
	             productRepository.save(product);
	         }

	         order.setStatus("Đang chờ");
	         orderRepository.save(order);
	     }
	     return "redirect:/order/history";
	 }
}
