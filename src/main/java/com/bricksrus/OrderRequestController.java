package com.bricksrus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST service to handle all order requests, and return order references.
 * Most of the real logic is in OrderService, for easier unit-testing.
 */
@RestController
class OrderRequestController {
    static final String ENDPOINT_ORDER = "/api/order/";
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderService orderService;

    /**
     * Create a new order from POST body containing JSON {"numBricks":XXXX}
     * numBricks must be >= 1 or a 4XX error will be returned.
     *
     * @return JSON containing the order reference, in the form {"id":XXXX}
     */
    @RequestMapping(value = ENDPOINT_ORDER, method = RequestMethod.POST)
    public ResponseEntity<JustOrderReference> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        OrderReference ref = orderService.createOrder(request);
        JustOrderReference ret = ref.getJustOrderReference();
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    /**
     * Given an order reference id, returns the corresponding order details.
     *
     * Order reference id must be for a valid existing order, or a 4XX error will be returned.
     *
     * @return JSON containing the order details, in the form {"id":XXXX,"numBricks":XXXX}
     */
    @RequestMapping(value = ENDPOINT_ORDER + "{id}", method = RequestMethod.GET)
    public ResponseEntity<OrderReference> getOrder(@PathVariable("id") int id) {
        Optional<OrderReference> ref = orderService.findById(id);

        if (!ref.isPresent()) {
            String toLog = "Could not find order reference " + id;
            log.warn(toLog);

            // Spec says 'no order details are returned' but let's try and provide a bit of helpful debug too
            return new ResponseEntity(toLog,HttpStatus.BAD_REQUEST);
        }
        else {
            return new ResponseEntity<>(ref.get(), HttpStatus.OK);
        }
    }

    /**
     * Returns order details for all existing orders.
     *
     * @return JSON containing the order details, in the form [{"id":XXX,"numBricks":XXX},...]
     */
    @RequestMapping(value = ENDPOINT_ORDER, method = RequestMethod.GET)
    public ResponseEntity<List<OrderReference>> listAllOrders() {
        List<OrderReference> refs = orderService.findAll();
        return new ResponseEntity<>(refs, HttpStatus.OK);
    }

    /**
     * Updates an existing order reference with a new value of bricks.
     *
     * Order reference id must be for a valid existing order, or a 4XX error will be returned.
     * numBricks must be >= 1 or a 4XX error will be returned.
     *
     * @return JSON containing the order details, in the form {"id":XXXX,"numBricks":XXXX}
     */
    @RequestMapping(value = ENDPOINT_ORDER + "{id}", method = RequestMethod.PUT)
    public ResponseEntity<JustOrderReference> updateOrder(@PathVariable("id") int id, @Valid @RequestBody UpdateOrderRequest request) {
        // Implementation note: the spec:
        // 'an Order reference the returned
        //	And the Order reference is unique to the submission'
        // is unclear to me here.  The latter part implies that a new order reference with a unique id should be created
        // for this, but generally you'd expect this method to simply update the existing order.  Going to assume this is
        // a C&P thing in the spec and go with updating the existing order.

        Optional<OrderReference> ref = orderService.updateOrder(id, request);

        if (!ref.isPresent()) {
            String toLog = "Could not find order reference " + id;
            log.warn(toLog);

            // Spec says 'no order details are returned' but let's try and provide a bit of helpful debug too
            return new ResponseEntity(toLog, HttpStatus.BAD_REQUEST);
        }
        else {
            JustOrderReference out = ref.get().getJustOrderReference();
            return new ResponseEntity<>(out, HttpStatus.OK);
        }
    }

}
