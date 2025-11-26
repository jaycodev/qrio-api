package com.qrio.categories.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.qrio.categories.model.Categories;

public interface CategoriesRepository extends JpaRepository<Categories, Long> {

    // Obtener todas las categorías para un restaurante específico
    List<Categories> findByRestaurantId(Long restaurantId);
    

    // Verificar si ya existe una categoría con el mismo nombre en un restaurante específico  
    boolean existsByRestaurantIdAndNameIgnoreCase(Long restaurantId, String name);

    
    // Encontrar una categoría por nombre y restaurante (útil para validación de actualización)    
    Optional<Categories> findByRestaurantIdAndNameIgnoreCase(Long restaurantId, String name);
}