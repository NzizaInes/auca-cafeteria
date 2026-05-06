package com.auca.cafeteria.observer;

import com.auca.cafeteria.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class StudentNotificationObserver implements OrderObserver {

    private static final Logger logger =
            LoggerFactory.getLogger(StudentNotificationObserver.class);

    @Override
    public void update(Order order) {
        String studentName = order.getStudent().getName();
        String message;

        switch (order.getStatus()) {
            case PAID:
                message = "Dear " + studentName + ", your order #"
                        + order.getId() + " is confirmed! Queue: "
                        + order.getQueueNumber();
                break;
            case PREPARING:
                message = "Dear " + studentName
                        + ", your meal is being prepared!";
                break;
            case READY:
                message = "Dear " + studentName
                        + ", your meal is READY! Please collect now.";
                break;
            case COMPLETED:
                message = "Thank you " + studentName
                        + " for dining at AUCA Cafeteria!";
                break;
            case CANCELLED:
                message = "Dear " + studentName + ", your order #"
                        + order.getId() + " has been cancelled.";
                break;
            default:
                message = "Dear " + studentName + ", order status: "
                        + order.getStatus();
                break;
        }

        logger.info("[STUDENT NOTIFICATION] -> {}", message);
        sendNotification(order.getStudent().getEmail(), message);
    }

    private void sendNotification(String email, String message) {
        logger.debug("[NOTIFICATION SENT] To: {} | Message: {}", email, message);
    }
}