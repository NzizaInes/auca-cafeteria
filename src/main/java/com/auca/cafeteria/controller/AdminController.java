package com.auca.cafeteria.controller;

import com.auca.cafeteria.enums.OrderStatus;
import com.auca.cafeteria.model.MenuItem;
import com.auca.cafeteria.model.Order;
import com.auca.cafeteria.service.MenuService;
import com.auca.cafeteria.service.OrderService;
import com.auca.cafeteria.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.auca.cafeteria.model.Admin;
import com.auca.cafeteria.model.Cashier;
import com.auca.cafeteria.model.KitchenStaff;
import com.auca.cafeteria.model.Student;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final MenuService menuService;
    private final OrderService orderService;
    private final UserService userService;

    @Autowired
    public AdminController(MenuService menuService,
                           OrderService orderService,
                           UserService userService) {
        this.menuService = menuService;
        this.orderService = orderService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalOrders",
                orderService.getAllOrders().size());
        model.addAttribute("totalUsers",
                userService.getAllUsers().size());
        model.addAttribute("menuItems",
                menuService.getAllMenuItems());
        model.addAttribute("pageTitle", "Admin Dashboard");
        return "admin/dashboard";
    }

    @GetMapping("/menu")
    public String manageMenu(Model model) {
        List<MenuItem> menuItems = menuService.getAllMenuItems();

        int availableCount = 0;
        int unavailableCount = 0;
        for (MenuItem item : menuItems) {
            if (item.getAvailable()) {
                availableCount++;
            } else {
                unavailableCount++;
            }
        }

        model.addAttribute("menuItems", menuItems);
        model.addAttribute("availableCount", availableCount);
        model.addAttribute("unavailableCount", unavailableCount);
        model.addAttribute("newItem", new MenuItem());
        model.addAttribute("pageTitle", "Manage Menu");
        return "admin/menu";
    }

    @PostMapping("/menu/save")
    public String saveMenuItem(@ModelAttribute MenuItem item) {
        if (item.getAvailable() == null) {
            item.setAvailable(true);
        }
        menuService.saveMenuItem(item);
        return "redirect:/admin/menu";
    }

    @PostMapping("/menu/{itemId}/toggle")
    public String toggleItem(@PathVariable Long itemId) {
        menuService.toggleAvailability(itemId);
        return "redirect:/admin/menu";
    }

    @PostMapping("/menu/{itemId}/delete")
    public String deleteMenuItem(@PathVariable Long itemId) {
        menuService.deleteMenuItem(itemId);
        return "redirect:/admin/menu";
    }

    @GetMapping("/users")
    public String manageUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("pageTitle", "Manage Users");
        return "admin/users";
    }

    @GetMapping("/reports")
    public String viewReports(Model model) {
        List<Order> allOrders = orderService.getAllOrders();

        double totalRevenue = 0;
        int pendingOrders = 0;
        for (Order order : allOrders) {
            if (order.getTotalPrice() != null) {
                totalRevenue += order.getTotalPrice();
            }
            if (order.getStatus() == OrderStatus.PENDING) {
                pendingOrders++;
            }
        }

        model.addAttribute("allOrders", allOrders);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("pendingOrders", pendingOrders);
        model.addAttribute("pageTitle", "Reports & Analytics");
        return "admin/reports";
    }
    @GetMapping("/menu/{itemId}/edit")
    public String editMenuItem(@PathVariable Long itemId, Model model) {
        MenuItem item = menuService.getMenuItemById(itemId);
        model.addAttribute("item", item);
        model.addAttribute("pageTitle", "Edit Menu Item");
        return "admin/edit-menu-item";
    }

    @PostMapping("/menu/{itemId}/update")
    public String updateMenuItem(@PathVariable Long itemId,
                                 @ModelAttribute MenuItem updatedItem) {
        MenuItem existing = menuService.getMenuItemById(itemId);
        existing.setName(updatedItem.getName());
        existing.setDescription(updatedItem.getDescription());
        existing.setPrice(updatedItem.getPrice());
        existing.setCategory(updatedItem.getCategory());
        menuService.saveMenuItem(existing);
        return "redirect:/admin/menu";
    }
    @GetMapping("/users/add")
    public String addUserPage(Model model) {
        model.addAttribute("pageTitle", "Add User");
        return "admin/add-user";
    }

    @PostMapping("/users/save")
    public String saveUser(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String role,
            @RequestParam(required = false) String studentId,
            @RequestParam(required = false) String faculty,
            @RequestParam(required = false) String staffId,
            @RequestParam(required = false) String shift,
            @RequestParam(required = false) String cashierId,
            @RequestParam(required = false) String department) {

        // Check if email already exists
        if (userService.emailExists(email)) {
            return "redirect:/admin/users?error=Email already exists";
        }

        switch (role) {
            case "STUDENT":
                Student student = new Student();
                student.setName(name);
                student.setEmail(email);
                student.setPassword(password);
                student.setStudentId(studentId);
                student.setFaculty(faculty);
                userService.registerUser(student);
                break;
            case "KITCHEN_STAFF":
                KitchenStaff kitchen = new KitchenStaff();
                kitchen.setName(name);
                kitchen.setEmail(email);
                kitchen.setPassword(password);
                kitchen.setStaffId(staffId);
                kitchen.setShift(shift);
                userService.registerUser(kitchen);
                break;
            case "CASHIER":
                Cashier cashier = new Cashier();
                cashier.setName(name);
                cashier.setEmail(email);
                cashier.setPassword(password);
                cashier.setCashierId(cashierId);
                cashier.setShift(shift);
                userService.registerUser(cashier);
                break;
            case "ADMIN":
                Admin admin = new Admin();
                admin.setName(name);
                admin.setEmail(email);
                admin.setPassword(password);
                admin.setDepartment(department);
                admin.setAccessLevel(1);
                userService.registerUser(admin);
                break;
        }

        return "redirect:/admin/users?success=User added successfully";
    }
}