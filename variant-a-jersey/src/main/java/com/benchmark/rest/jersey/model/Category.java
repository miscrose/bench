package com.benchmark.rest.jersey.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Category {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", length = 32, nullable = false, unique = true)
    private String code;

    @Column(name = "name", length = 128, nullable = false)
    private String name;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @JsonIgnore
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Item> items = new ArrayList<>();

    @PrePersist
    @PreUpdate
    protected void updateTimestamp() {
        this.updatedAt = Instant.now();
    }
}

