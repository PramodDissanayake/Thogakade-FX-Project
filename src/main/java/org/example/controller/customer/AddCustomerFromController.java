package org.example.controller.customer;

import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.db.DBConnection;
import org.example.model.Customer;
import org.example.model.tm.Table01;
import org.example.model.tm.Table02;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.ResourceBundle;

public class AddCustomerFromController implements Initializable {
    public JFXTextField txtCustomerId;
    public ChoiceBox cmbCustomerTitle;
    public JFXTextField txtCustomerName;
    public DatePicker dateDob;
    public JFXTextField txtSalary,txtAddress;
 
    public JFXTextField txtCity;
    public JFXTextField txtProvince;
    public JFXTextField txtPostalCode;
    public TableView tblCustomer1;
    public TableColumn colCustomerId;
    public TableColumn colCustomerTitle;
    public TableColumn colCustomerName;
    public TableColumn colDob;
    public TableView tblCustomer2;
    public TableColumn colCustomerIdTbl2;
    public TableColumn colSalary;
    public TableColumn colAddress;
    public TableColumn colCity;
    public TableColumn colProvince;
    public TableColumn colPostalCode;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        colCustomerIdTbl2.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        colCustomerTitle.setCellValueFactory(new PropertyValueFactory<>("customerTitle"));
        colCustomerName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colDob.setCellValueFactory(new PropertyValueFactory<>("dob"));
        colSalary.setCellValueFactory(new PropertyValueFactory<>("salary"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colCity.setCellValueFactory(new PropertyValueFactory<>("city"));
        colProvince.setCellValueFactory(new PropertyValueFactory<>("province"));
        colPostalCode.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        loadInitialValues();
        loadTable01();
        loadTable02();

        
    }

    private void loadTable01() {
        ObservableList<Table01> tbl01 = FXCollections.observableArrayList();
        ObservableList<Customer> allCustomers = CustomerController.getInstance().getAllCustomers();

        allCustomers.forEach(customer -> {
            tbl01.add(
                    new Table01(
                            customer.getCustomerId(),
                            customer.getCustomerTitle(),
                            customer.getCustomerName(),
                            customer.getDob()
                    )
            );
        });
        tblCustomer1.setItems(tbl01);
    }

    private void loadTable02() {
        ObservableList<Table02> tbl02 = FXCollections.observableArrayList();

        ObservableList<Customer> allCustomers = CustomerController.getInstance().getAllCustomers();
        allCustomers.forEach(customer -> {
            tbl02.add(
                    new Table02(
                            customer.getCustomerId(),
                            customer.getSalary(),
                            customer.getAddress(),
                            customer.getCity(),
                            customer.getProvince(),
                            customer.getPostalCode()
                    )
            );
        });
        tblCustomer2.setItems(tbl02);
    }

    private void loadInitialValues() {
        cmbCustomerTitle.setValue("Select Title");
        ObservableList list = FXCollections.observableArrayList();
        list.add(new String("MR"));
        list.add(new String("MRS"));
        list.add(new String("MS"));
        cmbCustomerTitle.setItems(list);
    }

    public void btnAddCustomerOnAction(ActionEvent actionEvent) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = format.parse(dateDob.getValue().toString());
            Customer customer = new Customer(
                    txtCustomerId.getText(),
                    cmbCustomerTitle.getValue().toString(),
                    txtCustomerName.getText(),
                    date,
                    Double.parseDouble(txtSalary.getText()),
                    txtAddress.getText(),
                    txtCity.getText(),
                    txtProvince.getText(),
                    txtPostalCode.getText()
            );

            boolean b = CustomerController.getInstance().addCustomer(customer);
            System.out.println(b);
            if (b){
                new Alert(Alert.AlertType.ERROR,"Customer Not Added !").show();
            }else{
                new Alert(Alert.AlertType.CONFIRMATION,"Customer Added !").show();
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }



    }

    public void btnSearchOnAction(ActionEvent actionEvent) {
        Customer customer = CustomerController.getInstance().searchCustomer(txtCustomerId.getText());
        System.out.println(customer);
    }

    public void btnDeleteOnAction(ActionEvent actionEvent) {
    }

    public void btnUpdateOnAction(ActionEvent actionEvent) {
    }





}
