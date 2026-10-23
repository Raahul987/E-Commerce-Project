package com.repositories;

import com.models.CartItem;
import com.helpers.FileUtil;
import java.util.*;

public class CartRepository {
    private static final String FILE = "cart.txt";

    public CartRepository() { FileUtil.ensureFile(FILE); }

    private CartItem fromLine(String line) {
        String[] p = line.split("\\|", -1);
        if (p.length < 3) return null;
        try { return new CartItem(p[0], p[1], Integer.parseInt(p[2])); }
        catch (Exception e) { return null; }
    }

    private String toLine(CartItem c) {
        return c.getUserId() + "|" + c.getProductId() + "|" + c.getQuantity();
    }

    public List<CartItem> findByUser(String userId) {
        List<CartItem> result = new ArrayList<>();
        for (String line : FileUtil.read(FILE)) {
            CartItem c = fromLine(line);
            if (c != null && c.getUserId().equals(userId)) result.add(c);
        }
        return result;
    }

    public CartItem find(String userId, String productId) {
        for (CartItem c : findByUser(userId))
            if (c.getProductId().equalsIgnoreCase(productId)) return c;
        return null;
    }

    public void save(CartItem item) {
        List<String> lines = new ArrayList<>();
        boolean found = false;
        for (String line : FileUtil.read(FILE)) {
            CartItem c = fromLine(line);
            if (c == null) continue;
            if (c.getUserId().equals(item.getUserId()) && c.getProductId().equals(item.getProductId())) {
                lines.add(toLine(item));
                found = true;
            } else lines.add(toLine(c));
        }
        if (!found) lines.add(toLine(item));
        FileUtil.write(FILE, lines);
    }

    public void delete(String userId, String productId) {
        List<String> lines = new ArrayList<>();
        for (String line : FileUtil.read(FILE)) {
            CartItem c = fromLine(line);
            if (c == null) continue;
            if (!(c.getUserId().equals(userId) && c.getProductId().equalsIgnoreCase(productId)))
                lines.add(toLine(c));
        }
        FileUtil.write(FILE, lines);
    }

    public void clear(String userId) {
        List<String> lines = new ArrayList<>();
        for (String line : FileUtil.read(FILE)) {
            CartItem c = fromLine(line);
            if (c != null && !c.getUserId().equals(userId)) lines.add(toLine(c));
        }
        FileUtil.write(FILE, lines);
    }
}
