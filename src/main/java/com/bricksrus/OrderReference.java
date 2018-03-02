package com.bricksrus;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.annotation.Generated;
import javax.persistence.*;
import java.util.UUID;

/**
 * An OrderReference is created when an order comes in, and stores the number of bricks ordered.
 *
 * It's persisted to database.
 */
@Entity
public class OrderReference implements Comparable<OrderReference> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    // Why not use uuid?  They're generally better for databases (easy multi-threading), but these ids need to be
    // customer-facing and no-one wants to read out a UUID over the phone...
    // private UUID uuid;
    private int numBricks;

    protected OrderReference() {}

    public OrderReference(int numBricks) {
        this.numBricks = numBricks;
    }

    public int getId() { return id; }
    public int getNumBricks() { return numBricks; }
    public void setNumBricks(int numBricks) { this.numBricks = numBricks; }
    @JsonIgnore
    public JustOrderReference getJustOrderReference() { return new JustOrderReference(id); }

    @Override
    public String toString() {
        return String.format("uuid=%d bricks=%d", id, numBricks);
    }

    @Override
    public int compareTo(OrderReference ref) {
        return Integer.compare(id, ref.id);
    }
}
