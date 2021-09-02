package com.benlep.gsrcoin.model;

public class Channel {
    public final String name;
	public final String[] product_ids;

    public Channel(String name) {
        this(name, null);
    }

    public Channel(String name, String[] product_ids) {
        this.name = name;
        this.product_ids = product_ids;
    }
}
