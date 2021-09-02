package com.benlep.gsrcoin;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;

import com.benlep.gsrcoin.book.Order;
import com.benlep.gsrcoin.book.OrderBook;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

@SpringBootApplication
public class GsrcoinApplication implements CommandLineRunner {
	private static final String COINBASE_WS = "wss://ws-feed.pro.coinbase.com";
	private static final int LEVELS = 10;
	private static final int ORDER_BLOCK = 100;
	private static final Logger LOG = LoggerFactory.getLogger(GsrcoinApplication.class);

	// to measure processing rate
	private int count = 0;
	private long millis = System.currentTimeMillis();

	private final ObjectMapper mapper;
	
	public GsrcoinApplication(ObjectMapper mapper) {
		this.mapper = mapper;
	}

	public static void main(String[] args) {
		LOG.info("STARTING ");
        SpringApplication.run(GsrcoinApplication.class, args);
		LOG.info("FINISHED !!!");
    }
 
    @Override
    public void run(String... args) throws Exception {
		// validation and defaulting
		String inst = "ETH-BTC";
		boolean trace = true;

		if (args.length > 2) {
			throw new IllegalArgumentException("We need only 2 argument: the instrument to subscribe on Coinbase and the debug flag");
		} else if (args.length > 1) {
			trace = Boolean.parseBoolean(args[1]);
		} else if (args.length > 0) {
			inst = args[0];
		}

		processFeed(inst, trace);
    }

	private void processFeed(String inst, boolean trace) throws ExecutionException, InterruptedException {
		LOG.info("-- Subscribing to {}", inst);

		BlockingQueue<Order> orderQueue = new ArrayBlockingQueue<>(5);
		OrderBook book = new OrderBook(inst, LEVELS);

		var clientSession = subscribe(inst, orderQueue);
		while (clientSession.isOpen()) {
			Order newOrder = orderQueue.take();
			book.add(newOrder);
			
			if (trace) {
				book.print();
			}
			
			measureRate();
		}
	}



	protected WebSocketSession subscribe(String inst, BlockingQueue<Order> orderQueue) throws InterruptedException, ExecutionException {
		var handler = new CoinbaseHandler(inst, mapper, orderQueue);
		var webSocketClient = new StandardWebSocketClient();
		var clientSession = webSocketClient.doHandshake(handler, new WebSocketHttpHeaders(), URI.create(COINBASE_WS)).get();
		clientSession.setTextMessageSizeLimit(1000_000);
		return clientSession;
	}

	protected void measureRate() {
		if (++count % ORDER_BLOCK == 0) {
			long elapsed = System.currentTimeMillis() - millis;
			LOG.info("added {} orders at {}/s", count, 1000.0 * ORDER_BLOCK / elapsed);
			millis = System.currentTimeMillis();
		}
	}
}
