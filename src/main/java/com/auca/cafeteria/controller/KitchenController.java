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
@RequestMapping("/kitchen")
public class KitchenController {

    private final OrderService orderService;

    @Autowired
    public KitchenController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Show PAID and PREPARING orders
        List<Order> orders = new ArrayList<>();
        orders.addAll(orderService.getOrdersByStatus(OrderStatus.PAID));
        orders.addAll(orderService.getOrdersByStatus(OrderStatus.PREPARING));
        model.addAttribute("orders", orders);
        model.addAttribute("pageTitle", "Kitchen Dashboard");
        return "kitchen/dashboard";
    }

    @PostMapping("/order/{orderId}/status")
    public String updateOrderStatus(@PathVariable Long orderId,
                                    @RequestParam OrderStatus status) {
        orderService.updateOrderStatus(orderId, status);
        return "redirect:/kitchen/dashboard";
    }
}