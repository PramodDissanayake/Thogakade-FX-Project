package org.example.controller.customer;

import javafx.collections.ObservableList;
import org.example.model.Customer;

public interface CustomerService {
    Customer searchCustomer(String customerId);
    ObservableList<Customer> getAllCustomers();
    boolean addCustomer(Customer customer);
}
