package com.benchmark.rest.jersey.repository;

import com.benchmark.rest.jersey.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    Page<Item> findByCategoryId(Long categoryId, Pageable pageable);

    @Query("SELECT i FROM Item i JOIN FETCH i.category c WHERE c.id = :categoryId")
    List<Item> findByCategoryIdJoinFetch(@Param("categoryId") Long categoryId);

    @Query(
        value = "SELECT i FROM Item i JOIN FETCH i.category",
        countQuery = "SELECT COUNT(i) FROM Item i"
    )
    Page<Item> findAllWithCategory(Pageable pageable);
}

