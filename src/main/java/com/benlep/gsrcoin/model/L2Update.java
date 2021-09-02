package com.benlep.gsrcoin.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.benlep.gsrcoin.book.Order;

public class L2Update {
    private final LocalDateTime time;
    private final List<Order> asks;
    private final List<Order> bids;
    
    public L2Update(LocalDateTime time, List<Order> asks, List<Order> bids) {
        this.time = time;
        this.asks = asks;
        this.bids = bids;
    }

    public List<Order> getBids() {
        return bids;
    }

    public List<Order> getAsks() {
        return asks;
    }

    public LocalDateTime getTime() {
        return time;
    }
}
