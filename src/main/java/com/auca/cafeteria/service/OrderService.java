package com.auca.cafeteria.service;

import com.auca.cafeteria.enums.OrderStatus;
import com.auca.cafeteria.model.MenuItem;
import com.auca.cafeteria.model.Order;
import com.auca.cafeteria.model.QueueEntry;
import com.auca.cafeteria.model.Student;
import com.auca.cafeteria.observer.OrderObserver;
import com.auca.cafeteria.observer.OrderSubject;
import com.auca.cafeteria.repository.OrderRepository;
import com.auca.cafeteria.repository.QueueEntryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderService implements OrderSubject {

    private static final Logger logger =
            LoggerFactory.getLogger(OrderService.class);

    private final List<OrderObserver> observers = new ArrayList<>();
    private final OrderRepository orderRepository;
    private final QueueEntryRepository queueEntryRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        QueueEntryRepository queueEntryRepository,
                        List<OrderObserver> observerList) {
        this.orderRepository = orderRepository;
        this.queueEntryRepository = queueEntryRepository;
        this.observers.addAll(observerList);
        logger.info("[ORDER SERVICE] {} observer(s) registered.", observers.size());
    }

    @Override
    public void registerObserver(OrderObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(OrderObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(Order order) {
        logger.info("[OBSERVER] Notifying {} observer(s) for Order #{}",
                observers.size(), order.getId());
        for (OrderObserver observer : observers) {
            observer.update(order);
        }
    }

    public Order placeOrder(Student student, List<MenuItem> items) {
        Order order = new Order();
        order.setStudent(student);
        order.setItems(items);
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());

        double total = 0;
        for (MenuItem item : items) {
            total += item.getPrice();
        }
        order.setTotalPrice(total);

        Order savedOrder = orderRepository.save(order);
        int queueNumber = assignQueueNumber(savedOrder);
        savedOrder.setQueueNumber(queueNumber);
        orderRepository.save(savedOrder);

        notifyObservers(savedOrder);
        return savedOrder;
    }

    public Order updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Order not found: " + orderId));
        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());
        Order updated = orderRepository.save(order);
        notifyObservers(updated);
        return updated;
    }

    public List<Order> getOrdersByStudent(Student student) {
        return orderRepository.findByStudentOrderByCreatedAtDesc(student);
    }

    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatusOrderByCreatedAtAsc(status);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Order not found: " + orderId));
    }

    private int assignQueueNumber(Order order) {
        long count = orderRepository.countTodayOrders();
        int queueNumber = (int) count + 1;
        QueueEntry entry = new QueueEntry();
        entry.setOrder(order);
        entry.setQueueNumber(queueNumber);
        entry.setEstimatedWaitMinutes(queueNumber * 5);
        queueEntryRepository.save(entry);
        return queueNumber;
    }
    public Order rejectPayment(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        order.setStatus(OrderStatus.REJECTED);
        order.setUpdatedAt(LocalDateTime.now());
        order = orderRepository.save(order);
        notifyObservers(order);
        return order;
    }
    public List<Order> getActiveQueueOrders() {
        return orderRepository.findByStatusIn(
                List.of(OrderStatus.PAID, OrderStatus.PREPARING, OrderStatus.READY)
        );
    }

    public Order savePaymentReference(Long orderId,
                                      String paymentMethod,
                                      String paymentReference) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Order not found: " + orderId));
        order.setPaymentMethod(paymentMethod);
        order.setPaymentReference(paymentReference);
        order.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }
}