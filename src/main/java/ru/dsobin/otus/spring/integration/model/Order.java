package ru.dsobin.otus.spring.integration.model;

import lombok.Value;

import java.util.List;

/**
 * Заказ гостя за столиком.
 */
@Value
public class Order {
    long id;
    int table;
    List<OrderItem> items;
}
