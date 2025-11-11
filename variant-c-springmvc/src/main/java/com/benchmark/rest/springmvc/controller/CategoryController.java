package com.benchmark.rest.springmvc.controller;

import com.benchmark.rest.springmvc.dto.ItemDTO;
import com.benchmark.rest.springmvc.model.Category;
import com.benchmark.rest.springmvc.model.Item;
import com.benchmark.rest.springmvc.repository.CategoryRepository;
import com.benchmark.rest.springmvc.repository.ItemRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryRepository repository;
    private final ItemRepository itemRepository;

    public CategoryController(CategoryRepository repository, ItemRepository itemRepository) {
        this.repository = repository;
        this.itemRepository = itemRepository;
    }

    @GetMapping
    public Page<Category> getAllCategories(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody @Valid Category category) {
        Category saved = repository.save(category);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(
            @PathVariable Long id,
            @RequestBody @Valid Category category) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setName(category.getName());
                    existing.setCode(category.getCode());
                    Category updated = repository.save(existing);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/items")
    public Page<ItemDTO> getCategoryItems(
            @PathVariable Long id,
            Pageable pageable) {
        
        if (!repository.existsById(id)) {
            return Page.empty();
        }
        
        List<Item> items = itemRepository.findByCategoryIdJoinFetch(id);
        List<ItemDTO> dtos = items.stream()
                .map(item -> new ItemDTO(
                        item.getId(),
                        item.getSku(),
                        item.getName(),
                        item.getPrice(),
                        item.getStock(),
                        item.getCategory().getCode()
                ))
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, items.size());
    }
}

