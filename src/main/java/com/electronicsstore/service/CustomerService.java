package com.electronicsstore.service;

import com.electronicsstore.model.Customer;
import com.electronicsstore.repository.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class CustomerService {

    private final CustomerRepository CustomerRepository;

    @Autowired
    public CustomerService(CustomerRepository CustomerRepository) {
        this.CustomerRepository = CustomerRepository;
    }

    @Transactional(readOnly = true)
    public Customer getCustomerById(Long id) {
        return CustomerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + id));
    }

    @Transactional
    public Customer createCustomer(String name) {
        Customer customer = Customer.builder()
                .name(name)
                .build();
        return CustomerRepository.save(customer);
    }

    @Transactional
    public void deleteCustomer(Long id) {
        CustomerRepository.deleteById(id);
    }
}
