package com.auca.cafeteria.repository;

import com.auca.cafeteria.enums.OrderStatus;
import com.auca.cafeteria.model.Order;
import com.auca.cafeteria.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStudentOrderByCreatedAtDesc(Student student);
    List<Order> findByStatusOrderByCreatedAtAsc(OrderStatus status);
    List<Order> findByStatusIn(List<OrderStatus> statuses);

    @Query("SELECT COUNT(o) FROM Order o WHERE DATE(o.createdAt) = CURRENT_DATE AND o.status IN (com.auca.cafeteria.enums.OrderStatus.PAID, com.auca.cafeteria.enums.OrderStatus.PREPARING, com.auca.cafeteria.enums.OrderStatus.READY, com.auca.cafeteria.enums.OrderStatus.COMPLETED)")
    long countTodayOrders();
}