package com.repositories;

import com.models.Address;
import com.helpers.FileUtil;
import java.util.*;

public class AddressRepository {
    private static final String FILE = "addresses.txt";

    public AddressRepository() { FileUtil.ensureFile(FILE); }

    private Address fromLine(String line) {
        String[] p = line.split("\\|", -1);
        if (p.length < 6) return null;
        return new Address(p[0], p[1], p[2], p[3], p[4], p[5]);
    }

    private String toLine(Address a) {
        return String.join("|", FileUtil.clean(a.getId()), FileUtil.clean(a.getUserId()),
                FileUtil.clean(a.getLine()), FileUtil.clean(a.getCity()),
                FileUtil.clean(a.getState()), FileUtil.clean(a.getPincode()));
    }

    public List<Address> findByUser(String userId) {
        List<Address> result = new ArrayList<>();
        for (String line : FileUtil.read(FILE)) {
            Address a = fromLine(line);
            if (a != null && a.getUserId().equals(userId)) result.add(a);
        }
        return result;
    }

    public List<String> findAllIds() {
        List<String> ids = new ArrayList<>();
        for (String line : FileUtil.read(FILE)) {
            Address a = fromLine(line);
            if (a != null) ids.add(a.getId());
        }
        return ids;
    }

    public Address findById(String id) {
        for (String line : FileUtil.read(FILE)) {
            Address a = fromLine(line);
            if (a != null && a.getId().equalsIgnoreCase(id)) return a;
        }
        return null;
    }

    public void save(Address address) {
        List<String> lines = new ArrayList<>();
        boolean found = false;
        for (String line : FileUtil.read(FILE)) {
            Address a = fromLine(line);
            if (a == null) continue;
            if (a.getId().equals(address.getId())) {
                lines.add(toLine(address));
                found = true;
            } else lines.add(toLine(a));
        }
        if (!found) lines.add(toLine(address));
        FileUtil.write(FILE, lines);
    }

    public void delete(String id) {
        List<String> lines = new ArrayList<>();
        for (String line : FileUtil.read(FILE)) {
            Address a = fromLine(line);
            if (a != null && !a.getId().equalsIgnoreCase(id)) lines.add(toLine(a));
        }
        FileUtil.write(FILE, lines);
    }
}
