package com.bricksrus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Handles operations on OrderReferences: creation, getting, etc.
 */
@Service
public class OrderService
{
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderReferenceRepository orderReferences;

    /**
     * Create a new order.  request.numBricks must be >= 1
     */
    public OrderReference createOrder(CreateOrderRequest request) throws IllegalArgumentException {
        // This should have been validated before with @Valid
        if (request.getNumBricks() < 1) {
            throw new IllegalArgumentException("invalid number of bricks");
        }

        OrderReference orderReference = new OrderReference(request.getNumBricks());
        orderReferences.save(orderReference);

        log.info(String.format("Received order request {%s}, created ref {%s}", request, orderReference));

        return orderReference;
    }
}
