package com.qrio.dishes.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.qrio.dishes.model.Dish;

public interface DishRepository extends JpaRepository<Dish, Long> {

    // Obtener todos los platos de una categoría específica
    List<Dish> findByCategoryId(Long categoryId);

    
    // Obtener platos disponibles de una categoría específica
    List<Dish> findByCategoryIdAndAvailableTrue(Long categoryId);

    
    // Verificar si ya existe un plato con el mismo nombre en una categoría
    boolean existsByCategoryIdAndNameIgnoreCase(Long categoryId, String name);

    
    // Encontrar plato por nombre y categoría (útil para validación de actualización)
    Optional<Dish> findByCategoryIdAndNameIgnoreCase(Long categoryId, String name);
}