package com.services;

import com.models.CartItem;
import com.models.Product;
import com.repositories.CartRepository;
import com.repositories.ProductRepository;
import java.util.*;

public class CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    public boolean add(String userId, String productId, int quantity) {
        Product p = productRepository.findById(productId);
        if (p == null || quantity <= 0 || p.getStock() < quantity) return false;

        CartItem existing = cartRepository.find(userId, productId);
        int newQty = existing == null ? quantity : existing.getQuantity() + quantity;
        if (newQty > p.getStock()) return false;
        cartRepository.save(new CartItem(userId, productId, newQty));
        return true;
    }

    public boolean update(String userId, String productId, int quantity) {
        Product p = productRepository.findById(productId);
        if (p == null || quantity < 1 || quantity > p.getStock()) return false;
        cartRepository.save(new CartItem(userId, productId, quantity));
        return true;
    }

    public void remove(String userId, String productId) {
        cartRepository.delete(userId, productId);
    }

    public void clear(String userId) {
        cartRepository.clear(userId);
    }

    public List<CartItem> items(String userId) {
        return cartRepository.findByUser(userId);
    }

    public double total(String userId) {
        double total = 0;
        for (CartItem item : items(userId)) {
            Product p = productRepository.findById(item.getProductId());
            if (p != null) total += p.getPrice() * item.getQuantity();
        }
        return total;
    }
}
