package com.bricksrus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Handles operations on OrderReferences: creation, getting, etc.
 */
@Service
public class OrderService {
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

    /**
     * Returns the OrderReference for an existing order.
     *
     * @param id A valid order reference id
     * @return an Optional containing None if an invalid order reference was provided, else the corresponding OrderReference.
     */
    public Optional<OrderReference> findById(int id) {
        OrderReference out = orderReferences.findOne(id);
        return out == null ? Optional.empty() : Optional.of(out);
    }

    /**
     * Returns all existing OrderReferences as a List, sorted by their ids.
     */
    public List<OrderReference> findAll() {
        Iterable<OrderReference> refs = orderReferences.findAll();
        List<OrderReference> out = new ArrayList<>();
        refs.forEach(ref -> out.add(ref));
        Collections.sort(out);
        return out;
    }

    /**
     * Updates an existing order with a new quantity of bricks.
     *
     * @return an Optional containing None if an invalid order reference was provided, else the corresponding OrderReference,
     *  updated with the new values.
     */
    public Optional<OrderReference> updateOrder(int id, UpdateOrderRequest request) {
        Optional<OrderReference> orderReferenceOpt = findById(id);

        if (orderReferenceOpt.isPresent()) {
            OrderReference ref = orderReferenceOpt.get();

            ref.setNumBricks(request.getNumBricks());
            orderReferences.save(ref);

            log.info(String.format("Updating order {%d}, to {%s}", id, request));

            return Optional.of(ref);
        }
        else {
            return orderReferenceOpt;
        }
    }
}
