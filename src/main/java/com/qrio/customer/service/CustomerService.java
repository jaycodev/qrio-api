package com.qrio.customer.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.qrio.auth.dto.mobile.FirebaseAuthResponse;
import com.qrio.customer.dto.request.CreateCustomerRequest;
import com.qrio.customer.dto.request.UpdateCustomerRequest;
import com.qrio.customer.dto.response.CustomerDetailResponse;
import com.qrio.customer.dto.response.CustomerListResponse;
import com.qrio.customer.model.Customer;
import com.qrio.customer.repository.CustomerRepository;
import com.qrio.shared.exception.ResourceNotFoundException;
import com.qrio.shared.type.Status;

import lombok.RequiredArgsConstructor;

import java.util.Map;

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
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        customer.setName(request.name());
        customer.setPhone(request.phone());

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

    @Transactional(readOnly = true)
    public Optional<Customer> findByFirebaseUid(String firebaseUid) {
        return customerRepository.findByFirebaseUid(firebaseUid);
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

    @Transactional
    public FirebaseAuthResponse firebaseAuth(String uid, String email) {
        Customer customer = customerRepository.findByFirebaseUid(uid).orElse(null);
        boolean isNew = false;

        if (customer == null) {
            customer = new Customer();
            customer.setFirebaseUid(uid);
            customer.setEmail(email);
            customer.setStatus(Status.ACTIVO);
            customer = customerRepository.save(customer);
            isNew = true;
        }

        return new FirebaseAuthResponse(customer, isNew);
    }

}