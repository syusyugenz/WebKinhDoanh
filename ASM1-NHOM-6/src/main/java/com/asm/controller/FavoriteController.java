package com.asm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.asm.model.Customer;
import com.asm.model.Favorite;
import com.asm.model.Product;
import com.asm.repository.CustomerRepository;

import com.asm.repository.FavoriteRepository;
import com.asm.repository.ProductRepository;
import com.asm.service.CustomerService;


import jakarta.servlet.http.HttpSession;

import java.util.List;
@Controller
public class FavoriteController {
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    FavoriteRepository favoriteRepository;

    // Xem trang yêu thích
    @RequestMapping("/favorites/views")
    public String viewFavorites(Model model, HttpSession session) {
        Integer customerId = (Integer) session.getAttribute("customer_id");
        if (customerId == null) {
        	
            return "redirect:/auth/login";
        }
        List<Favorite> listFavorites = favoriteRepository.findByCustomerId(customerId);
        model.addAttribute("listFavorites", listFavorites);
        return "viewfavorites";
    }

    // Thêm sản phẩm vào trang yêu thích
    @RequestMapping("/favorites/add/{productId}")
    public String addFavorite(Model model, @PathVariable("productId") Integer productId, HttpSession session) {
        Integer customerId = (Integer) session.getAttribute("customer_id");
        if (customerId == null) {
            return "redirect:/auth/login";
        }
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null) {
            model.addAttribute("message", "Không tìm thấy thông tin khách hàng!");
            return "viewfavorites";
        }
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            model.addAttribute("message", "Sản phẩm không tồn tại!");
            return "viewfavorites";
        }
        Favorite favorite = favoriteRepository.findByCustomerIdAndProductId(customerId, productId);
        if (favorite == null) {
            favorite = new Favorite();
            favorite.setCustomer(customer);
            favorite.setProduct(product);
            favoriteRepository.saveAndFlush(favorite);
        } else {
            model.addAttribute("message", "Sản phẩm đã có trong danh sách yêu thích!");
            return "redirect:/favorites/views";
        }
        
        List<Favorite> listFavorites = favoriteRepository.findByCustomerId(customerId);
        model.addAttribute("listFavorites", listFavorites);
        return "viewfavorites";
    }

    // Xóa sản phẩm khỏi trang yêu thích
    @GetMapping("/favorites/remove/{id}")
    public String removeFavorite(Model model, @PathVariable("id") Integer id, HttpSession session) {
        Favorite favorite = favoriteRepository.findById(id).orElse(null);
        if (favorite != null) {
            favoriteRepository.delete(favorite);
        } else {
            model.addAttribute("message", "Sản phẩm yêu thích không tồn tại!");
            return "viewfavorites";
        }

        Integer customerId = (Integer) session.getAttribute("customer_id");
        if (customerId == null) {
            return "redirect:/auth/login";
        }
        
        List<Favorite> listFavorites = favoriteRepository.findByCustomerId(customerId);
        model.addAttribute("listFavorites", listFavorites);
        return "viewfavorites";
    }
}

