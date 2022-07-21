package datastorage;

import javafx.scene.control.Button;

public class Order {

    private String orderID;
    private String order;
    public Button placeholder = new Button("placeholder");
    private float cost;

    public Order(String orderID, String order) {
        this.orderID = orderID;
        this.order = order;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public Button getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(Button placeholder) {
        this.placeholder = placeholder;
    }

}
