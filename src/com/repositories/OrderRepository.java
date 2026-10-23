package com.repositories;

import com.models.Order;
import com.helpers.FileUtil;
import java.util.*;

public class OrderRepository {
    private static final String FILE = "orders.txt";

    public OrderRepository() { FileUtil.ensureFile(FILE); }

    private Order fromLine(String line) {
        String[] p = line.split("\\|", -1);
        if (p.length < 7) return null;
        try {
            return new Order(p[0], p[1], Double.parseDouble(p[2]), p[3], p[4], p[5], p[6]);
        } catch (Exception e) { return null; }
    }

    private String toLine(Order o) {
        return String.join("|", FileUtil.clean(o.getId()), FileUtil.clean(o.getUserId()),
                String.valueOf(o.getTotal()), FileUtil.clean(o.getPaymentStatus()),
                FileUtil.clean(o.getOrderStatus()), FileUtil.clean(o.getAddressId()),
                FileUtil.clean(o.getDateTime()));
    }

    public List<Order> findAll() {
        List<Order> result = new ArrayList<>();
        for (String line : FileUtil.read(FILE)) {
            Order o = fromLine(line);
            if (o != null) result.add(o);
        }
        return result;
    }

    public List<String> findAllIds() {
        List<String> ids = new ArrayList<>();
        for (Order o : findAll()) ids.add(o.getId());
        return ids;
    }

    public List<Order> findByUser(String userId) {
        List<Order> result = new ArrayList<>();
        for (Order o : findAll()) if (o.getUserId().equals(userId)) result.add(o);
        return result;
    }

    public Order findById(String id) {
        for (Order o : findAll()) if (o.getId().equalsIgnoreCase(id)) return o;
        return null;
    }

    public void save(Order order) {
        List<String> lines = new ArrayList<>();
        boolean found = false;
        for (Order o : findAll()) {
            if (o.getId().equals(order.getId())) {
                lines.add(toLine(order));
                found = true;
            } else lines.add(toLine(o));
        }
        if (!found) lines.add(toLine(order));
        FileUtil.write(FILE, lines);
    }
}
