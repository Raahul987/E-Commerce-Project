package com.models;

public class Order {
    private String id;
    private String userId;
    private double total;
    private String paymentStatus;
    private String orderStatus;
    private String addressId;
    private String dateTime;

    public Order(String id, String userId, double total, String paymentStatus,
                 String orderStatus, String addressId, String dateTime) {
        this.id = id;
        this.userId = userId;
        this.total = total;
        this.paymentStatus = paymentStatus;
        this.orderStatus = orderStatus;
        this.addressId = addressId;
        this.dateTime = dateTime;
    }

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public double getTotal() { return total; }
    public String getPaymentStatus() { return paymentStatus; }
    public String getOrderStatus() { return orderStatus; }
    public String getAddressId() { return addressId; }
    public String getDateTime() { return dateTime; }

    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
}
