package com.auca.cafeteria.observer;

import com.auca.cafeteria.enums.OrderStatus;
import com.auca.cafeteria.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class KitchenObserver implements OrderObserver {

    private static final Logger logger =
            LoggerFactory.getLogger(KitchenObserver.class);

    @Override
    public void update(Order order) {
        if (order.getStatus() == OrderStatus.PAID) {
            logger.info("[KITCHEN ALERT] New order #{} received. Items: {}",
                    order.getId(),
                    order.getItems().size() + " item(s)");
        }
    }
}