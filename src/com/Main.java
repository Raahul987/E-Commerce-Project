package com;

import com.models.*;
import com.repositories.*;
import com.services.*;
import com.helpers.*;
import java.time.LocalDateTime;
import java.util.*;

public class Main {
    private final InputUtil input = new InputUtil(new Scanner(System.in));
    private final UserRepository userRepo = new UserRepository();
    private final ProductRepository productRepo = new ProductRepository();
    private final CartRepository cartRepo = new CartRepository();
    private final AddressRepository addressRepo = new AddressRepository();
    private final OrderRepository orderRepo = new OrderRepository();
    private final WishlistRepository wishlistRepo = new WishlistRepository();
    private final ReviewRepository reviewRepo = new ReviewRepository();
    private final UserService userService = new UserService(userRepo);
    private final CartService cartService = new CartService(cartRepo, productRepo);

    public static void main(String[] args) {
        new Main().start();
    }

    private void start() {
        while (true) {
            System.out.println("\n======================================");
            System.out.println("        E-COMMERCE APPLICATION");
            System.out.println("======================================");
            System.out.println("1. Customer Register");
            System.out.println("2. Customer/Admin Login");
            System.out.println("3. Browse Products");
            System.out.println("0. Exit");

            int choice = input.integer("Choose: ");
            switch (choice) {
                case 1 -> register();
                case 2 -> login();
                case 3 -> browseProducts();
                case 0 -> { System.out.println("Thank you."); return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void register() {
        System.out.println("\n--- CUSTOMER REGISTRATION ---");
        String name = input.lettersOnly("Name: ", "Give a valid user name.");
        String email = input.email("Email: ");
        String password = input.password("Password: ");
        String phone = input.mobile("Phone: ");

        User user = userService.register(name, email, password, phone);
        if (user == null) System.out.println("Email already exists.");
        else System.out.println("Registration successful. Your User ID: " + user.getId());
    }

    private void login() {
        System.out.println("\n--- LOGIN ---");
        String email = input.text("Email: ");
        String password = input.text("Password: ");

        User user = userService.findByEmail(email);
        if (user == null) {
            System.out.println("Invalid email.");
            return;
        }
        if (!user.getPassword().equals(password)) {
            System.out.println("Invalid password.");
            return;
        }

        if (user.getRole().equalsIgnoreCase("ADMIN")) adminMenu(user);
        else customerMenu(user);
    }

    private void customerMenu(User user) {
        while (true) {
            System.out.println("\n======================================");
            System.out.println("          CUSTOMER HOME");
            System.out.println("======================================");
            System.out.println("Welcome, " + user.getName());
            System.out.println("1. Products");
            System.out.println("2. Buy Products");
            System.out.println("3. My Cart");
            System.out.println("4. Wishlist");
            System.out.println("5. My Address");
            System.out.println("6. My Orders");
            System.out.println("7. My Account");
            System.out.println("0. Logout");
            int choice = input.integer("Choose: ");
            switch (choice) {
                case 1 -> { browseProducts(); String id=input.text("Product ID (0 to back): "); if(!id.equals("0")) showProductDetailsForCustomer(user,id); }
                case 2 -> directBuy(user);
                case 3 -> simpleCartMenu(user);
                case 4 -> wishlistMenu(user);
                case 5 -> addressMenu(user);
                case 6 -> orderHistory(user);
                case 7 -> accountMenu(user);
                case 0 -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void showProductDetailsForCustomer(User user, String id) {
        Product p=productRepo.findById(id);
        if(p==null){System.out.println("Product not found.");return;}
        System.out.println("\n--- PRODUCT DETAILS ---");
        printProduct(p);
        System.out.println("Description: "+p.getDescription());
        if(input.yesNo("Add to cart? (Y/N): ")){
            int q=input.integer("Quantity: ");
            System.out.println(cartService.add(user.getId(),id,q)?"Product added to cart.":"Unable to add product.");
        }
        if(input.yesNo("Add to wishlist? (Y/N): ")){wishlistRepo.add(user.getId(),id);System.out.println("Added to wishlist.");}
    }

    private void simpleCartMenu(User user) {
        while(true){
            System.out.println("\n--- MY CART ---");
            showCart(user);
            System.out.println("1. Add Product");
            System.out.println("2. Change Quantity");
            System.out.println("3. Remove Product");
            System.out.println("4. Order From My Cart");
            System.out.println("0. Back");

            int c=input.integer("Choose: ");
            if(c==0)return;

            if(c==1){
                browseProducts();
                String id=input.text("Product ID: ");
                int q=input.integer("Quantity: ");
                System.out.println(cartService.add(user.getId(),id,q)
                        ?"Product added to cart.":"Unable to add product.");
            } else if(c==2){
                String id=input.text("Product ID: ");
                int q=input.integer("New quantity: ");
                System.out.println(cartService.update(user.getId(),id,q)
                        ?"Quantity updated.":"Unable to update quantity.");
            } else if(c==3){
                cartService.remove(user.getId(),input.text("Product ID: "));
                System.out.println("Product removed from cart.");
            } else if(c==4){
                quickCheckout(user);
                return;
            }
        }
    }

    private void directBuy(User user) {
        System.out.println("\n--- BUY PRODUCTS ---");
        browseProducts();

        String id = input.text("Product ID (0 to back): ");
        if (id.equals("0")) return;

        Product product = productRepo.findById(id);
        if (product == null) {
            System.out.println("Product not found.");
            return;
        }

        int quantity = input.integer("Quantity: ");
        if (quantity <= 0 || quantity > product.getStock()) {
            System.out.println("Unable to buy product.");
            return;
        }

        List<CartItem> directItems = new ArrayList<>();
        directItems.add(new CartItem(user.getId(), product.getId(), quantity));
        placeOrder(user, directItems, product.getPrice() * quantity, false);
    }

    private void browseProducts() {
        System.out.println("\n--- PRODUCTS ---");
        List<Product> products = productRepo.findAll();
        printProductsTable(products);
    }

    // Displays products in a neatly aligned console table.
    private void printProductsTable(List<Product> products) {
        if (products.isEmpty()) {
            System.out.println("No products available.");
            return;
        }

        String line = "+--------+----------------------+----------------+-------------+-------+";
        System.out.println(line);
        System.out.printf("| %-6s | %-20s | %-14s | %-11s | %-5s |%n",
                "ID", "Product Name", "Category", "Price (Rs.)", "Stock");
        System.out.println(line);

        for (Product p : products) {
            System.out.printf("| %-6s | %-20.20s | %-14.14s | %11.2f | %5d |%n",
                    p.getId(), p.getName(), p.getCategory(), p.getPrice(), p.getStock());
        }
        System.out.println(line);
    }

    private void printProduct(Product p) {
        System.out.printf("%-8s | %-20s | %-14s | Rs. %10.2f | Stock: %d%n",
                p.getId(), p.getName(), p.getCategory(), p.getPrice(), p.getStock());
    }

    private void searchProducts() {
        String keyword = input.text("Search name/category: ").toLowerCase();
        for (Product p : productRepo.findAll()) {
            if (keyword.isBlank() || p.getName().toLowerCase().contains(keyword)
                    || p.getCategory().toLowerCase().contains(keyword)) printProduct(p);
        }
    }

    private void showCart(User user) {
        List<CartItem> items = cartService.items(user.getId());
        if (items.isEmpty()) {
            System.out.println("Cart is empty.");
            return;
        }
        for (CartItem item : items) {
            Product p = productRepo.findById(item.getProductId());
            if (p != null)
                System.out.printf("%s - %s x%d = Rs. %.2f%n",
                        p.getId(), p.getName(), item.getQuantity(), p.getPrice() * item.getQuantity());
        }
        System.out.printf("Cart Total: Rs. %.2f%n", cartService.total(user.getId()));
    }

    private void wishlistMenu(User user) {
        while (true) {
            System.out.println("\n--- WISHLIST ---");
            List<String> ids = wishlistRepo.findByUser(user.getId());
            if (ids.isEmpty()) System.out.println("Wishlist is empty.");
            for (String id : ids) {
                Product p = productRepo.findById(id);
                if (p != null) printProduct(p);
            }

            System.out.println("1. Add");
            System.out.println("2. Remove");
            System.out.println("0. Back");
            int c = input.integer("Choose: ");
            if (c == 0) return;
            if (c == 1) wishlistRepo.add(user.getId(), input.text("Product ID: "));
            else if (c == 2) wishlistRepo.remove(user.getId(), input.text("Product ID: "));
        }
    }

    private void addressMenu(User user) {
        while(true){
            System.out.println("\n--- MY ADDRESS ---");
            List<Address> a=addressRepo.findByUser(user.getId());
            if(a.isEmpty())System.out.println("No address saved.");
            for(Address x:a){
                System.out.println("\nAddress");
                String[] addressParts = x.getLine().split(",", 2);
                String doorNo = addressParts.length > 0 ? addressParts[0].trim() : "";
                String streetName = addressParts.length > 1 ? addressParts[1].trim() : "";
                System.out.println("Door No    : " + doorNo);
                System.out.println("Street Name: " + streetName);
                System.out.println("City       : " + x.getCity());
                System.out.println("State      : " + x.getState());
                System.out.println("Pin Code   : " + x.getPincode());
            }
            System.out.println("1. Add Address");
            System.out.println("2. Edit Address");
            System.out.println("3. Delete Address");
            System.out.println("0. Back");
            int c=input.integer("Choose: "); if(c==0)return;
            if(c==1){
                System.out.println("\nAddress");
                String doorNo=input.text("Door No: ");
                String streetName=input.text("Street Name: ");
                String city=input.lettersOnly("City: ", "Give a valid city name.");
                String state=input.lettersOnly("State: ", "Give a valid state name.");
                String pincode=input.digitsOnly("Pin Code: ");
                Address x=new Address(IdGenerator.next("ADDRESS-", addressRepo.findAllIds()),user.getId(),
                        doorNo + ", " + streetName, city, state, pincode);
                addressRepo.save(x);
                System.out.println("Address saved.");
            }
            else if(c==2){
                String id=input.text("Address ID: ");
                Address x=addressRepo.findById(id);
                if(x==null||!x.getUserId().equals(user.getId())){System.out.println("Address not found.");continue;}
                System.out.println("\nAddress");
                String doorNo=input.text("Door No: ");
                String streetName=input.text("Street Name: ");
                x.setLine(doorNo + ", " + streetName);
                x.setCity(input.lettersOnly("City: ", "Give a valid city name."));
                x.setState(input.lettersOnly("State: ", "Give a valid state name."));
                x.setPincode(input.digitsOnly("Pin Code: "));
                addressRepo.save(x);
                System.out.println("Address updated.");
            }
            else if(c==3){addressRepo.delete(input.text("Address ID: "));System.out.println("Address deleted.");}
        }
    }

    private void checkout(User user) { quickCheckout(user); }

    private void quickCheckout(User user) {
        List<CartItem> items = cartService.items(user.getId());
        if (items.isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }
        placeOrder(user, items, cartService.total(user.getId()), true);
    }

    private void placeOrder(User user, List<CartItem> items, double total, boolean clearCart) {
        System.out.println("\n======================================");
        System.out.println("             PLACE ORDER");
        System.out.println("======================================");

        System.out.println("--- ORDER ITEMS ---");
        for (CartItem item : items) {
            Product p = productRepo.findById(item.getProductId());
            if (p != null) {
                System.out.printf("%s - %s x%d = Rs. %.2f%n",
                        p.getId(), p.getName(), item.getQuantity(),
                        p.getPrice() * item.getQuantity());
            }
        }

        System.out.printf("Order Total: Rs. %.2f%n", total);

        System.out.println("\n--- CUSTOMER DETAILS ---");
        System.out.println("Username     : " + user.getName());
        System.out.println("Mobile Number: " + user.getPhone());
        System.out.println("Email        : " + user.getEmail());

        List<Address> addresses = addressRepo.findByUser(user.getId());
        Address address;

        if (addresses.isEmpty()) {
            System.out.println("\nAddress");
            String doorNo=input.text("Door No: ");
            String streetName=input.text("Street Name: ");
            String city=input.lettersOnly("City: ", "Give a valid city name.");
            String state=input.lettersOnly("State: ", "Give a valid state name.");
            String pincode=input.digitsOnly("Pin Code: ");
            address = new Address(
                    IdGenerator.next("ADDRESS-", addressRepo.findAllIds()),
                    user.getId(),
                    doorNo + ", " + streetName,
                    city,
                    state,
                    pincode
            );
            addressRepo.save(address);
        } else {
            address = addresses.get(0);
            String[] addressParts = address.getLine().split(",", 2);
            String doorNo = addressParts.length > 0 ? addressParts[0].trim() : "";
            String streetName = addressParts.length > 1 ? addressParts[1].trim() : "";
            System.out.println("\nAddress");
            System.out.println("Door No    : " + doorNo);
            System.out.println("Street Name: " + streetName);
            System.out.println("City       : " + address.getCity());
            System.out.println("State      : " + address.getState());
            System.out.println("Pin Code   : " + address.getPincode());

            if (!input.yesNo("Use this address? (Y/N): ")) {
                System.out.println("\nAddress");
                doorNo=input.text("Door No: ");
                streetName=input.text("Street Name: ");
                address = new Address(
                        IdGenerator.next("ADDRESS-", addressRepo.findAllIds()),
                        user.getId(),
                        doorNo + ", " + streetName,
                        input.lettersOnly("City: ", "Give a valid city name."),
                        input.lettersOnly("State: ", "Give a valid state name."),
                        input.digitsOnly("Pin Code: ")
                );
                addressRepo.save(address);
            }
        }

        System.out.printf("Final Amount: Rs. %.2f%n", total);
        System.out.println("1. UPI / Card");
        System.out.println("2. Cash on Delivery");

        int payment = input.integer("Payment: ");
        if (payment != 1 && payment != 2) {
            System.out.println("Invalid payment option.");
            return;
        }

        if (payment == 1 && !input.yesNo("Payment successful? (Y/N): ")) {
            System.out.println("Payment Failed. Please try again.");
            return;
        }

        String orderId = IdGenerator.next("ORDER-", orderRepo.findAllIds());
        orderRepo.save(new Order(
                orderId,
                user.getId(),
                total,
                payment == 2 ? "COD" : "PAID",
                "PLACED",
                address.getId(),
                LocalDateTime.now().toString()
        ));

        for (CartItem item : items) {
            Product product = productRepo.findById(item.getProductId());
            if (product != null) {
                product.setStock(product.getStock() - item.getQuantity());
                productRepo.save(product);

                FileUtil.append(
                        "order_items.txt",
                        orderId + "|" + item.getProductId() + "|" +
                                item.getQuantity() + "|" + product.getPrice()
                );
            }
        }

        if (clearCart) {
            cartService.clear(user.getId());
        }

        System.out.println("\n======================================");
        System.out.println("          ORDER CONFIRMED");
        System.out.println("======================================");
        System.out.println("Username : " + user.getName());
        System.out.println("Mobile   : " + user.getPhone());
        System.out.println("Email    : " + user.getEmail());
        String[] confirmedAddressParts = address.getLine().split(",", 2);
        String confirmedDoorNo = confirmedAddressParts.length > 0 ? confirmedAddressParts[0].trim() : "";
        String confirmedStreetName = confirmedAddressParts.length > 1 ? confirmedAddressParts[1].trim() : "";
        System.out.println("Address  :");
        System.out.println("Door No    : " + confirmedDoorNo);
        System.out.println("Street Name: " + confirmedStreetName);
        System.out.println("City       : " + address.getCity());
        System.out.println("State      : " + address.getState());
        System.out.println("Pin Code   : " + address.getPincode());
        System.out.println("Order ID : " + orderId);
        System.out.printf("Amount   : Rs. %.2f%n", total);
        System.out.println("Status   : PLACED");
    }

    private void orderHistory(User user) {
        System.out.println("\n--- MY ORDERS ---");
        List<Order> orders = orderRepo.findByUser(user.getId());
        if (orders.isEmpty()) {
            System.out.println("No orders.");
            return;
        }
        for (Order o : orders)
            System.out.printf("%s | Rs. %.2f | Payment: %s | Status: %s | %s%n",
                    o.getId(), o.getTotal(), o.getPaymentStatus(), o.getOrderStatus(), o.getDateTime());
    }

    private void reviewProduct(User user) {
        List<Order> orders = orderRepo.findByUser(user.getId());
        boolean delivered = orders.stream().anyMatch(o -> o.getOrderStatus().equalsIgnoreCase("DELIVERED"));
        if (!delivered) {
            System.out.println("You can review products after delivery.");
            return;
        }

        String productId = input.text("Product ID to review: ");
        int rating = input.integer("Rating (1-5): ");
        if (rating < 1 || rating > 5) {
            System.out.println("Rating must be 1 to 5.");
            return;
        }
        String comment = input.text("Comment: ");
        reviewRepo.save(new Review(user.getId(), productId, rating, comment));
        System.out.println("Review submitted.");
    }

    private void accountMenu(User user) {
        while(true){
            System.out.println("\n--- MY ACCOUNT ---");
            System.out.println("User ID      : " + user.getId());
            System.out.println("Username     : " + user.getName());
            System.out.println("Email        : " + user.getEmail());
            System.out.println("Mobile Number: " + user.getPhone());
            System.out.println("Role         : " + user.getRole());

            System.out.println("1. Edit Account");
            System.out.println("2. Change Password");
            System.out.println("3. Delete Account");
            System.out.println("0. Back");

            int c=input.integer("Choose: ");
            if(c==0)return;
            if(c==1) updateAccount(user);
            else if(c==2) changePassword(user);
            else if(c==3 && deleteAccount(user)) return;
            else if(c<0 || c>3) System.out.println("Invalid option.");
        }
    }

    private void updateAccount(User user) {
        System.out.println("\n--- EDIT ACCOUNT ---");
        user.setName(input.lettersOnly("New name: ", "Give a valid user name."));
        user.setPhone(input.mobile("New mobile number: "));
        userService.update(user);
        System.out.println("Account updated successfully.");
    }

    private void changePassword(User user) {
        System.out.println("\n--- CHANGE PASSWORD ---");
        String newPassword = input.password("New password: ");
        user.setPassword(newPassword);
        userService.update(user);
        System.out.println("Password changed successfully.");
    }

    private boolean deleteAccount(User user) {
        String confirmation = input.text("Type DELETE to permanently delete your account: ");
        if (!confirmation.equals("DELETE")) {
            System.out.println("Deletion cancelled.");
            return false;
        }
        userService.delete(user.getId());
        System.out.println("Account deleted.");
        return true;
    }

    private void adminMenu(User admin) {
        while (true) {
            System.out.println("\n--- ADMIN DASHBOARD ---");
            System.out.println("1. Manage Products");
            System.out.println("2. Manage Orders");
            System.out.println("3. Manage Users");
            System.out.println("4. Inventory");
            System.out.println("5. Reports");
            System.out.println("0. Logout");

            int c = input.integer("Choose: ");
            switch (c) {
                case 1 -> productAdminMenu();
                case 2 -> orderAdminMenu();
                case 3 -> userAdminMenu();
                case 4 -> inventory();
                case 5 -> report();
                case 0 -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void productAdminMenu() {
        while (true) {
            System.out.println("\n--- MANAGE PRODUCTS ---");
            System.out.println("1. View");
            System.out.println("2. Add");
            System.out.println("3. Edit");
            System.out.println("4. Delete");
            System.out.println("0. Back");
            int c = input.integer("Choose: ");
            if (c == 0) return;

            if (c == 1) browseProducts();
            else if (c == 2) {
                String id = input.text("Product ID: ");
                String name = input.lettersOnly("Name: ", "Give a valid product name.");
                String category = input.lettersOnly("Category: ", "Give a valid category.");
                double price = input.decimal("Price: ");
                int stock = input.integer("Stock: ");
                String description = input.text("Description: ");
                productRepo.save(new Product(id, name, category, price, stock, description));
                System.out.println("Product saved.");
            } else if (c == 3) {
                Product p = productRepo.findById(input.text("Product ID: "));
                if (p == null) { System.out.println("Not found."); continue; }
                p.setName(input.lettersOnly("Name: ", "Give a valid product name."));
                p.setCategory(input.lettersOnly("Category: ", "Give a valid category."));
                p.setPrice(input.decimal("Price: "));
                p.setStock(input.integer("Stock: "));
                p.setDescription(input.text("Description: "));
                productRepo.save(p);
                System.out.println("Product updated.");
            } else if (c == 4) {
                productRepo.delete(input.text("Product ID: "));
                System.out.println("Product deleted.");
            }
        }
    }

    private void orderAdminMenu() {
        while(true){
            System.out.println("\n--- DELIVER ORDER ---");
            System.out.println("1. Update Delivery Status");System.out.println("0. Back");
            int c=input.integer("Choose: ");if(c==0)return;
            if(c==1){
                String name=input.text("Customer Name: ");String productId=input.text("Product ID: ");User customer=null;
                for(User u:userRepo.findAll())if(u.getName().equalsIgnoreCase(name)){customer=u;break;}
                if(customer==null){System.out.println("Customer not found.");continue;}
                Order selected=null;
                for(Order o:orderRepo.findByUser(customer.getId()))for(String line:FileUtil.read("order_items.txt")){String[] p=line.split("\\|",-1);if(p.length>=3&&p[0].equals(o.getId())&&p[1].equalsIgnoreCase(productId)){selected=o;break;}if(selected!=null)break;}
                if(selected==null){System.out.println("Order for this customer and product was not found.");continue;}
                System.out.println("Customer: "+customer.getName());System.out.println("Product ID: "+productId);System.out.println("Current Status: "+selected.getOrderStatus());
                System.out.println("1. SHIPPED");System.out.println("2. OUT_FOR_DELIVERY");System.out.println("3. DELIVERED");
                int st=input.integer("Status: ");String ns=st==1?"SHIPPED":st==2?"OUT_FOR_DELIVERY":st==3?"DELIVERED":null;
                if(ns==null){System.out.println("Invalid status.");continue;}selected.setOrderStatus(ns);orderRepo.save(selected);System.out.println("Order status updated to "+ns+".");
            }
        }
    }

    private void userAdminMenu() {
        while (true) {
            System.out.println("\n--- MANAGE USERS ---");
            for (User u : userRepo.findAll())
                System.out.printf("%s | %s | %s | %s%n", u.getId(), u.getName(), u.getEmail(), u.getRole());

            System.out.println("1. Delete Customer");
            System.out.println("0. Back");
            int c = input.integer("Choose: ");
            if (c == 0) return;
            if (c == 1) {
                String id = input.text("Customer ID: ");
                User u = userRepo.findById(id);
                if (u != null && u.getRole().equals("CUSTOMER")) {
                    userRepo.delete(id);
                    System.out.println("Customer deleted.");
                } else System.out.println("Customer not found.");
            }
        }
    }

    private void inventory() {
        System.out.println("\n--- INVENTORY ---");
        for (Product p : productRepo.findAll())
            System.out.printf("%s | %s | Stock: %d%n", p.getId(), p.getName(), p.getStock());
    }

    private void report() {
        List<Order> orders = orderRepo.findAll();
        double sales = 0;
        for (Order o : orders)
            if (o.getPaymentStatus().equalsIgnoreCase("PAID") || o.getPaymentStatus().equalsIgnoreCase("COD"))
                sales += o.getTotal();

        System.out.println("\n--- SALES REPORT ---");
        System.out.println("Total Orders: " + orders.size());
        System.out.printf("Total Order Value: Rs. %.2f%n", sales);
        System.out.println("Total Users: " + userRepo.findAll().size());
        System.out.println("Total Products: " + productRepo.findAll().size());
    }
}
