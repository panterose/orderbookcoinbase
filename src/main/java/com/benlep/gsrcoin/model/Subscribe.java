package com.benlep.gsrcoin.model;

public class Subscribe {
    public final String type = "subscribe";

	public final String[] product_ids;
	public final Channel[] channels;

    public Subscribe(String[] product_ids, Channel[] channels) {
        this.product_ids = product_ids;
        this.channels = channels;
    }


}
