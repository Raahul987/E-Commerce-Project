package com.services;

import com.models.User;
import com.repositories.UserRepository;

public class UserService {
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public User register(String name, String email, String password, String phone) {
        if (repository.findByEmail(email) != null) return null;
        User user = new User(nextCustomerId(), name, email, password, phone, "CUSTOMER");
        repository.save(user);
        return user;
    }

    private String nextCustomerId() {
        int maxId = 0;
        for (User u : repository.findAll()) {
            try {
                int id = Integer.parseInt(u.getId().replace("ID-", ""));
                if (id > maxId) maxId = id;
            } catch (NumberFormatException ignored) {
                // Ignore old/random IDs and the admin ID.
            }
        }
        return "ID-" + (maxId + 1);
    }

    public User findByEmail(String email) {
        return repository.findByEmail(email);
    }

    public User login(String email, String password) {
        User u = repository.findByEmail(email);
        return u != null && u.getPassword().equals(password) ? u : null;
    }

    public void update(User user) {
        repository.save(user);
    }

    public void delete(String id) {
        repository.delete(id);
    }
}
