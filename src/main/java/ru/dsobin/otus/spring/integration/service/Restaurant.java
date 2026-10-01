package ru.dsobin.otus.spring.integration.service;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import ru.dsobin.otus.spring.integration.model.Order;
import ru.dsobin.otus.spring.integration.model.ServedOrder;

/**
 * Точка входа в процесс. Реализацию генерирует Spring Integration:
 * вызов метода отправляет заказ в канал ordersChannel и ждёт ответ из потока.
 */
@MessagingGateway(defaultReplyTimeout = "5000")
public interface Restaurant {

    @Gateway(requestChannel = "ordersChannel")
    ServedOrder placeOrder(Order order);
}
