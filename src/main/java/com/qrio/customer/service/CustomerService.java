package com.qrio.customer.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qrio.customer.dto.request.CreateCustomerRequest;
import com.qrio.customer.dto.request.UpdateCustomerRequest;

import com.qrio.customer.dto.response.CustomerResponse;
import com.qrio.customer.model.Customer;
import com.qrio.customer.model.type.CustomerStatus;
import com.qrio.customer.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Transactional
    public CustomerResponse createCustomer(CreateCustomerRequest request) {
        if (customerRepository.existsByFirebaseUid(request.uidFirebase())) {
            throw new IllegalArgumentException("Customer with this Firebase UID already exists");
        }

        if (customerRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Email is already in use");
        }

        Customer customer = new Customer();
        customer.setUidFirebase(request.uidFirebase());
        customer.setName(request.name());
        customer.setEmail(request.email());
        customer.setPhone(request.phone());
        customer.setStatus(request.status() != null ? request.status() : CustomerStatus.ACTIVE);
        customer.setCreatedAt(LocalDateTime.now());

        Customer saved = customerRepository.save(customer);
        return mapToResponse(saved);
    }

    @Transactional
    public CustomerResponse updateCustomer(Long id, UpdateCustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with id: " + id));

        customerRepository.findByEmail(request.email())
                .filter(c -> !c.getId().equals(id))
                .ifPresent(c -> {
                    throw new IllegalArgumentException("Email is already in use by another customer");
                });

        customer.setUidFirebase(request.uidFirebase());
        customer.setName(request.name());
        customer.setEmail(request.email());
        customer.setPhone(request.phone());
        customer.setStatus(request.status() != null ? request.status() : customer.getStatus());

        Customer updated = customerRepository.save(customer);
        return mapToResponse(updated);
    }

    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with id: " + id));
        return mapToResponse(customer);
    }

     @Transactional(readOnly = true)
     public CustomerResponse getCustomerByFirebaseUid(String firebaseUid) {
         Customer customer = customerRepository.findByFirebaseUid(firebaseUid) // <--- ¡CAMBIADO AQUÍ!
                 .orElseThrow(
                         () -> new IllegalArgumentException("Customer not found with Firebase UID: " + firebaseUid));
         return mapToResponse(customer);
     }

    @Transactional(readOnly = true)
    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public CustomerResponse deactivateCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with id: " + id));

        customer.setStatus(CustomerStatus.INACTIVE);
        Customer updated = customerRepository.save(customer);
        return mapToResponse(updated);
    }

    private CustomerResponse mapToResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getUidFirebase(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getStatus(),
                customer.getCreatedAt());
    }
}
