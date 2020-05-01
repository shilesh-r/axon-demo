package com.optum.ofs.mz.commandmodel;

import com.optum.ofs.mz.coreapi.commands.ConfirmOrderCommand;
import com.optum.ofs.mz.coreapi.commands.PlaceOrderCommand;
import com.optum.ofs.mz.coreapi.commands.ShipOrderCommand;
import com.optum.ofs.mz.coreapi.events.OrderConfirmedEvent;
import com.optum.ofs.mz.coreapi.events.OrderPlacedEvent;
import com.optum.ofs.mz.coreapi.events.OrderShippedEvent;
import com.optum.ofs.mz.coreapi.exceptions.UnconfirmedOrderException;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
public class OrderAggregate {

    @AggregateIdentifier
    private String orderId;
    private boolean orderConfirmed;

    @CommandHandler
    public OrderAggregate(PlaceOrderCommand placeOrderCommand){
        apply(new OrderPlacedEvent(placeOrderCommand.getOrderId(), placeOrderCommand.getProduct()));
    }

    @CommandHandler
    public void handle(ConfirmOrderCommand confirmOrderCommand){
        apply(new OrderConfirmedEvent(confirmOrderCommand.getOrderId()));
    }

    @CommandHandler
    public void handle(ShipOrderCommand shipOrderCommand){
        if(!orderConfirmed){
            throw new UnconfirmedOrderException();
        }
        apply(new OrderShippedEvent(shipOrderCommand.getOrderId()));
    }

    @EventSourcingHandler
    public void on(OrderPlacedEvent orderPlacedEvent){
        this.orderId = orderPlacedEvent.getOrderId();
        this.orderConfirmed = false;
    }

    @EventSourcingHandler
    public void on(OrderConfirmedEvent orderConfirmedEvent){
        this.orderConfirmed = true;
    }

    protected OrderAggregate(){

    }

}
