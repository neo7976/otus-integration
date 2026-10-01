package ru.dsobin.otus.spring.integration.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.handler.LoggingHandler;
import ru.dsobin.otus.spring.integration.model.Dish;
import ru.dsobin.otus.spring.integration.model.ItemType;
import ru.dsobin.otus.spring.integration.model.Order;
import ru.dsobin.otus.spring.integration.model.OrderItem;
import ru.dsobin.otus.spring.integration.model.ServedOrder;
import ru.dsobin.otus.spring.integration.service.KitchenService;

import java.util.List;

/**
 * Процесс обработки заказа:
 * <pre>
 * Restaurant (gateway) -> ordersChannel
 *   -> filter: пустой заказ отклоняется (discardFlow сразу отвечает gateway)
 *   -> enrichHeaders: номер заказа и столика в заголовки
 *   -> split: заказ -> отдельные позиции
 *   -> route по типу позиции:
 *        HOT   -> subflow «Горячий цех»
 *        COLD  -> subflow «Холодный цех»
 *        DRINK -> subflow «Бар»
 *   -> aggregate: готовые блюда собираются обратно по заказу
 *   -> handle: List&lt;Dish&gt; -> ServedOrder, ответ уходит в gateway
 * </pre>
 */
@Configuration
public class KitchenFlowConfig {

    public static final String ORDER_ID_HEADER = "orderId";
    public static final String TABLE_HEADER = "table";

    @Bean
    public IntegrationFlow kitchenFlow(KitchenService kitchen) {
        return IntegrationFlows.from("ordersChannel")
                .log(LoggingHandler.Level.INFO, "order.received", m -> "Принят заказ: " + m.getPayload())
                .filter(Order.class, order -> !order.getItems().isEmpty(),
                        f -> f.discardFlow(df -> df
                                .log(LoggingHandler.Level.WARN, "order.rejected", m -> "Пустой заказ отклонён: " + m.getPayload())
                                .transform(Order.class, ServedOrder::rejected)))
                .enrichHeaders(h -> h
                        .headerFunction(ORDER_ID_HEADER, m -> ((Order) m.getPayload()).getId())
                        .headerFunction(TABLE_HEADER, m -> ((Order) m.getPayload()).getTable()))
                .split(Order.class, Order::getItems)
                .<OrderItem, ItemType>route(OrderItem::getType, r -> r
                        .subFlowMapping(ItemType.HOT, sf -> sf
                                .handle(OrderItem.class, (item, headers) -> kitchen.cookHot(item)))
                        .subFlowMapping(ItemType.COLD, sf -> sf
                                .handle(OrderItem.class, (item, headers) -> kitchen.prepareCold(item)))
                        .subFlowMapping(ItemType.DRINK, sf -> sf
                                .handle(OrderItem.class, (item, headers) -> kitchen.pourDrink(item))))
                .aggregate()
                .<List<Dish>>handle((dishes, headers) -> ServedOrder.served(
                        headers.get(ORDER_ID_HEADER, Long.class),
                        headers.get(TABLE_HEADER, Integer.class),
                        dishes))
                .log(LoggingHandler.Level.INFO, "order.served", m -> "Заказ готов: " + m.getPayload())
                .get();
    }
}
