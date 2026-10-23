package com.models;

public class Address {
    private String id;
    private String userId;
    private String line;
    private String city;
    private String state;
    private String pincode;

    public Address(String id, String userId, String line, String city, String state, String pincode) {
        this.id = id;
        this.userId = userId;
        this.line = line;
        this.city = city;
        this.state = state;
        this.pincode = pincode;
    }

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getLine() { return line; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getPincode() { return pincode; }

    public void setLine(String line) { this.line = line; }
    public void setCity(String city) { this.city = city; }
    public void setState(String state) { this.state = state; }
    public void setPincode(String pincode) { this.pincode = pincode; }
}
