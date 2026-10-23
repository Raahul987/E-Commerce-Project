package com.repositories;

import com.models.User;
import com.helpers.FileUtil;
import java.util.*;

public class UserRepository {
    private static final String FILE = "users.txt";

    public UserRepository() {
        FileUtil.ensureFile(FILE);
        List<String> lines = FileUtil.read(FILE);
        boolean adminExists = lines.stream().anyMatch(x -> x.startsWith("ADMIN001|"));
        if (!adminExists) {
            FileUtil.append(FILE, "ADMIN001|Administrator|admin@ecommerce.com|admin123|0000000000|ADMIN");
        }
    }

    private User fromLine(String line) {
        String[] p = line.split("\\|", -1);
        if (p.length < 6) return null;
        return new User(p[0], p[1], p[2], p[3], p[4], p[5]);
    }

    private String toLine(User u) {
        return String.join("|", FileUtil.clean(u.getId()), FileUtil.clean(u.getName()),
                FileUtil.clean(u.getEmail()), FileUtil.clean(u.getPassword()),
                FileUtil.clean(u.getPhone()), FileUtil.clean(u.getRole()));
    }

    public List<User> findAll() {
        List<User> result = new ArrayList<>();
        for (String line : FileUtil.read(FILE)) {
            if (!line.isBlank()) {
                User u = fromLine(line);
                if (u != null) result.add(u);
            }
        }
        return result;
    }

    public User findById(String id) {
        for (User u : findAll()) if (u.getId().equalsIgnoreCase(id)) return u;
        return null;
    }

    public User findByEmail(String email) {
        for (User u : findAll()) if (u.getEmail().equalsIgnoreCase(email)) return u;
        return null;
    }

    public void save(User user) {
        List<String> lines = new ArrayList<>();
        for (User u : findAll()) lines.add(u.getId().equals(user.getId()) ? toLine(user) : toLine(u));
        boolean found = findById(user.getId()) != null;
        if (!found) lines.add(toLine(user));
        FileUtil.write(FILE, lines);
    }

    public void delete(String id) {
        List<String> lines = new ArrayList<>();
        for (User u : findAll()) if (!u.getId().equals(id)) lines.add(toLine(u));
        FileUtil.write(FILE, lines);
    }
}
