
package com.bricksrus;

import javax.validation.constraints.Min;

/**
 * A request to update an existing order request with a different number of bricks.
 */
public class UpdateOrderRequest {
    // Seems silly to create an order for 0 bricks, and less is obviously illegal.
    @Min(1)
    private int numBricks;

    protected UpdateOrderRequest() {
    }

    public UpdateOrderRequest(int numBricks) {
        this.numBricks = numBricks;
    }

    public int getNumBricks() { return numBricks; }

    @Override
    public String toString() {
        return "numBricks=" + numBricks;
    }
}
