package com.asm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asm.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer>{
}
