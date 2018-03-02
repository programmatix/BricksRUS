package com.bricksrus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
        OrderReference ref = orderService.findById(id);

        if (ref == null) {
            String toLog = "Could not find order reference " + id;
            log.warn(toLog);

            // Spec says 'no order details are returned' but let's try and provide a bit of helpful debug too
            return new ResponseEntity(toLog,HttpStatus.BAD_REQUEST);
        }
        else {
            return new ResponseEntity<>(ref, HttpStatus.OK);
        }
    }


}
