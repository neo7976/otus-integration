package ru.dsobin.otus.spring.integration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import ru.dsobin.otus.spring.integration.model.ItemType;
import ru.dsobin.otus.spring.integration.model.Order;
import ru.dsobin.otus.spring.integration.model.OrderItem;
import ru.dsobin.otus.spring.integration.model.ServedOrder;
import ru.dsobin.otus.spring.integration.service.Restaurant;

import java.util.List;

/**
 * Демонстрация: несколько гостей делают заказы через gateway.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.demo.enabled", havingValue = "true", matchIfMissing = true)
public class DemoRunner implements CommandLineRunner {

    private final Restaurant restaurant;

    @Override
    public void run(String... args) {
        List<Order> orders = List.of(
                new Order(1, 5, List.of(
                        new OrderItem("Борщ", ItemType.HOT),
                        new OrderItem("Салат Цезарь", ItemType.COLD),
                        new OrderItem("Морс", ItemType.DRINK))),
                new Order(2, 3, List.of(
                        new OrderItem("Капучино", ItemType.DRINK),
                        new OrderItem("Чизкейк", ItemType.COLD))),
                new Order(3, 7, List.of()));

        for (Order order : orders) {
            ServedOrder served = restaurant.placeOrder(order);
            log.info("Столик {}: {}", served.getTable(), served.isAccepted()
                    ? "вынесли " + served.getDishes().size() + " поз."
                    : "заказ отклонён");
        }
    }
}
