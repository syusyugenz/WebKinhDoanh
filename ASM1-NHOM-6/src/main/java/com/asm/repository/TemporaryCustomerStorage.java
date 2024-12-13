package com.asm.repository;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.asm.model.Customer;
public class TemporaryCustomerStorage {
//    private Map<String, Customer> temporaryCustomers = new HashMap<>();
//
//    public String addCustomer(Customer customer) {
//        String token = UUID.randomUUID().toString();
//        temporaryCustomers.put(token, customer);
//        return token;
//    }
//
//    public Customer getCustomer(String token) {
//        return temporaryCustomers.get(token);
//    }
//
//    public void removeCustomer(String token) {
//        temporaryCustomers.remove(token);
//    }
	 private Map<String, Customer> customerStorage = new HashMap<>();
	    private Map<String, Customer> otpStorage = new HashMap<>();

	    public String addCustomer(Customer customer) {
	        String token = UUID.randomUUID().toString();
	        customerStorage.put(token, customer);
	        otpStorage.put(customer.getOtp(), customer);
	        return token;
	    }

	    public Customer getCustomer(String token) {
	        return customerStorage.get(token);
	    }

	    public Customer getCustomerByOtp(String otp) {
	        return otpStorage.get(otp);
	    }

	    public void removeCustomer(String token) {
	        Customer customer = customerStorage.remove(token);
	        if (customer != null) {
	            otpStorage.remove(customer.getOtp());
	        }
	    }

	    public void removeCustomerByOtp(String otp) {
	        Customer customer = otpStorage.remove(otp);
	        if (customer != null) {
	            customerStorage.values().remove(customer);
	        }
	    }
}