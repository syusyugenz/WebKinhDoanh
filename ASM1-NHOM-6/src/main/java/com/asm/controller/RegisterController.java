package com.asm.controller;

import java.sql.Date;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.asm.model.Customer;
import com.asm.repository.CustomerRepository;
import com.asm.repository.CustomerRepositorydk;
import com.asm.repository.TemporaryCustomerStorage;
import com.asm.service.CustomerService;
import com.asm.service.EmailService;


@Controller
@RequestMapping("/auth")
@Validated
public class RegisterController {

    @Autowired
    private CustomerRepositorydk customerRepositorydk;
  
    private TemporaryCustomerStorage temporaryCustomerStorage = new TemporaryCustomerStorage(); //để lưu trữ tạm thời thông tin khách hàng

    @Autowired
    private CustomerService customerService;
    @Autowired
    private EmailService emailService;

    @RequestMapping("/auth/register")
    public String register() {
        return "user/register";
    }

    
    @PostMapping("/register")//xử lý các yêu cầu POST đến URL /register.
    public String register(Model model, @Valid  @ModelAttribute("customer") Customer customer, BindingResult bindingResult) { // xử lý việc đăng ký khách hàng mới.
        if (bindingResult.hasErrors()) {
            model.addAttribute("message", "Vui lòng điền đầy đủ thông tin!");
            return "user/register";
        }

        Optional<Customer> existingCustomerByEmail = customerRepositorydk.findByEmail(customer.getEmail());
        if (existingCustomerByEmail.isPresent()) {
            model.addAttribute("message", "Email đã tồn tại!");
            return "user/register";
        }

        Optional<Customer> existingCustomerByPhone = customerRepositorydk.findByPhone(customer.getPhone());
        if (existingCustomerByPhone.isPresent()) {
            model.addAttribute("message", "Số điện thoại đã tồn tại!");
            return "user/register";
        } else if (!customer.getPhone().matches("\\d{10}")) {
            model.addAttribute("message", "Số điện thoại phải chứa đúng 10 chữ số!");
            return "user/register";
        } if (customer.getPassword().length() < 8) {
            model.addAttribute("message", "Mật khẩu phải chứa ít nhất 8 ký tự!");
            return "user/register";
        }

        // Generate OTP
        String otp = generateOTP();
        customer.setOtp(otp);
        customer.setEnabled(false);

        try {
            temporaryCustomerStorage.addCustomer(customer);
            emailService.sendEmail(customer.getEmail(), "Xác nhận đăng ký", "Mã OTP của bạn là: " + otp);

            model.addAttribute("message", "Đăng kí thành công! Vui lòng kiểm tra email để xác nhận đăng ký.");
        } catch (Exception e) {
            model.addAttribute("message", "Lỗi đăng kí!");
        }

        return "user/verify";
    }

    @GetMapping("/verify")
    public String verifyAccount(@RequestParam("otp") String otp, Model model) {
        Customer customer = temporaryCustomerStorage.getCustomerByOtp(otp);
        if (customer == null) {
            model.addAttribute("message", "Mã OTP không hợp lệ!");
            return "user/verify";
        }

        customer.setEnabled(true);
        customer.setOtp(null);
        customerRepositorydk.save(customer);
        temporaryCustomerStorage.removeCustomerByOtp(otp);

        model.addAttribute("message", "Xác nhận đăng ký thành công! Bạn có thể đăng nhập.");
        return "login";
    }

    private String generateOTP() {
        Random random = new Random();
        int otpValue = 100000 + random.nextInt(900000);
        return String.valueOf(otpValue);
    }



    

   
}