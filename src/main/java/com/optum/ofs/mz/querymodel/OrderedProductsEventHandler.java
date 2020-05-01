package com.optum.ofs.mz.querymodel;

import com.optum.ofs.mz.coreapi.events.OrderConfirmedEvent;
import com.optum.ofs.mz.coreapi.events.OrderPlacedEvent;
import com.optum.ofs.mz.coreapi.events.OrderShippedEvent;
import com.optum.ofs.mz.coreapi.queries.FindAllOrderedProductsQuery;
import com.optum.ofs.mz.coreapi.queries.OrderedProduct;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderedProductsEventHandler {

    private final Map<String, OrderedProduct> orderedProducts = new HashMap<>();

    @EventHandler
    public void on(OrderPlacedEvent orderPlacedEvent){
        String orderId = orderPlacedEvent.getOrderId();
        orderedProducts.put(orderId, new OrderedProduct(orderPlacedEvent.getOrderId(), orderPlacedEvent.getProduct()));
    }

    @EventHandler
    public void on(OrderConfirmedEvent event) {
        orderedProducts.computeIfPresent(event.getOrderId(), (orderId, orderedProduct) -> {
            orderedProduct.setOrderConfirmed();
            return orderedProduct;
        });
    }

    @EventHandler
    public void on(OrderShippedEvent event) {
        orderedProducts.computeIfPresent(event.getOrderId(), (orderId, orderedProduct) -> {
            orderedProduct.setOrderShipped();
            return orderedProduct;
        });
    }

    @QueryHandler
    public List<OrderedProduct> handle(FindAllOrderedProductsQuery query) {
        return new ArrayList<>(orderedProducts.values());
    }
}
