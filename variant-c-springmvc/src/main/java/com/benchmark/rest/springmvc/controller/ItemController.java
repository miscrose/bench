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
@RequestMapping("/items")
public class ItemController {

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;

    public ItemController(ItemRepository itemRepository, CategoryRepository categoryRepository) {
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public Page<ItemDTO> getItems(
            @RequestParam(required = false) Long categoryId,
            Pageable pageable) {
        
        Page<Item> itemPage;

        if (categoryId != null) {
            List<Item> items = itemRepository.findByCategoryIdJoinFetch(categoryId);
            itemPage = new PageImpl<>(items, pageable, items.size());
        } else {
            itemPage = itemRepository.findAllWithCategory(pageable);
        }

        return itemPage.map(item -> new ItemDTO(
                item.getId(),
                item.getSku(),
                item.getName(),
                item.getPrice(),
                item.getStock(),
                item.getCategory().getCode()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDTO> getItemById(@PathVariable Long id) {
        return itemRepository.findById(id)
                .map(item -> new ItemDTO(
                        item.getId(),
                        item.getSku(),
                        item.getName(),
                        item.getPrice(),
                        item.getStock(),
                        item.getCategory().getCode()
                ))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ItemDTO> createItem(@RequestBody @Valid Item item) {
        if (item.getCategory() == null || item.getCategory().getId() == null) {
            return ResponseEntity.badRequest().build();
        }

        Category category = categoryRepository.findById(item.getCategory().getId())
                .orElse(null);
        if (category == null) {
            return ResponseEntity.badRequest().build();
        }

        item.setCategory(category);
        Item saved = itemRepository.save(item);
        ItemDTO response = new ItemDTO(
                saved.getId(),
                saved.getSku(),
                saved.getName(),
                saved.getPrice(),
                saved.getStock(),
                saved.getCategory().getCode()
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemDTO> updateItem(
            @PathVariable Long id,
            @RequestBody @Valid Item item) {
        return itemRepository.findById(id)
                .map(existing -> {
                    existing.setName(item.getName());
                    existing.setPrice(item.getPrice());
                    existing.setStock(item.getStock());
                    existing.setSku(item.getSku());
                    
                    if (item.getCategory() != null && item.getCategory().getId() != null) {
                        categoryRepository.findById(item.getCategory().getId())
                                .ifPresent(existing::setCategory);
                    }
                    
                    Item updated = itemRepository.save(existing);
                    ItemDTO response = new ItemDTO(
                            updated.getId(),
                            updated.getSku(),
                            updated.getName(),
                            updated.getPrice(),
                            updated.getStock(),
                            updated.getCategory().getCode()
                    );
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        if (!itemRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        itemRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

