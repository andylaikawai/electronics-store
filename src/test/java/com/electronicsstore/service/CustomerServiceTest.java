package com.electronicsstore.service;

import com.electronicsstore.model.Customer;
import com.electronicsstore.repository.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class CustomerServiceIntegrationTest {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    public void testCreateAndGetCustomer() {
        String name = "Andy Lai";
        Customer createdCustomer = customerService.createCustomer(name);

        assertThat(createdCustomer).isNotNull();
        assertThat(createdCustomer.getCustomerId()).isNotNull();
        assertThat(createdCustomer.getName()).isEqualTo(name);

        Customer foundCustomer = customerService.getCustomerById(createdCustomer.getCustomerId());
        assertThat(foundCustomer).isNotNull();
        assertThat(foundCustomer.getCustomerId()).isEqualTo(createdCustomer.getCustomerId());
        assertThat(foundCustomer.getName()).isEqualTo(name);
    }

    @Test
    public void testDeleteCustomer() {
        Customer customer = Customer.builder().name("Andy Lai").build();
        Customer savedCustomer = customerRepository.save(customer);

        assertThat(customerRepository.existsById(savedCustomer.getCustomerId())).isTrue();

        customerService.deleteCustomer(savedCustomer.getCustomerId());

        assertThat(customerRepository.existsById(savedCustomer.getCustomerId())).isFalse();
    }

    @Test
    public void testGetCustomerByIdNotFound() {
        Long invalidId = 999L;
        assertThatThrownBy(() -> customerService.getCustomerById(invalidId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Customer not found with id: " + invalidId);
    }
}