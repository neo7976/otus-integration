package ru.dsobin.otus.spring.integration.model;

import lombok.Value;

/**
 * Готовое блюдо или напиток.
 */
@Value
public class Dish {
    String name;
    ItemType type;
    /**
     * Кто приготовил: горячий цех, холодный цех или бар.
     */
    String preparedBy;
}
