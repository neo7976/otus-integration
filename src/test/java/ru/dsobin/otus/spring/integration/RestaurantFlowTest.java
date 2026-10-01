package ru.dsobin.otus.spring.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.dsobin.otus.spring.integration.model.Dish;
import ru.dsobin.otus.spring.integration.model.ItemType;
import ru.dsobin.otus.spring.integration.model.Order;
import ru.dsobin.otus.spring.integration.model.OrderItem;
import ru.dsobin.otus.spring.integration.model.ServedOrder;
import ru.dsobin.otus.spring.integration.service.Restaurant;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static ru.dsobin.otus.spring.integration.service.KitchenService.BAR;
import static ru.dsobin.otus.spring.integration.service.KitchenService.COLD_SHOP;
import static ru.dsobin.otus.spring.integration.service.KitchenService.HOT_SHOP;

@SpringBootTest(properties = "app.demo.enabled=false")
class RestaurantFlowTest {

    @Autowired
    private Restaurant restaurant;

    @Test
    @DisplayName("Каждая позиция готовится в своём цехе, заказ собирается обратно целиком")
    void orderIsSplitRoutedAndAggregated() {
        Order order = new Order(10, 4, List.of(
                new OrderItem("Стейк", ItemType.HOT),
                new OrderItem("Оливье", ItemType.COLD),
                new OrderItem("Лимонад", ItemType.DRINK)));

        ServedOrder served = restaurant.placeOrder(order);

        assertThat(served.isAccepted()).isTrue();
        assertThat(served.getOrderId()).isEqualTo(10);
        assertThat(served.getTable()).isEqualTo(4);
        assertThat(served.getDishes())
                .extracting(Dish::getName, Dish::getPreparedBy)
                .containsExactlyInAnyOrder(
                        tuple("Стейк", HOT_SHOP),
                        tuple("Оливье", COLD_SHOP),
                        tuple("Лимонад", BAR));
    }

    @Test
    @DisplayName("Пустой заказ отклоняется фильтром")
    void emptyOrderIsRejected() {
        ServedOrder served = restaurant.placeOrder(new Order(11, 2, List.of()));

        assertThat(served.isAccepted()).isFalse();
        assertThat(served.getDishes()).isEmpty();
    }
}
