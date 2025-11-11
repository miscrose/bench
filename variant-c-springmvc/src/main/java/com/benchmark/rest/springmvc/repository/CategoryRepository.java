package com.benchmark.rest.springmvc.repository;

import com.benchmark.rest.springmvc.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}

