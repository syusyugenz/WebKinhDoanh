package com.asm.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.asm.model.Customer;
import com.asm.repository.CustomerRepository;
import com.asm.repository.CustomerRepositorydk;

import jakarta.transaction.Transactional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepositorydk customerRepositorydk;
    @Autowired
    private CustomerRepository customerRepository;
    
    @Transactional
    public Customer registerCustomer(Customer customer) throws Exception {
        if (customerRepositorydk.findByEmail(customer.getEmail()).isPresent()) {
            throw new Exception("Email đã tồn tại");
        }
        if (customerRepositorydk.findByPhone(customer.getPhone()).isPresent()) {
            throw new Exception("Số điện thoại đã tồn tại");
        }
        return customerRepositorydk.save(customer);
    }
    
    @Transactional
    public void saveCustomer(Customer customer) {
        customerRepository.save(customer);
    }

    @Transactional
    public void deleteCustomer(Integer id) {
        customerRepository.deleteById(id);
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Page<Customer> getAllCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable);
    }

    public Customer findById(Integer id) {
        Optional<Customer> optionalCustomer = customerRepository.findById(id);
        return optionalCustomer.orElse(null);
    }
    
    @Transactional
    public void updateCustomerStatus(Integer id, boolean enabled) {
        Customer customer = findById(id);
        if (customer != null) {
            customer.setEnabled(enabled);
            saveCustomer(customer);
        }
    }
    
    public long countCustomers() {
        return customerRepository.count();
    }
   
    
}