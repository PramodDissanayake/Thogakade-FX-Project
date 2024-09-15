package org.example.controller.customer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.crudUtil.CrudUtil;
import org.example.db.DBConnection;
import org.example.model.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;

public class CustomerController implements CustomerService{

    private static CustomerController instance;
    private CustomerController (){}

    public static CustomerController getInstance(){
        if (instance==null){
           return instance=new CustomerController();
        }
        return instance;
    }

    public Customer searchCustomer(String customerId){
        System.out.println(customerId);
        try {
            ResultSet resultSet  = CrudUtil.execute("SELECT * FROM customer WHERE CustID=?",customerId);

                while (resultSet.next()){
                    return new Customer(
                            resultSet.getString(1),
                            resultSet.getString(2),
                            resultSet.getString(3),
                            resultSet.getDate(4),
                            resultSet.getDouble(5),
                            resultSet.getString(6),
                            resultSet.getString(7),
                            resultSet.getString(8),
                            resultSet.getString(9)
                    );
                }


        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public ObservableList<Customer> getAllCustomers() {
        try {
            ResultSet resultSet =CrudUtil.execute("SELECT * FROM customer");
            ObservableList<Customer> listTable = FXCollections.observableArrayList();
            while (resultSet.next()) {
                listTable.add(
                        new Customer(
                                resultSet.getString("CustID"),
                                resultSet.getString("CustTitle"),
                                resultSet.getString("CustName"),
                                resultSet.getDate("DOB"),
                                resultSet.getDouble("salary"),
                                resultSet.getString("CustAddress"),
                                resultSet.getString("City"),
                                resultSet.getString("Province"),
                                resultSet.getString("PostalCode")
                        )
                );
            }
            return listTable;

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean addCustomer(Customer customer){
        try {
            String SQL = "INSERT INTO customer VALUES (?,?,?,?,?,?,?,?,?)";
            CrudUtil.execute(
                    SQL,
                    customer.getCustomerId(),
                    customer.getCustomerTitle(),
                    customer.getCustomerName(),
                    customer.getDob(),
                    customer.getSalary(),
                    customer.getAddress(),
                    customer.getCity(),
                    customer.getProvince(),
                    customer.getPostalCode()
                    );

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}
