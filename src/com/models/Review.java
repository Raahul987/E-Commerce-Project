package com.models;

public class Review {
    private String userId;
    private String productId;
    private int rating;
    private String comment;

    public Review(String userId, String productId, int rating, String comment) {
        this.userId = userId;
        this.productId = productId;
        this.rating = rating;
        this.comment = comment;
    }

    public String getUserId() { return userId; }
    public String getProductId() { return productId; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
}
