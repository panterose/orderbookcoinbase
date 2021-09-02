package com.benlep.gsrcoin.book;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentSkipListMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderBook {
    private static final Logger LOG = LoggerFactory.getLogger(OrderBook.class);
    
    private final SortedMap<Double, Order> asks = new ConcurrentSkipListMap<>(Comparator.naturalOrder());
    private final SortedMap<Double, Order> bids = new ConcurrentSkipListMap<>(Comparator.reverseOrder());

    private final int levels;
    private final String instrument;

    public OrderBook(String instrument, int levels) {
        this.instrument = instrument;
        this.levels = levels;
    }

    public SortedMap<Double, Order> getAsks() {
        return asks;
    }

    public SortedMap<Double, Order> getBids() {
        return bids;
    }

    public int getLevels() {
        return levels;
    }

    public void add(Order order) {
        if (order.isBuy()) {
            add(bids, order);
        } else {
            add(asks, order);
        }
    }

    private void add(SortedMap<Double, Order> orders, Order order) {
        Order previous = orders.putIfAbsent(order.getPrice() , order);
        if (previous != null) {
            previous.addQuantity(order.getQuantity());
        }
        
        cleanOrders(orders);
    }

    private void cleanOrders(SortedMap<Double, Order> orders) {
        //clean 0 orders
        for (Double price: orders.keySet()) {
            if (orders.get(price).getQuantity() == 0) {
                orders.remove(price);
            }
        }

        // remove if too long
        if (orders.size() > levels) {
            Double toBeRemoved = orders.lastKey();
            orders.remove(toBeRemoved);
        }
    }

	public void print() {
        LOG.info("OrderBook for {} on {} levels", instrument, levels);
        for (Order bid: bids.values()) {
            LOG.info("++ bid: p={}/q={}", bid.getPrice(), bid.getQuantity());
        }
        for (Order ask: asks.values()) {
            LOG.info("-- ask: p={}/q={}", ask.getPrice(), ask.getQuantity());
        }
	}
    
}
