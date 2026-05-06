package com.auca.cafeteria.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "menu_items")
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Item name is required")
    @Column(nullable = false)
    private String name;

    private String description;

    @Positive(message = "Price must be positive")
    @Column(nullable = false)
    private Double price;

    private String category;

    @Column(nullable = false)
    private Boolean available = true;

    private String imageUrl;
}