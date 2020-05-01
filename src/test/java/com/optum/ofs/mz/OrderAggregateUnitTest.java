package com.optum.ofs.mz;

import com.optum.ofs.mz.commandmodel.OrderAggregate;
import com.optum.ofs.mz.coreapi.commands.ConfirmOrderCommand;
import com.optum.ofs.mz.coreapi.commands.PlaceOrderCommand;
import com.optum.ofs.mz.coreapi.commands.ShipOrderCommand;
import com.optum.ofs.mz.coreapi.events.OrderConfirmedEvent;
import com.optum.ofs.mz.coreapi.events.OrderPlacedEvent;
import com.optum.ofs.mz.coreapi.events.OrderShippedEvent;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

public class OrderAggregateUnitTest {

    private AggregateTestFixture<OrderAggregate> fixture;

    @Before
    public void setUp(){
        fixture = new AggregateTestFixture<>(OrderAggregate.class);
    }

    @Test
    public void givenNoPriorActivity_WhenNewOrderIsPlaced_ThenExpectOrderPlacedEvent(){
        String orderId = UUID.randomUUID().toString();
        String product = "Deluxe Chair";
        fixture.givenNoPriorActivity()
                .when(new PlaceOrderCommand(orderId,product))
                .expectEvents(new OrderPlacedEvent(orderId,product));

    }

    @Test
    public void givenOrderPlacedEvent_whenConfirmOrderCommand_thenShouldPublishOrderConfirmedEvent() {
        String orderId = UUID.randomUUID().toString();
        String product = "Deluxe Chair";
        fixture.given(new OrderPlacedEvent(orderId, product))
                .when(new ConfirmOrderCommand(orderId))
                .expectEvents(new OrderConfirmedEvent(orderId));
    }

    @Test
    public void givenOrderPlacedEvent_WhenShipOrderCommand_ExpectException(){
        String orderId = UUID.randomUUID().toString();
        String product = "Deluxe Chair";
        fixture.given(new OrderPlacedEvent(orderId, product))
                .when(new ShipOrderCommand(orderId))
                .expectException(IllegalStateException.class);
    }

    @Test
    public void givenOrderPlacedEventOrderConfirmedEvent_WhenShipOrderCommand_ExpectOrderShippedEvent(){
        String orderId = UUID.randomUUID().toString();
        String product = "Deluxe Chair";
        fixture.given(new OrderPlacedEvent(orderId,product), new OrderConfirmedEvent(orderId))
                .when(new ShipOrderCommand(orderId))
                .expectEvents(new OrderShippedEvent(orderId));
    }
}
