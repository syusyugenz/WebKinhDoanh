package com.asm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asm.model.Customer;

public interface CustomerRepositorydk extends JpaRepository<Customer, Integer> {
    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByPhone(String phone);
}