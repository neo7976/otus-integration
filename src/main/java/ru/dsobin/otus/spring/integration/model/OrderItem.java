package ru.dsobin.otus.spring.integration.model;

import lombok.Value;

/**
 * Позиция заказа: что заказал гость.
 */
@Value
public class OrderItem {
    String name;
    ItemType type;
}
