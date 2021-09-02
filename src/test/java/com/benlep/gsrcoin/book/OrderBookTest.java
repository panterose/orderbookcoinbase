package com.benlep.gsrcoin.book;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class OrderBookTest {
    @Test
    public void addBid() {
        OrderBook book = new OrderBook(2);
        
        book.add(new Order(true, 1.0,1.0));

        assertEquals(0, book.getAsks().size());
        assertEquals(1, book.getBids().size());
        assertEquals(true, book.getBids().get(1.0).isBuy());
        assertEquals(1.0, book.getBids().get(1.0).getPrice());
        assertEquals(1.0, book.getBids().get(1.0).getQuantity());

        book.add(new Order(true, 1.0, 2.0));

        assertEquals(1, book.getBids().size());
        assertEquals(true, book.getBids().get(1.0).isBuy());
        assertEquals(1.0, book.getBids().get(1.0).getPrice());
        assertEquals(3.0, book.getBids().get(1.0).getQuantity());

        book.add(new Order(true, 2.0, 1.0));

        assertEquals(2, book.getBids().size());
        assertEquals(true, book.getBids().get(1.0).isBuy());
        assertEquals(1.0, book.getBids().get(1.0).getPrice());
        assertEquals(3.0, book.getBids().get(1.0).getQuantity());
        assertEquals(true, book.getBids().get(2.0).isBuy());
        assertEquals(2.0, book.getBids().get(2.0).getPrice());
        assertEquals(1.0, book.getBids().get(2.0).getQuantity());

        assertEquals(2.0, book.getBids().firstKey());
        assertEquals(1.0, book.getBids().lastKey());
    }

    @Test
    public void addAsk() {
        OrderBook book = new OrderBook(2);
        
        book.add(new Order(false, 1.0,1.0));

        assertEquals(0, book.getBids().size());
        assertEquals(1, book.getAsks().size());

        assertEquals(false, book.getAsks().get(1.0).isBuy());
        assertEquals(1.0, book.getAsks().get(1.0).getPrice());
        assertEquals(1.0, book.getAsks().get(1.0).getQuantity());

        book.add(new Order(false, 2.0, 1.0));
        assertEquals(2, book.getAsks().size());
        assertEquals(false, book.getAsks().get(1.0).isBuy());
        assertEquals(1.0, book.getAsks().get(1.0).getPrice());
        assertEquals(1.0, book.getAsks().get(1.0).getQuantity());
        assertEquals(false, book.getAsks().get(2.0).isBuy());
        assertEquals(2.0, book.getAsks().get(2.0).getPrice());
        assertEquals(1.0, book.getAsks().get(2.0).getQuantity());

        assertEquals(1.0, book.getAsks().firstKey());
        assertEquals(2.0, book.getAsks().lastKey());
    }

    @Test
    public void emptyOrders() {
        OrderBook book = new OrderBook(5);
        for (int i = 1; i < 10; i++) {
            book.add(new Order(true, i * 1.0, 1.0));
        }

        assertEquals(5, book.getBids().size());
        assertEquals(9.0, book.getBids().firstKey());
        assertEquals(5.0, book.getBids().lastKey());


        book.add(new Order(true, 5.0, -1.0));

        assertEquals(4, book.getBids().size());
        assertEquals(9.0, book.getBids().firstKey());
        assertEquals(6.0, book.getBids().lastKey());
    }
}
