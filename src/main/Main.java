package main;

import Engine.MatchingEngine;
import Model.Order;
import Model.Side;
import Model.Trade;
import Trader.TraderTask;
import confirmation.TradeConfirmer;

import java.util.*;
import java.util.concurrent.*;

public class Main {

    public static volatile boolean marketOpen = true;
    public static void main(String[] args) throws InterruptedException {

        List<Order> allOrders = new ArrayList<>();

        Scanner sc = new Scanner(System.in);

        while (sc.hasNextLine()){
            String line = sc.nextLine().trim();

            // Stops if string is null ie the orders are finished ""
            if (line.isEmpty()) {
                break;
            }

            String[] parts = line.split("\\s+");

            String trader = parts[0];
            Side side = Side.valueOf(parts[1].toUpperCase());
            int price = Integer.parseInt(parts[2]);
            int qty = Integer.parseInt(parts[3]);

            allOrders.add(new Order(trader, side, price, qty));
        }

        // Lets verify

        for(Order ord:allOrders){
            System.out.println(ord.toString());
        }


        Map<String, List<Order>> traderOrders = new HashMap<>();

        for (Order order : allOrders) {
            traderOrders
                    .computeIfAbsent(order.getTrader(), k -> new ArrayList<>())
                    .add(order);
        }

        ExecutorService executor = Executors.newFixedThreadPool(3);
        List<Trade> tradeHistory = Collections.synchronizedList(new ArrayList<>());
        TradeConfirmer confirmer = new TradeConfirmer(executor, tradeHistory);


        BlockingQueue<Order> orderQueue = new LinkedBlockingQueue<>();
        MatchingEngine engine = new MatchingEngine(
                orderQueue,
                tradeHistory,
                confirmer
        );

        Thread engineThread = new Thread(engine, "MatchingEngine");
        engineThread.start();

        List<Thread> traderThreads = new ArrayList<>();

        for (Map.Entry<String, List<Order>> entry : traderOrders.entrySet()) {

            TraderTask task =
                    new TraderTask(entry.getValue(), orderQueue);

            Thread traderThread =
                    new Thread(task, entry.getKey());

            traderThreads.add(traderThread);
            traderThread.start();
        }

        for (Thread t : traderThreads) {
            t.join();
        }


        marketOpen = false;

        engineThread.join();

        CompletableFuture
                .allOf(engine.getFutures().toArray(new CompletableFuture[0]))
                .join();

        executor.shutdown();

        System.out.println("\n===== FINAL TRADES =====");

        for (Trade trade : tradeHistory) {
            System.out.println(trade);
        }

        System.out.println("Total Trades = " + tradeHistory.size());





    }
}