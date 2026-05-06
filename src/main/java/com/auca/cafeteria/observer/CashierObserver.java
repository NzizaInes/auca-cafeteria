package com.auca.cafeteria.observer;

import com.auca.cafeteria.enums.OrderStatus;
import com.auca.cafeteria.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CashierObserver implements OrderObserver {

    private static final Logger logger =
            LoggerFactory.getLogger(CashierObserver.class);

    @Override
    public void update(Order order) {
        if (order.getStatus() == OrderStatus.PENDING) {
            logger.info("[CASHIER ALERT] Order #{} from {} awaiting payment. Amount: {} RWF",
                    order.getId(),
                    order.getStudent().getName(),
                    order.getTotalPrice());
        }
    }
}