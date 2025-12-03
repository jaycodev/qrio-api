package com.qrio.product.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.qrio.category.model.Category;
import com.qrio.category.repository.CategoryRepository;
import com.qrio.product.dto.request.CreateProductRequest;
import com.qrio.product.dto.request.UpdateProductRequest;
import com.qrio.product.dto.response.ProductDetailResponse;
import com.qrio.product.dto.response.ProductListResponse;
import com.qrio.product.model.Product;
import com.qrio.product.repository.ProductRepository;
import com.qrio.shared.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Validated
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<ProductListResponse> getList(Long categoryId) {
        return productRepository.findList(categoryId);
    }

    @Transactional(readOnly = true)
    public ProductDetailResponse getDetailById(Long id) {
        return productRepository.findDetailById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    @Transactional
    public ProductListResponse create(CreateProductRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (productRepository.existsByCategoryIdAndNameIgnoreCase(request.categoryId(), request.name())) {
            throw new IllegalArgumentException("Product name already exists in this category");
        }

        Product product = new Product();
        product.setCategory(category);
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setImageUrl(request.imageUrl());
        product.setAvailable(request.available() != null ? request.available() : true);

        Product saved = productRepository.save(product);
        return toListResponse(saved);
    }

    @Transactional
    public ProductListResponse update(Long id, UpdateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Category newCategory = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        boolean nameExistsForOther = productRepository
                .findByCategoryIdAndNameIgnoreCase(request.categoryId(), request.name())
                .filter(d -> !d.getId().equals(id))
                .isPresent();

        if (nameExistsForOther) {
            throw new IllegalArgumentException("Product name already exists in the target category");
        }

        product.setCategory(newCategory);
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setImageUrl(request.imageUrl());
        product.setAvailable(request.available());

        Product updated = productRepository.save(product);
        return toListResponse(updated);
    }

    private ProductListResponse toListResponse(Product product) {
        return new ProductListResponse(
                product.getId(),
                product.getCategory().getId(),
                product.getName(),
                product.getPrice(),
                product.getImageUrl(),
                product.getAvailable());
    }
}