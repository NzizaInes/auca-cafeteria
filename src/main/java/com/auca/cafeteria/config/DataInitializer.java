package com.auca.cafeteria.config;

import com.auca.cafeteria.model.*;
import com.auca.cafeteria.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner seedDatabase(
            UserRepository userRepository,
            MenuItemRepository menuItemRepository) {

        return args -> {
            // Only seed if database is empty — prevents duplicates on restart
            if (userRepository.count() > 0) {
                System.out.println("===========================================");
                System.out.println("  Database already has data — skipping seed");
                System.out.println("===========================================");
                return;
            }

            // Demo users
            Student student = new Student();
            student.setName("UWAYO Nziza Ines");
            student.setEmail("student@auca.ac.rw");
            student.setPassword(passwordEncoder.encode("password123"));
            student.setStudentId("S2024001");
            student.setFaculty("Information Technology");
            userRepository.save(student);

            KitchenStaff kitchenStaff = new KitchenStaff();
            kitchenStaff.setName("Marie Claire Uwimana");
            kitchenStaff.setEmail("kitchen@auca.ac.rw");
            kitchenStaff.setPassword(passwordEncoder.encode("password123"));
            kitchenStaff.setStaffId("K001");
            kitchenStaff.setShift("MORNING");
            userRepository.save(kitchenStaff);

            Cashier cashier = new Cashier();
            cashier.setName("Eric Nshimiyimana");
            cashier.setEmail("cashier@auca.ac.rw");
            cashier.setPassword(passwordEncoder.encode("password123"));
            cashier.setCashierId("C001");
            cashier.setShift("MORNING");
            userRepository.save(cashier);

            Admin admin = new Admin();
            admin.setName("Rutarindwa Jean Pierre");
            admin.setEmail("admin@auca.ac.rw");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setDepartment("Cafeteria Management");
            admin.setAccessLevel(1);
            userRepository.save(admin);

            // Demo menu items
            String[][] items = {
                    {"Ugali + Beans",  "Traditional Rwandan staple",  "500",  "LUNCH"},
                    {"Rice + Chicken", "Grilled chicken with rice",   "1500", "LUNCH"},
                    {"Chapati + Egg",  "Breakfast chapati with egg",  "800",  "BREAKFAST"},
                    {"Matoke + Sauce", "Steamed plantain with sauce", "700",  "LUNCH"},
                    {"Tea + Bread",    "Morning tea with bread",      "300",  "BREAKFAST"},
                    {"Pasta + Mince",  "Spaghetti with minced meat",  "1200", "DINNER"},
                    {"Fruit Salad",    "Fresh mixed seasonal fruits", "600",  "BREAKFAST"},
                    {"Fried Potatoes", "Crispy golden potatoes",      "500",  "LUNCH"}
            };

            for (String[] item : items) {
                MenuItem menuItem = new MenuItem();
                menuItem.setName(item[0]);
                menuItem.setDescription(item[1]);
                menuItem.setPrice(Double.parseDouble(item[2]));
                menuItem.setCategory(item[3]);
                menuItem.setAvailable(true);
                menuItemRepository.save(menuItem);
            }

            System.out.println("===========================================");
            System.out.println("  AUCA Cafeteria — Demo Data Loaded");
            System.out.println("  student@auca.ac.rw  / password123");
            System.out.println("  kitchen@auca.ac.rw  / password123");
            System.out.println("  cashier@auca.ac.rw  / password123");
            System.out.println("  admin@auca.ac.rw    / admin123");
            System.out.println("===========================================");
        };
    }
}