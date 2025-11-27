package com.qrio.dishes.model;

import java.math.BigDecimal;

import com.qrio.categories.model.Categories; 
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Data
@Table(name = "dishes")
public class Dish {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; 
	
	@ManyToOne(fetch = FetchType.LAZY)
 	@JoinColumn(name = "category_id", nullable = false)
	private Categories category; 
	
    @Column(length = 150, nullable = false) 
	private String name;
	
    @Column(columnDefinition = "TEXT")
	private String description;
	
    @Column(precision = 10, scale = 2, nullable = false)
	private BigDecimal price;

    @Column(columnDefinition = "TEXT")
	private String imageUrl; 
	
	private Boolean available = true;
	
}