package ru.dsobin.otus.spring.integration.model;

import lombok.Value;

import java.util.List;

/**
 * Результат обработки заказа: что вынесли гостю.
 */
@Value
public class ServedOrder {
    long orderId;
    int table;
    List<Dish> dishes;
    /**
     * false, если заказ отклонён (например, пустой).
     */
    boolean accepted;

    public static ServedOrder served(long orderId, int table, List<Dish> dishes) {
        return new ServedOrder(orderId, table, dishes, true);
    }

    public static ServedOrder rejected(Order order) {
        return new ServedOrder(order.getId(), order.getTable(), List.of(), false);
    }
}
