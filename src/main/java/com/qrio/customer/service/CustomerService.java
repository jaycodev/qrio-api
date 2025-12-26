package com.qrio.customer.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.qrio.customer.dto.request.CreateCustomerRequest;
import com.qrio.customer.dto.request.UpdateCustomerRequest;
import com.qrio.customer.dto.response.CustomerDetailResponse;
import com.qrio.customer.dto.response.CustomerListResponse;
import com.qrio.customer.model.Customer;
import com.qrio.customer.repository.CustomerRepository;
import com.qrio.shared.exception.ResourceNotFoundException;
import com.qrio.shared.type.Status;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Validated
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Transactional(readOnly = true)
    public List<CustomerListResponse> getList() {
        return customerRepository.findList();
    }

    @Transactional(readOnly = true)
    public CustomerDetailResponse getDetailById(Long id) {
        return customerRepository.findDetailById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    }

    @Transactional
    public CustomerListResponse create(CreateCustomerRequest request) {
        if (customerRepository.existsByFirebaseUid(request.firebaseUid())) {
            throw new IllegalArgumentException("Customer with this Firebase UID already exists");
        }

        if (customerRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Email is already in use");
        }

        Customer customer = new Customer();
        customer.setFirebaseUid(request.firebaseUid());
        customer.setName(request.name());
        customer.setEmail(request.email());
        customer.setPhone(request.phone());
        customer.setStatus(request.status() != null ? request.status() : Status.ACTIVO);
        customer.setCreatedAt(LocalDateTime.now());

        Customer saved = customerRepository.save(customer);
        return toListResponse(saved);
    }

    @Transactional
    public CustomerListResponse update(Long id, UpdateCustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        customerRepository.findByEmail(request.email())
                .filter(c -> !c.getId().equals(id))
                .ifPresent(c -> {
                    throw new IllegalArgumentException("Email is already in use by another customer");
                });

        customer.setFirebaseUid(request.firebaseUid());
        customer.setName(request.name());
        customer.setEmail(request.email());
        customer.setPhone(request.phone());
        customer.setStatus(request.status() != null ? request.status() : customer.getStatus());

        Customer updated = customerRepository.save(customer);
        return toListResponse(updated);
    }

    @Transactional(readOnly = true)
    public CustomerDetailResponse getByFirebaseUid(String firebaseUid) {
        Customer customer = customerRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Customer not found with Firebase UID: " + firebaseUid));
        return new CustomerDetailResponse(
                customer.getId(),
                customer.getFirebaseUid(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getStatus(),
                customer.getCreatedAt());
    }

    private CustomerListResponse toListResponse(Customer customer) {
        return new CustomerListResponse(
                customer.getId(),
                customer.getFirebaseUid(),
                customer.getName(),
                customer.getEmail(),
                customer.getStatus(),
                customer.getCreatedAt());
    }
}