package com.bricksrus;

import javax.validation.constraints.Min;

/**
 * A request for a new order of bricks.
 */
public class CreateOrderRequest {
    // Seems silly to create an order for 0 bricks, and less is obviously illegal.
    @Min(1)
    private int numBricks;

    protected CreateOrderRequest() {
    }

    public CreateOrderRequest(int numBricks) {
        this.numBricks = numBricks;
    }

    public int getNumBricks() { return numBricks; }
}
