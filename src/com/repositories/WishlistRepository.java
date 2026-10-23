package com.repositories;

import com.helpers.FileUtil;
import java.util.*;

public class WishlistRepository {
    private static final String FILE = "wishlist.txt";

    public WishlistRepository() { FileUtil.ensureFile(FILE); }

    public List<String> findByUser(String userId) {
        List<String> result = new ArrayList<>();
        for (String line : FileUtil.read(FILE)) {
            String[] p = line.split("\\|", -1);
            if (p.length >= 2 && p[0].equals(userId)) result.add(p[1]);
        }
        return result;
    }

    public void add(String userId, String productId) {
        if (!findByUser(userId).contains(productId))
            FileUtil.append(FILE, userId + "|" + productId);
    }

    public void remove(String userId, String productId) {
        List<String> lines = new ArrayList<>();
        for (String line : FileUtil.read(FILE)) {
            String[] p = line.split("\\|", -1);
            if (p.length >= 2 && p[0].equals(userId) && p[1].equalsIgnoreCase(productId)) continue;
            lines.add(line);
        }
        FileUtil.write(FILE, lines);
    }
}
