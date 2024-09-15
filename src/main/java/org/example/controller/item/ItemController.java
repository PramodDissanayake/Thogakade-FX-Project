package org.example.controller.item;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.crudUtil.CrudUtil;
import org.example.db.DBConnection;
import org.example.model.Customer;
import org.example.model.Item;
import org.example.model.OrderDetail;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ItemController {
    private static ItemController instance;

    private ItemController() {
    }

    public static ItemController getInstance() {
        if (instance == null) {
            return instance = new ItemController();
        }
        return instance;
    }

    public ObservableList<Item> getAllItems() {
        try {
            ResultSet resultSet = DBConnection.getInstance().getConnection().createStatement().executeQuery("SELECT * FROM item");
            ObservableList<Item> listTable = FXCollections.observableArrayList();
            while (resultSet.next()) {
                listTable.add(
                        new Item(
                                resultSet.getString(1),
                                resultSet.getString(2),
                                resultSet.getString(3),
                                resultSet.getDouble(4),
                                resultSet.getInt(5)
                        )
                );
            }
            return listTable;

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Item searchItem(String itemCode) {
        try {
            PreparedStatement psTm = DBConnection.getInstance().getConnection().prepareStatement("SELECT * FROM item WHERE ItemCode=?");

            psTm.setString(1, itemCode);
            boolean execute = psTm.execute();
            if (execute) {
                ResultSet resultSet = psTm.getResultSet();
                ;
                while (resultSet.next()) {
                    return new Item(
                            resultSet.getString(1),
                            resultSet.getString(2),
                            resultSet.getString(3),
                            resultSet.getDouble(4),
                            resultSet.getInt(5)
                    );
                }
            }

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public boolean updateStock(List<OrderDetail> orderDetailList) {
        boolean isUpdate = false;
        for (OrderDetail orderDetail : orderDetailList) {
            isUpdate = updateStock(orderDetail);
        }
        if (isUpdate){
            return true;
        }
        return false;
    }

    public boolean updateStock(OrderDetail orderDetail) {

        try {
            Object isUpdate = CrudUtil.execute("UPDATE item SET QtyOnHand=QtyOnHand-? WHERE ItemCode = ?", orderDetail.getQty(), orderDetail.getItemCode());
            return (Boolean) isUpdate;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
