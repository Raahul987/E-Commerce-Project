package com.repositories;

import com.models.Product;
import com.helpers.FileUtil;
import java.util.*;

public class ProductRepository {
    private static final String FILE = "products.txt";

    public ProductRepository() {
        FileUtil.ensureFile(FILE);
        if (findAll().isEmpty()) {
            save(new Product("P1001", "Laptop", "Electronics", 55000, 10, "15 inch laptop"));
            save(new Product("P1002", "Smartphone", "Electronics", 25000, 20, "Android smartphone"));
            save(new Product("P1003", "Headphones", "Accessories", 1999, 30, "Wireless headphones"));
            save(new Product("P1004", "Backpack", "Fashion", 1499, 25, "Daily-use backpack"));
        }
    }

    private Product fromLine(String line) {
        String[] p = line.split("\\|", -1);
        if (p.length < 6) return null;
        try {
            return new Product(p[0], p[1], p[2], Double.parseDouble(p[3]), Integer.parseInt(p[4]), p[5]);
        } catch (Exception e) {
            return null;
        }
    }

    private String toLine(Product p) {
        return String.join("|", FileUtil.clean(p.getId()), FileUtil.clean(p.getName()),
                FileUtil.clean(p.getCategory()), String.valueOf(p.getPrice()),
                String.valueOf(p.getStock()), FileUtil.clean(p.getDescription()));
    }

    public List<Product> findAll() {
        List<Product> result = new ArrayList<>();
        for (String line : FileUtil.read(FILE)) {
            if (!line.isBlank()) {
                Product p = fromLine(line);
                if (p != null) result.add(p);
            }
        }
        return result;
    }

    public Product findById(String id) {
        for (Product p : findAll()) if (p.getId().equalsIgnoreCase(id)) return p;
        return null;
    }

    public void save(Product product) {
        List<String> lines = new ArrayList<>();
        boolean found = false;
        for (Product p : findAll()) {
            if (p.getId().equals(product.getId())) {
                lines.add(toLine(product));
                found = true;
            } else lines.add(toLine(p));
        }
        if (!found) lines.add(toLine(product));
        FileUtil.write(FILE, lines);
    }

    public void delete(String id) {
        List<String> lines = new ArrayList<>();
        for (Product p : findAll()) if (!p.getId().equalsIgnoreCase(id)) lines.add(toLine(p));
        FileUtil.write(FILE, lines);
    }
}
