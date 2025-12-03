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
import com.qrio.product.repository.ProductBranchAvailabilityRepository;
import com.qrio.product.repository.ProductRepository;
import com.qrio.shared.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Validated
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductBranchAvailabilityRepository productBranchAvailabilityRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<ProductListResponse> getList(Long branchId) {
        return productRepository.findList(branchId);
    }

    @Transactional(readOnly = true)
    public ProductDetailResponse getDetailById(Long id) {
        return productRepository.findDetailById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    @Transactional
    public ProductListResponse create(CreateProductRequest request, Long branchId) {
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

        Product saved = productRepository.save(product);
        return toListResponse(saved, branchId);
    }

    @Transactional
    public ProductListResponse update(Long id, UpdateProductRequest request, Long branchId) {
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

        Product updated = productRepository.save(product);
        return toListResponse(updated, branchId);
    }

    private ProductListResponse toListResponse(Product product, Long branchId) {
        boolean available = branchId != null
                ? productBranchAvailabilityRepository
                        .findAvailabilityByProductAndBranch(product.getId(), branchId)
                        .orElse(false)
                : false;

        return new ProductListResponse(
                product.getId(),
                product.getImageUrl(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),

                product.getCategory().getId(),
                product.getCategory().getName(),

                available);
    }
}