package com.asm.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.asm.model.Customer;
import com.asm.repository.CustomerRepository;
import com.asm.repository.CustomerRepositorydk;
import com.asm.service.CookieService;
import com.asm.service.SessionService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("auth")
public class AuthController {
	@Autowired
	CustomerRepository customerRepository;
	@Autowired
	CustomerRepositorydk customerRepositorydk;
	@Autowired
	SessionService sessionService;
	@Autowired
	CookieService cookieService;
	@GetMapping("/login")
	public String login() {
		return "login";
	}
	
	
	@PostMapping("login")
	public String login(Model model, @RequestParam("email") String email, @RequestParam("password") String password,
			@RequestParam(value = "remember", required = false) Boolean remember) {
		Customer customer = customerRepository.findByEmail(email);
		if (customer != null) {
			if (!customer.getEnabled()) {
				model.addAttribute("message",
						"Tài khoản của bạn đã bị khóa. Vui lòng liên hệ quản trị viên để biết thêm chi tiết.");
				return "login";
			}
			if (customer.getPassword().equals(password)) {
				if (customer.isAdmin()) {
					sessionService.setAttribute("customer", customer);
					sessionService.setAttribute("customer_id", customer.getCustomer_id());
					if (remember != null && remember) {
						cookieService.create("email", email, 30);
						return "redirect:/admin/index";
					} else {
						cookieService.delete("email");
						return "redirect:/admin/index";
					}
				} else {
					sessionService.setAttribute("customer", customer);
					sessionService.setAttribute("customer_id", customer.getCustomer_id());
					if (remember != null && remember) {
						cookieService.create("email", email, 30);
						return "redirect:/home";
					} else {
						cookieService.delete("email");
						return "redirect:/home";
					}
				}
			} else {
				model.addAttribute("message", "Mật khẩu không đúng!");
			}
		} else {
			model.addAttribute("message", "Email chưa đăng ký!");
		}
		return "login";
	}
	
	
	@GetMapping("/forgotpassword")
	public String forgotPassword() {
		return "user/forgotpasword";
	}
	
	@GetMapping("/register")
	public String register() {
		return "user/register";
	}
	
	@GetMapping("/changepassword")
	public String changePassword(Model model, HttpSession session) {
	    Customer customer = (Customer) session.getAttribute("customer");
	    if (customer == null) {
	        return "redirect:/auth/login";  // Redirect to login if not logged in
	    }
	    model.addAttribute("customer", customer);
	    return "user/changepassword";  // Return the change password form view
	}

	@PostMapping("/changepassword")
	public String changePasswordSubmit(Model model, HttpSession session,
	                                    @RequestParam("oldPassword") String oldPassword,
	                                    @RequestParam("newPassword") String newPassword,
	                                    @RequestParam("confirmPassword") String confirmPassword) {
	    Customer customer = (Customer) session.getAttribute("customer");
	    if (customer == null) {
	        return "redirect:/auth/login";  // Redirect to login if not logged in
	    }

	    // Validation checks
	    if (!customer.getPassword().equals(oldPassword)) {
	        model.addAttribute("message1", "Mật khẩu cũ không chính xác!");
	        return "user/changepassword"; // Redirect back to form with error message
	    }

	    if (newPassword.isEmpty() || newPassword.length() < 8) { // Check for empty or weak password
	        model.addAttribute("message2", "Mật khẩu mới phải dài ít nhất 8 ký tự!");
	        return "user/changepassword"; // Redirect back to form with error message
	    }

	    if (!newPassword.equals(confirmPassword)) {
	        model.addAttribute("message3", "Mật khẩu mới không khớp!");
	        return "user/changepassword"; // Redirect back to form with error message
	    }

	    // Update password in database
	    customer.setPassword(newPassword);
	    customerRepository.saveAndFlush(customer);

	    model.addAttribute("message4", "Đổi mật khẩu thành công!");
	    return "user/changepassword";  // Redirect back to change password form with success message
	}

	@GetMapping("/updateprofile")
    public String updateProfile(Model model) {
		List<Customer> customers = customerRepository.findAll();
		model.addAttribute("customers",customers);
        return "user/updateprofile";
    }
	
	@PostMapping("/updateprofile")
    public String updateprofileSubmit(Model model, HttpSession session,
                                       @RequestParam("name") String name,
                                       @RequestParam("email") String email,
                                       @RequestParam("phone") String phone,
                                       @RequestParam("address") String address) {
        Customer customer = (Customer) session.getAttribute("customer");

        if (customer == null) {
            model.addAttribute("message", "Vui lòng đăng nhập lại.");
            return "user/updateprofile";
        }

        boolean hasErrors = false; // Flag for tracking validation errors

        // Validate email format
        if (!email.matches("^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*\\.[a-zA-Z]{2,}$")) {
            model.addAttribute("message", "Email không đúng định dạng!");
            hasErrors = true;
        }

        // Validate phone number format
        if (!phone.matches("\\d{10}")) {
            model.addAttribute("message", "Số điện thoại phải là 10 chữ số và không chứa ký tự chữ!");
            return "user/updateprofile";
        }

        // Check for existing email (excluding current user)
        Customer existingCustomerByEmail = customerRepository.findByEmail(email);
        if (existingCustomerByEmail != null && !existingCustomerByEmail.getCustomer_id().equals(customer.getCustomer_id())) {
            model.addAttribute("message", "Email đã tồn tại!");
            hasErrors = true;
        }

        // Check for existing phone number (excluding current user)
        Customer existingCustomerByPhone = customerRepository.findByPhone(phone);
        if (existingCustomerByPhone != null && !existingCustomerByPhone.getCustomer_id().equals(customer.getCustomer_id())) {
            model.addAttribute("message", "Số điện thoại đã tồn tại!");
            hasErrors = true;
        }

        // Update customer details only if no validation errors occurred
        if (!hasErrors) {
            try {
                customer.setName(name);
                customer.setPhone(phone);
                customer.setAddress(address);
                customer.setEmail(email);
                customerRepository.saveAndFlush(customer);
                model.addAttribute("message", "Cập nhật thành công");
            } catch (DataIntegrityViolationException e) {
                model.addAttribute("message", "Email hoặc số điện thoại đã tồn tại!");
            } catch (Exception e) {
                model.addAttribute("message", "Lỗi cập nhật");
                e.printStackTrace(); // Log the exception for debugging
            }
        }
        return "user/updateprofile";
    }
	
	@GetMapping("/logout")
	public String logout(HttpSession session) {
	    session.invalidate();
	    return "redirect:/home";
	}
}
