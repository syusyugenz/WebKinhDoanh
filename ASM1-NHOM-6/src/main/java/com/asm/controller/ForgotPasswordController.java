package com.asm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.asm.model.Customer;
import com.asm.repository.CustomerRepositorydk;
import com.asm.service.EmailService;

import jakarta.servlet.http.HttpSession;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Controller
@RequestMapping
public class ForgotPasswordController {

    @Autowired
    private CustomerRepositorydk customerRepositorydk;

    @Autowired
    private EmailService emailService;

   
    @PostMapping("/auth/forgotpassword")
    public String processForgotPasswordForm(HttpSession session, Model model, @RequestParam("email") String email) {
        Optional<Customer> customerOptional = customerRepositorydk.findByEmail(email);
        if (!customerOptional.isPresent()) {
            model.addAttribute("message", "Email không tồn tại!");
            return "user/forgotpassword";
        }

        Customer customer = customerOptional.get();
        String otp = String.valueOf(new Random().nextInt(999999));
        customer.setOtp(otp);
        customer.setOtp_expiry(LocalDateTime.now().plusMinutes(10));
        customerRepositorydk.save(customer);

        emailService.sendEmail(customer.getEmail(), "Mã OTP đặt lại mật khẩu", "Mã OTP của bạn là: " + otp);

        session.setAttribute("email", email);
        model.addAttribute("message", "Mã OTP đã được gửi đến email của bạn.");
        return "user/enter-otp";
    }
    
    @PostMapping("/auth/verify-otp")
    public String verifyOtp(HttpSession session, Model model, @RequestParam("otp") String otp) {
        String email = (String) session.getAttribute("email");
        if (email == null) {
            model.addAttribute("message", "Email không tồn tại trong session!");
            return "user/enter-otp";
        }

        Optional<Customer> customerOptional = customerRepositorydk.findByEmail(email);
        if (!customerOptional.isPresent()) {
            model.addAttribute("message", "Email không tồn tại!");
            return "user/enter-otp";
        }

        Customer customer = customerOptional.get();
        if (!otp.equals(customer.getOtp()) || customer.getOtp_expiry().isBefore(LocalDateTime.now())) {
            model.addAttribute("message", "Mã OTP không hợp lệ hoặc đã hết hạn!");
            return "user/enter-otp";
        }

        model.addAttribute("email", email);
        return "user/reset-password";
    }
    @PostMapping("/auth/reset-password")
    public String processResetPasswordForm(HttpSession session, Model model, @RequestParam("password") String password) {
        String email = (String) session.getAttribute("email");
        if (email == null) {
            model.addAttribute("message", "Email không tồn tại trong session!");
            return "user/reset-password";
        }

        Optional<Customer> customerOptional = customerRepositorydk.findByEmail(email);
        if (!customerOptional.isPresent()) {
            model.addAttribute("message", "Email không tồn tại!");
            return "user/reset-password";
        }

        Customer customer = customerOptional.get();
        if (password.length() < 8) {
            model.addAttribute("message", "Mật khẩu phải chứa ít nhất 8 ký tự!");
            model.addAttribute("email", email);
            return "user/reset-password";
        }

        customer.setPassword(password);
        customer.setOtp(null);
        customer.setOtp_expiry(null);
        customerRepositorydk.save(customer);

        model.addAttribute("message", "Mật khẩu đã được đặt lại thành công!");
        return "login";
    }




}
