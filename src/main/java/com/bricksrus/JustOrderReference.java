package com.bricksrus;

/**
 * On creating an order we just want to return the order reference to the user.
 */
public class JustOrderReference {
    private int id;

    protected JustOrderReference() {}

    public JustOrderReference(int id) {
        this.id = id;
    }

    public long getId() { return id; }
}
