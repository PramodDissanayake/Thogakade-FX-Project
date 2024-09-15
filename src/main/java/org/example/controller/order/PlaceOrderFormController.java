package org.example.controller.order;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;
import org.example.controller.customer.CustomerController;
import org.example.controller.item.ItemController;
import org.example.db.DBConnection;
import org.example.model.Customer;
import org.example.model.Item;
import org.example.model.Order;
import org.example.model.OrderDetail;
import org.example.model.tm.CartTbl;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceOrderFormController implements Initializable {
    public Label lblDate;
    public Label lblTime;
    public ComboBox cmbCustomerIDs;
    public ComboBox cmbItemCode;
    public Label lblName;
    public Label lblAddress;
    public Label lblSalary;
    public Label lblCity;


    public Label lblDesc;
    public Label lblPackSize;
    public Label lblUnitPrice;
    public Label lblQty;
    public Label lblOrderId;
    public TableView tblCart;
    public TableColumn colItemCode;
    public TableColumn colDesc;
    public TableColumn colQty;
    public TableColumn colUnitPrice;
    public TableColumn colTotal;
    public TextField txtQtyFroCustomer;
    public Label lblNetTotal;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadDateAndTime();
        loadCustomerIDs();
        loadItemCodes();
        generateOrderId();
        colItemCode.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        colDesc.setCellValueFactory(new PropertyValueFactory<>("desc"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        cmbCustomerIDs.getSelectionModel().selectedItemProperty().addListener((observable,oldValue,newValue)-> {
            setCustomerDataFroLbl((String)newValue);
        });
        cmbItemCode.getSelectionModel().selectedItemProperty().addListener((observable,oldValue,newValue)-> {
            setItemDataFroLbl((String)newValue);
        });
    }

    private void setItemDataFroLbl(String ItemCode) {
        Item item = ItemController.getInstance().searchItem(ItemCode);
        System.out.println(item);
        lblDesc.setText(item.getDesc());
        lblPackSize.setText(item.getPackSize());
        lblUnitPrice.setText(String.valueOf(item.getUnitPrice()));
        lblQty.setText(String.valueOf(item.getQty()));
    }

    private void setCustomerDataFroLbl(String customerId) {
        Customer customer = CustomerController.getInstance().searchCustomer(customerId);
        System.out.println(customer);
        lblName.setText(customer.getCustomerName());
        lblAddress.setText(customer.getAddress());
        lblCity.setText(customer.getCity());
        lblSalary.setText(String.valueOf(customer.getSalary()));
    }

    private void loadItemCodes() {
        ObservableList<Item> allItems = ItemController.getInstance().getAllItems();

        ObservableList<String> itemCods=FXCollections.observableArrayList();

        allItems.forEach(item ->{
            itemCods.add(item.getItemCode());
        });

        cmbItemCode.setItems(itemCods);
    }

    private void loadCustomerIDs() {
        ObservableList<Customer> allCustomers = CustomerController.getInstance().getAllCustomers();

        ObservableList ids = FXCollections.observableArrayList();

        allCustomers.forEach(customer -> {
            ids.add(customer.getCustomerId());

        });
        System.out.println(ids);
        cmbCustomerIDs.setItems(ids);

    }

    private void loadDateAndTime() {
        //Date
        Date date = new Date();
        SimpleDateFormat f =new SimpleDateFormat("yyyy-MM-dd");
        lblDate.setText(f.format(date));


        //Time
        Timeline timeline = new Timeline(new KeyFrame(Duration.ZERO,e->{
            LocalTime time = LocalTime.now();
            lblTime.setText(
                    time.getHour()+" : "+time.getMinute()+" : "+time.getSecond()
            );
        }),
                new KeyFrame(Duration.seconds(1))
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    public  void generateOrderId() {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM orders");
            Integer count = 0;
            while (resultSet.next()){
                count = resultSet.getInt(1);

            }
            if (count == 0) {
                lblOrderId.setText("D001");
            }
            String lastOrderId="";
            ResultSet resultSet1 = connection.createStatement().executeQuery("SELECT OrderID\n" +
                    "FROM orders\n" +
                    "ORDER BY OrderID DESC\n" +
                    "LIMIT 1;");
            if (resultSet1.next()){
                lastOrderId = resultSet1.getString(1);
                Pattern pattern = Pattern.compile("[A-Za-z](\\d+)");
                Matcher matcher = pattern.matcher(lastOrderId);
                if (matcher.find()) {
                    int number = Integer.parseInt(matcher.group(1));
                    number++;
                    lblOrderId.setText(String.format("D%03d", number));
                } else {
                    new Alert(Alert.AlertType.WARNING,"hello").show();
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    ObservableList<CartTbl> cartList = FXCollections.observableArrayList();
    public void btnAddToCartOnAction(ActionEvent actionEvent) {

        String itemCode =(String)cmbItemCode.getValue();
        String desc = lblDesc.getText();
        Integer qtyFroCustomer =Integer.parseInt(txtQtyFroCustomer.getText());
        Double unitPrice = Double.valueOf(lblUnitPrice.getText());
        Double total =  qtyFroCustomer*unitPrice;
        CartTbl cartTbl = new CartTbl(itemCode, desc, qtyFroCustomer, unitPrice, total,0.0);
        System.out.println(cartTbl);

        int qtyStock = Integer.parseInt(lblQty.getText());
        if(qtyStock<qtyFroCustomer){
            new Alert(Alert.AlertType.WARNING,"Invalid QTY").show();
            return;
        }

        cartList.add(cartTbl);


        tblCart.setItems(cartList);
        calcNetTotal();

    }

    public void btnClearOnAction(ActionEvent actionEvent) {
    }

    public void btnPlaceOrderOnAction(ActionEvent actionEvent) {
        try {

            String orderId = lblOrderId.getText();
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date orderDate = format.parse(lblDate.getText());
            String customerId = cmbCustomerIDs.getValue().toString();

            List<OrderDetail> orderDetailList = new ArrayList<>();

            for (CartTbl cartTbl : cartList ){
                String oID = lblOrderId.getText();
                String itemCode = cartTbl.getItemCode();
                Integer qty = cartTbl.getQty();
                Double discount = cartTbl.getDiscount();
                orderDetailList.add(new OrderDetail(oID,itemCode,qty,discount));
            }

            Order order = new Order(orderId, orderDate, customerId, orderDetailList);
            Boolean isOrderPlace = OrderController.getInstance().placeOrder(order);
            if (isOrderPlace){
                generateOrderId();
                new Alert(Alert.AlertType.INFORMATION,"order Place !!").show();
            }
            System.out.println(order);

        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void txtAddtoCartOnAction(ActionEvent actionEvent) {
        btnAddToCartOnAction(actionEvent);
    }

    public void calcNetTotal(){
        double ttl=0;
        for (CartTbl cartObj : cartList){
            ttl+=cartObj.getTotal();
        }
        lblNetTotal.setText(String.valueOf(ttl)+"/=");
    }

    public void btnCommitOnAction(ActionEvent actionEvent) {
        try {
            System.out.println("Commit");
            Connection connection = DBConnection.getInstance().getConnection();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void btnRollBackOnAction(ActionEvent actionEvent) {
        try {
            System.out.println("RollBack");
            Connection connection = DBConnection.getInstance().getConnection();
            connection.rollback();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
