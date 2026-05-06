package com.auca.cafeteria.observer;

import com.auca.cafeteria.model.Order;

public interface OrderSubject {
    void registerObserver(OrderObserver observer);
    void removeObserver(OrderObserver observer);
    void notifyObservers(Order order);
}