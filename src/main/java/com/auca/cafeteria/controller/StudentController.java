package com.auca.cafeteria.controller;

import com.auca.cafeteria.model.MenuItem;
import com.auca.cafeteria.model.Order;
import com.auca.cafeteria.model.Student;
import com.auca.cafeteria.service.MenuService;
import com.auca.cafeteria.service.OrderService;
import com.auca.cafeteria.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final MenuService menuService;
    private final OrderService orderService;
    private final UserService userService;

    // In-memory cart
    private final List<Long> cartItemIds = new ArrayList<>();

    @Autowired
    public StudentController(MenuService menuService,
                             OrderService orderService,
                             UserService userService) {
        this.menuService = menuService;
        this.orderService = orderService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {
        String email = auth.getName();
        Student student = userService.findStudentByEmail(email);
        List<Order> orders = orderService.getOrdersByStudent(student);

        int totalOrders = orders.size();
        int activeOrders = 0;
        int completedOrders = 0;

        for (Order order : orders) {
            com.auca.cafeteria.enums.OrderStatus status = order.getStatus();
            if (status == com.auca.cafeteria.enums.OrderStatus.COMPLETED
                    || status == com.auca.cafeteria.enums.OrderStatus.READY) {
                // READY and COMPLETED both count as done
                completedOrders++;
            } else if (status == com.auca.cafeteria.enums.OrderStatus.CANCELLED) {
                // Cancelled orders don't count as active
            } else {
                // PENDING, PAID, PREPARING count as active
                activeOrders++;
            }
        }

        model.addAttribute("orders", orders);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("activeOrders", activeOrders);
        model.addAttribute("completedOrders", completedOrders);
        model.addAttribute("pageTitle", "Student Dashboard");
        return "student/dashboard";
    }

    @GetMapping("/menu")
    public String browseMenu(Model model) {
        List<MenuItem> menuItems = menuService.getAvailableMenuItems();

        // Build cart items list for display
        List<MenuItem> cartItems = new ArrayList<>();
        double cartTotal = 0;

        for (Long id : cartItemIds) {
            try {
                MenuItem item = menuService.getMenuItemById(id);
                cartItems.add(item);
                cartTotal += item.getPrice();
            } catch (Exception e) {
                // Skip items that no longer exist
            }
        }

        model.addAttribute("menuItems", menuItems);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartCount", cartItems.size());
        model.addAttribute("cartTotal", cartTotal);
        model.addAttribute("pageTitle", "Browse Menu");
        return "student/menu";
    }

    @PostMapping("/order/add")
    public String addToCart(@RequestParam Long itemId) {
        cartItemIds.add(itemId);
        return "redirect:/student/menu";
    }

    @PostMapping("/order/remove")
    public String removeFromCart(@RequestParam int index) {
        if (index >= 0 && index < cartItemIds.size()) {
            cartItemIds.remove(index);
        }
        return "redirect:/student/menu";
    }

    @PostMapping("/order/clear")
    public String clearCart() {
        cartItemIds.clear();
        return "redirect:/student/menu";
    }

    @PostMapping("/order/place")
    public String placeOrder(Authentication auth) {
        String email = auth.getName();
        Student student = userService.findStudentByEmail(email);

        List<MenuItem> items = new ArrayList<>();
        for (Long id : cartItemIds) {
            items.add(menuService.getMenuItemById(id));
        }

        if (!items.isEmpty()) {
            Order order = orderService.placeOrder(student, items);
            cartItemIds.clear();
            // Redirect to payment page instead of confirmation
            return "redirect:/student/payment/" + order.getId();
        }
        return "redirect:/student/menu";
    }

    @GetMapping("/payment/{orderId}")
    public String paymentPage(@PathVariable Long orderId, Model model) {
        Order order = orderService.getOrderById(orderId);
        model.addAttribute("order", order);
        model.addAttribute("pageTitle", "Make Payment");
        return "student/payment";
    }

    @PostMapping("/payment/submit")
    public String submitPayment(
            @RequestParam Long orderId,
            @RequestParam String paymentMethod,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String momoNetwork,
            @RequestParam(required = false) String cardNumber,
            @RequestParam(required = false) String cardHolder,
            @RequestParam(required = false) String expiryDate) {

        // Build payment reference string
        String paymentReference = "";
        if ("MOBILE_MONEY".equals(paymentMethod)) {
            paymentReference = momoNetwork + ": " + phoneNumber;
        } else if ("BANK_CARD".equals(paymentMethod)) {
            // Only show last 4 digits for security
            String maskedCard = "****-****-****-";
            if (cardNumber != null && cardNumber.length() >= 4) {
                maskedCard += cardNumber.replaceAll("\\s", "")
                        .substring(cardNumber.replaceAll("\\s", "").length() - 4);
            }
            paymentReference = cardHolder + " | " + maskedCard + " | Exp: " + expiryDate;
        } else {
            paymentReference = "Cash payment at counter";
        }

        // Save payment reference to order
        orderService.savePaymentReference(orderId, paymentMethod, paymentReference);

        return "redirect:/student/order/confirm/" + orderId;
    }

    @GetMapping("/order/confirm/{orderId}")
    public String orderConfirmation(@PathVariable Long orderId,
                                    Model model) {
        Order order = orderService.getOrderById(orderId);
        model.addAttribute("order", order);
        model.addAttribute("pageTitle", "Order Confirmation");
        return "student/order-confirmation";
    }

    @GetMapping("/order/track")
    public String trackOrder(Model model, Authentication auth) {
        String email = auth.getName();
        Student student = userService.findStudentByEmail(email);
        List<Order> orders = orderService.getOrdersByStudent(student);
        model.addAttribute("orders", orders);
        model.addAttribute("pageTitle", "Track Order");
        return "student/track-order";
    }
}