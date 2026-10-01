package ru.dsobin.otus.spring.integration.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.dsobin.otus.spring.integration.model.Dish;
import ru.dsobin.otus.spring.integration.model.OrderItem;

/**
 * Цеха кухни. Каждый метод вызывается из своего подпотока IntegrationFlow.
 */
@Slf4j
@Service
public class KitchenService {

    public static final String HOT_SHOP = "Горячий цех";
    public static final String COLD_SHOP = "Холодный цех";
    public static final String BAR = "Бар";

    public Dish cookHot(OrderItem item) {
        return prepare(item, HOT_SHOP);
    }

    public Dish prepareCold(OrderItem item) {
        return prepare(item, COLD_SHOP);
    }

    public Dish pourDrink(OrderItem item) {
        return prepare(item, BAR);
    }

    private Dish prepare(OrderItem item, String shop) {
        log.info("{}: готовим «{}»", shop, item.getName());
        return new Dish(item.getName(), item.getType(), shop);
    }
}
