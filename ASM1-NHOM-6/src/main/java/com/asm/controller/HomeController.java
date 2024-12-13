package com.asm.controller;


import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.asm.model.Category;
import com.asm.model.Customer;
import com.asm.model.Product;
import com.asm.repository.CategoryRepository;
import com.asm.repository.CustomerRepository;
import com.asm.repository.ProductRepository;
import com.asm.service.CustomerService;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
	@Autowired 
	ProductRepository productRepository;
	@Autowired
	CategoryRepository categoryRepository;
	@Autowired
	CustomerRepository customerRepository;
	@Autowired
	CustomerService customerService;
	
		@RequestMapping({"/home","/"})
		public String showIndex(Model model) {
			 List<Object[]> topProducts = productRepository.findTopProducts();
			 List<Object[]> randomProducts = productRepository.findRandomProducts();
			 List<Category> categories = categoryRepository.findAll();
			 model.addAttribute("random", randomProducts);
			 model.addAttribute("top", topProducts);
			 model.addAttribute("categories", categories);

			 return "index";
		}
		
		@RequestMapping("/contactus")
		public String showcontact() {
			return "contactus";
		}
		
		@RequestMapping("/productdetails")
		    public String showProductDetail(@RequestParam("id") Integer productId, Model model) {
		        // Sử dụng repository để lấy thông tin sản phẩm từ cơ sở dữ liệu
		        Product product = productRepository.findById(productId).orElse(null);
		        List<Object[]> randomProducts = productRepository.findRandomProducts();
				model.addAttribute("random", randomProducts);
		        model.addAttribute("product", product);
		        return "productdetails";
		}
			
		@RequestMapping("/shop")
		public String listProduct(Model model,
			                @RequestParam(name = "keyword", defaultValue = "") String keyword,
			                @RequestParam(name = "page", defaultValue = "0") int page,
			                @RequestParam(name = "size", required = false) Optional<Integer> size,
			                @RequestParam(name = "category", required = false) String categoryName,
			                @RequestParam(value = "sort", required = false) Optional<String> sort,
			                @RequestParam(value = "locGia", required = false) Optional<String> locGia) {
			int pageSize = size.orElse(6); // Mặc định là 6 nếu không được cung cấp
			Pageable pageable;
			if (sort.isPresent() && sort.get().equals("asc")) {
			pageable = PageRequest.of(page, pageSize, Sort.by("price").ascending());
			} else if (sort.isPresent() && sort.get().equals("desc")) {
			pageable = PageRequest.of(page, pageSize, Sort.by("price").descending());
			} else {
			pageable = PageRequest.of(page, pageSize);
			}

		    Page<Product> productPage = null;

		    if (locGia.isPresent()) {
		        switch (locGia.get()) {
		            case "duoi100000":
		                productPage = productRepository.findByPriceLessThan(100000, pageable);
		                break;
		            case "tu100000den500000":
		                productPage = productRepository.findByPriceBetween(100000, 500000, pageable);
		                break;
		            case "tren500000":
		                productPage = productRepository.findByPriceGreaterThan(500000, pageable);
		                break;
		            default:
		                productPage = productRepository.findAll(pageable);
		        }
		    } else if (categoryName != null && !categoryName.isEmpty()) {
		        productPage = productRepository.findProductsByCategoryName(categoryName, pageable);
		    } else {
		        productPage = productRepository.findProductByName(keyword, pageable);
		    }

		    List<Category> categories = categoryRepository.findAll();
		    model.addAttribute("listProducts", productPage.getContent());
		    model.addAttribute("currentPage", page);
		    model.addAttribute("totalPages", productPage.getTotalPages());
		    model.addAttribute("categories", categories);

		    return "shop";
		}

}

		
	       
	



