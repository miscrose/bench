package com.benchmark.rest.datarest.repository;

import com.benchmark.rest.datarest.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "items", path = "items")
public interface ItemRepository extends JpaRepository<Item, Long> {

    Page<Item> findByCategoryId(Long categoryId, Pageable pageable);
}

