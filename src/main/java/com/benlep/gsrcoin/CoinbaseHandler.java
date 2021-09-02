package com.benlep.gsrcoin;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;

import com.benlep.gsrcoin.book.Order;
import com.benlep.gsrcoin.book.OrderBook;
import com.benlep.gsrcoin.model.Channel;
import com.benlep.gsrcoin.model.L2Update;
import com.benlep.gsrcoin.model.Subscribe;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class CoinbaseHandler extends  TextWebSocketHandler {
    private static final Logger LOG = LoggerFactory.getLogger(CoinbaseHandler.class);
    
    private final String instrument;
    private final ObjectMapper mapper;
    private final BlockingQueue<Order> orderQueue;

    
    public CoinbaseHandler(String instrument, ObjectMapper mapper, BlockingQueue<Order> orderQueue) {
        this.instrument = instrument;
        this.mapper = mapper;
        this.orderQueue = orderQueue;
    }
    
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            var tree = mapper.readTree(message.getPayload());
            if (tree.get("type").asText().equals("l2update")) {
                ArrayNode changes = (ArrayNode) tree.get("changes");

                for (int i = 0; i < changes.size(); ++i) {
                    ArrayNode changePriceQty = (ArrayNode) changes.get(i);
                    boolean buy = changePriceQty.get(0).asText().equals("buy");
                    double price = changePriceQty.get(1).asDouble();
                    double qty = changePriceQty.get(2).asDouble();
                    
                    orderQueue.put(new Order(buy, price, qty));
                }
            }

        } catch (JsonParseException e) {
            // TODO Auto-generated catch block
            LOG.warn("Message too long ...");
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
    }
    
    @Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        var productIds = new String[] { instrument };
		var subMsg = new Subscribe(productIds, new Channel[] { new Channel("level2")});
		var subTxt = mapper.writeValueAsString(subMsg);

        session.sendMessage(new TextMessage(subTxt));
	}

    @Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        LOG.error("Connection closed: {}", status);
	}

}
