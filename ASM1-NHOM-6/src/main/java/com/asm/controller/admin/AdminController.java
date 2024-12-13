package com.asm.controller.admin;

import com.asm.model.Category;
import com.asm.model.Customer;
import com.asm.model.Order;
import com.asm.model.Product;
import com.asm.repository.CategoryRepository;
import com.asm.repository.CustomerRepository;
import com.asm.repository.OrderRepository;
import com.asm.repository.ProductRepository;
import com.asm.service.CategoryService;
import com.asm.service.CustomerService;
import com.asm.service.OrderService;
import com.asm.service.ProductService;
import com.asm.service.SessionService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("admin")
public class AdminController {

    @Autowired
    private CategoryRepository categoryRepository;   
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ProductService productService;
    
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private SessionService sessionService;
    
    // Show Admin Home
    @GetMapping("index")
    public String showAdmin(Model model) {
    	long productCount = productService.countProducts();
    	long customerCount = customerService.countCustomers();
    	List<Order> topOrders = orderService.findTopOrders(PageRequest.of(0, 5));  // Get top 5 orders

    	    model.addAttribute("productCount", productCount);
    	    model.addAttribute("customerCount", customerCount);
    	    model.addAttribute("topOrders", topOrders);
        return "admin/index";
    }

    // Customer Management
    @GetMapping("/managercustomer")
    public String showCustomers(Model model, @RequestParam(defaultValue = "0") int page) {

        Pageable pageable = PageRequest.of(page, 5); // 5 khách hàng mỗi trang
        Page<Customer> customerPage = customerService.getAllCustomers(pageable);
        model.addAttribute("customerPage", customerPage);
        return "admin/customer";
    }

    @GetMapping("/addcustomer")
    public String addCustomerForm(Model model) {
        model.addAttribute("customer", new Customer());
        return "admin/addCustomer";
    }

    @PostMapping("/savecustomer")
    public String saveCustomer(@ModelAttribute Customer customer, Model model, RedirectAttributes redirectAttributes) {
        if (customer.getPhone().length() > 10) {
            redirectAttributes.addFlashAttribute("message", "Phone number cannot be longer than 11 characters.");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            return "redirect:/admin/addcustomer";
        }
        try {
            customer.setEnabled(true);  // Đảm bảo giá trị enabled là true khi tạo mới
            customerService.saveCustomer(customer);
            redirectAttributes.addFlashAttribute("message", "Customer saved successfully!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Failed to save customer.");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        }
        return "redirect:/admin/managercustomer";
    }


    @GetMapping("lockcustomer/{id}")
    public String lockCustomer(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            Customer customer = customerService.findById(id);
            if (customer != null && !customer.isAdmin()) {
                customerService.updateCustomerStatus(id, false);
                redirectAttributes.addFlashAttribute("message", "Customer locked successfully!");
                redirectAttributes.addFlashAttribute("alertClass", "alert-success");
            } else {
                redirectAttributes.addFlashAttribute("message", "Cannot lock an admin account.");
                redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Failed to lock customer.");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        }
        return "redirect:/admin/managercustomer";
    }

    @GetMapping("unlockcustomer/{id}")
    public String unlockCustomer(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            Customer customer = customerService.findById(id);
            if (customer != null) {
                customerService.updateCustomerStatus(id, true);
                redirectAttributes.addFlashAttribute("message", "Customer unlocked successfully!");
                redirectAttributes.addFlashAttribute("alertClass", "alert-success");
            } else {
                redirectAttributes.addFlashAttribute("message", "Failed to unlock customer.");
                redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Failed to unlock customer.");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        }
        return "redirect:/admin/managercustomer";
    }

    // Product Management
    @GetMapping("/managerproduct")
    public String showProducts(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "admin/product";
    }

    @GetMapping("/addproduct")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin/addProduct";
    }

    @PostMapping("/saveproduct")
    public String saveProduct(@Valid @ModelAttribute("product") Product product,
                              BindingResult bindingResult,
                              @RequestParam("imageFile") MultipartFile imageFile,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findAll());
            return "admin/addProduct";
        }

        if (!imageFile.isEmpty()) {
            String fileName = imageFile.getOriginalFilename();
            String uploadDir = "src/main/resources/static/uploads/";
            Path uploadPath = Paths.get(uploadDir);

            try {
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                Path filePath = uploadPath.resolve(fileName);
                int counter = 1;
                while (Files.exists(filePath)) {
                    String newFileName = fileName.substring(0, fileName.lastIndexOf('.')) + "_" + counter + fileName.substring(fileName.lastIndexOf('.'));
                    filePath = uploadPath.resolve(newFileName);
                    counter++;
                }

                Files.copy(imageFile.getInputStream(), filePath);
                product.setImage(filePath.getFileName().toString());
            } catch (IOException e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("error", "Could not save image file: " + e.getMessage());
                return "redirect:/admin/addproduct";
            }
        }

        try {
            productRepository.save(product);
            redirectAttributes.addFlashAttribute("message", "Product added successfully!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to add product. Please try again: " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            return "redirect:/admin/addproduct";
        }

        return "redirect:/admin/managerproduct";
    }
    
    @GetMapping("editproduct/{id}")
    public String editProductForm(@PathVariable("id") Integer id, Model model) {
        Product product = productRepository.findById(id).orElse(null);
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin/editProduct";
    }

    @PostMapping("updateproduct/{id}")
    public String updateProduct(@PathVariable("id") Integer id, 
                                @ModelAttribute("product") Product product, 
                                @RequestParam("imageFile") MultipartFile file, 
                                RedirectAttributes redirectAttributes,
                                Model model) {
        try {
            if (!file.isEmpty()) {
                String uploadDir = "src/main/resources/static/uploads/";

                // Kiểm tra và tạo thư mục nếu chưa tồn tại
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Lưu tệp
                Path filePath = uploadPath.resolve(file.getOriginalFilename());
                Files.copy(file.getInputStream(), filePath);

                product.setImage(file.getOriginalFilename());
            } else {
                // Giữ nguyên giá trị ảnh cũ nếu không có tệp tin mới được tải lên
                Product existingProduct = productRepository.findById(id).orElse(null);
                if (existingProduct != null) {
                    product.setImage(existingProduct.getImage());
                }
            }

            product.setProduct_id(id);
            productRepository.save(product);
            redirectAttributes.addFlashAttribute("message", "Product updated successfully!");
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to update product. Please try again.");
            return "redirect:/admin/editproduct/" + id;
        }
        return "redirect:/admin/managerproduct";
    }

    @GetMapping("deleteproduct/{id}")
    public String deleteProduct(@PathVariable("id") Integer id) {
        productRepository.deleteById(id);
        return "redirect:/admin/managerproduct";
    }
    
    @GetMapping("/managercategory")
    public String showCategories(Model model) {
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        return "admin/category";
    }

    @GetMapping("/addcategory")
    public String showAddCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        return "admin/addCategory";
    }

    @PostMapping("/savecategory")
    public String saveCategory(Category category) {
        categoryService.saveCategory(category);
        return "redirect:/admin/managercategory";
    }

    @GetMapping("/editcategory/{id}")
    public String showEditCategoryForm(@PathVariable("id") Integer id, Model model) {
        Category category = categoryService.getCategoryById(id);
        model.addAttribute("category", category);
        return "admin/editCategory";
    }

    @GetMapping("/deletecategory/{id}")
    public String deleteCategory(@PathVariable("id") Integer id) {
        categoryService.deleteCategory(id);
        return "redirect:/admin/managercategory";
    }
    
    
    @GetMapping("managerstatis")
    public String showStatic(Model model, @RequestParam(name = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
        @RequestParam(name = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,@RequestParam("page") Optional<Integer>p ) {
    	Integer pe = p.orElse(sessionService.getAttribute("pageCate"));
		sessionService.setAttribute("pageCate", pe);
		if (pe == null) {
			pe = 0;
		}
		Pageable pageable = PageRequest.of(pe, 5);
		Page<Object[]> total = orderRepository.findTotal(pageable);
		 model.addAttribute("total", total);
		 Double totalpro= orderRepository.findTotalRevenue();
		 model.addAttribute("totalpro",totalpro);
		 List<Object[]> orderDetails;
		    
		    if (startDate != null && endDate != null) {
		        orderDetails = orderRepository.findOrderDetailsByDateRange(startDate, endDate);
		    } else {
		        orderDetails = orderRepository.findOrderDetails();
		    }


		    model.addAttribute("orderDetails", orderDetails);
		    
		    // Check if orderDetails is empty to determine if there are no results
		    model.addAttribute("noResults", orderDetails.isEmpty());

    	return "admin/managerstatis";
    }
}
