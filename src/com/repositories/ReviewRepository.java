package com.repositories;

import com.models.Review;
import com.helpers.FileUtil;
import java.util.*;

public class ReviewRepository {
    private static final String FILE = "reviews.txt";

    public ReviewRepository() { FileUtil.ensureFile(FILE); }

    public void save(Review review) {
        FileUtil.append(FILE, String.join("|", FileUtil.clean(review.getUserId()),
                FileUtil.clean(review.getProductId()), String.valueOf(review.getRating()),
                FileUtil.clean(review.getComment())));
    }

    public List<Review> findByProduct(String productId) {
        List<Review> result = new ArrayList<>();
        for (String line : FileUtil.read(FILE)) {
            String[] p = line.split("\\|", -1);
            if (p.length >= 4 && p[1].equalsIgnoreCase(productId)) {
                try { result.add(new Review(p[0], p[1], Integer.parseInt(p[2]), p[3])); }
                catch (Exception ignored) {}
            }
        }
        return result;
    }
}
