package com.auca.cafeteria.controller;

import com.auca.cafeteria.enums.OrderStatus;
import com.auca.cafeteria.model.Order;
import com.auca.cafeteria.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/cashier")
public class CashierController {

    private final OrderService orderService;

    @Autowired
    public CashierController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Order> orders =
                orderService.getOrdersByStatus(OrderStatus.PENDING);
        model.addAttribute("orders", orders);
        model.addAttribute("pageTitle", "Cashier Dashboard");
        return "cashier/dashboard";
    }

    @GetMapping("/confirmed")
    public String confirmedPayments(Model model) {
        // Show ALL orders that have passed the payment stage
        List<Order> allOrders = orderService.getAllOrders();
        List<Order> confirmedOrders = new ArrayList<>();

        for (Order order : allOrders) {
            OrderStatus status = order.getStatus();
            if (status == OrderStatus.PAID
                    || status == OrderStatus.PREPARING
                    || status == OrderStatus.READY
                    || status == OrderStatus.COMPLETED) {
                confirmedOrders.add(order);
            }
        }

        model.addAttribute("confirmedOrders", confirmedOrders);
        model.addAttribute("pageTitle", "Confirmed Payments");
        return "cashier/confirmed";
    }

    @PostMapping("/order/{orderId}/confirm")
    public String confirmPayment(@PathVariable Long orderId) {
        orderService.updateOrderStatus(orderId, OrderStatus.PAID);
        return "redirect:/cashier/dashboard";
    }
}