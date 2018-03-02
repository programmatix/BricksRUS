package com.bricksrus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * REST service to handle all order requests, and return order references.
 */
@RestController
class OrderRequestController {
    static final String ENDPOINT_ORDER = "/api/order/";

    @Autowired
    private OrderService orderService;

    /**
     * Create a new order from POST body containing JSON {"numBricks":XXXX}
     * numBricks must be >= 1 or a 4XX error will be returned.
     *
     * Test with:
     * curl -X POST -H "Content-Type: application/json" -d '{"numBricks":10}' http://localhost:8080/api/order/
     *
     * @return JSON containing the order reference, in the form {"id":XXXX}
     */
    @RequestMapping(path = ENDPOINT_ORDER, method = RequestMethod.POST)
    public ResponseEntity<JustOrderReference> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        OrderReference ref = orderService.createOrder(request);
        JustOrderReference ret = ref.getJustOrderReference();
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }
}
