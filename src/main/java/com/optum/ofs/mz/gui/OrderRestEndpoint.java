package com.optum.ofs.mz.gui;

import com.optum.ofs.mz.coreapi.commands.ConfirmOrderCommand;
import com.optum.ofs.mz.coreapi.commands.PlaceOrderCommand;
import com.optum.ofs.mz.coreapi.commands.ShipOrderCommand;
import com.optum.ofs.mz.coreapi.queries.FindAllOrderedProductsQuery;
import com.optum.ofs.mz.coreapi.queries.OrderedProduct;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class OrderRestEndpoint {
    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    public OrderRestEndpoint(CommandGateway commandGateway, QueryGateway queryGateway) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
    }

    @PostMapping("/ship-order")
    public void shipOrder(){
        String orderId = UUID.randomUUID().toString();
        commandGateway.send(new PlaceOrderCommand(orderId, "Deluxe Chair"));
        commandGateway.send(new ConfirmOrderCommand(orderId));
        commandGateway.send(new ShipOrderCommand(orderId));
    }

    @PostMapping("/ship-unconfirmed-order")
    public void shipUnconfirmedOrder() {
        String orderId = UUID.randomUUID().toString();
        commandGateway.send(new PlaceOrderCommand(orderId, "Deluxe Chair"));
        // This throws an exception, as an Order cannot be shipped if it has not been confirmed yet.
        commandGateway.send(new ShipOrderCommand(orderId));
    }

    @GetMapping("/all-orders")
    public List<OrderedProduct> findAllOrderedProducts(){
        return queryGateway.query(new FindAllOrderedProductsQuery(), ResponseTypes.multipleInstancesOf(OrderedProduct.class)).join();
    }
}
