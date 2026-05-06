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

    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt >= CURRENT_DATE")
    long countTodayOrders();
}