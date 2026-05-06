package com.auca.cafeteria.observer;

import com.auca.cafeteria.model.Order;

public interface OrderObserver {
    void update(Order order);
}