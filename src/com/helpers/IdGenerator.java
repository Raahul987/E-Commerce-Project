package com.helpers;

import java.util.Collection;

public class IdGenerator {
    public static String next(String prefix, Collection<String> existingIds) {
        int max = 0;
        for (String id : existingIds) {
            if (id == null || !id.startsWith(prefix)) continue;
            try {
                int number = Integer.parseInt(id.substring(prefix.length()));
                if (number > max) max = number;
            } catch (NumberFormatException ignored) {
            }
        }
        return prefix + (max + 1);
    }
}
