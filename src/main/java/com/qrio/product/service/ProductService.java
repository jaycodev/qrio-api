package com.qrio.product.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qrio.category.model.Category;
import com.qrio.category.repository.CategoryRepository;
import com.qrio.branch.model.Branch;
import com.qrio.branch.repository.BranchRepository;
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
    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    private final ProductBranchAvailabilityRepository productBranchAvailabilityRepository;
    private final CategoryRepository categoryRepository;
    private final BranchRepository branchRepository;

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
        log.info("[ProductService] create called with branchId={}, categoryId={}, name={}",
            branchId, request.categoryId(), request.name());
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
        log.info("[ProductService] product saved id={}, computing availability for branchId={}",
            saved.getId(), branchId);

        // Crear disponibilidad por sucursal si se envió branchId
        if (branchId != null) {
            Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));
            com.qrio.product.model.ProductBranchAvailability pba = new com.qrio.product.model.ProductBranchAvailability();
            pba.setId(new com.qrio.product.model.ProductBranchAvailabilityId(saved.getId(), branchId));
            pba.setProduct(saved);
            pba.setBranch(branch);
            pba.setAvailable(true);
            productBranchAvailabilityRepository.save(pba);
            log.info("[ProductService] ProductBranchAvailability created for productId={}, branchId={} (available=true)",
                saved.getId(), branchId);
        }
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

    @Transactional
    public Boolean setAvailability(Long productId, Long branchId, boolean available) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        Branch branch = branchRepository.findById(branchId)
            .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));

        var id = new com.qrio.product.model.ProductBranchAvailabilityId(productId, branchId);
        var pba = productBranchAvailabilityRepository.findById(id)
            .orElseGet(() -> {
                var np = new com.qrio.product.model.ProductBranchAvailability();
                np.setId(id);
                np.setProduct(product);
                np.setBranch(branch);
                return np;
            });
        pba.setAvailable(available);
        productBranchAvailabilityRepository.save(pba);
        log.info("[ProductService] setAvailability productId={}, branchId={}, available={}", productId, branchId, available);
        return available;
    }

    private ProductListResponse toListResponse(Product product, Long branchId) {
        log.info("[ProductService] toListResponse productId={}, branchId={}", product.getId(), branchId);
        Boolean available;
        if (branchId != null) {
            java.util.Optional<Boolean> availOpt = productBranchAvailabilityRepository
                .findAvailabilityByProductAndBranch(product.getId(), branchId);
            available = availOpt.orElse(false);
            log.info("[ProductService] availability lookup result={}, available={}", availOpt.orElse(null), available);
        } else {
            available = false;
            log.info("[ProductService] branchId is null, default available={}", available);
        }

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

    @Transactional
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found");
        }
        productRepository.deleteById(id);
    }
}