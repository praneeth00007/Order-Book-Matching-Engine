package Engine;

import Model.Order;
import Model.Side;
import Model.Trade;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

import confirmation.TradeConfirmer;
import main.Main;

public class MatchingEngine implements Runnable {

    private final BlockingQueue<Order> orderQueue;
    private final List<Trade> tradeHistory;
    private final ReentrantLock lock = new ReentrantLock();

    private final List<Order> buyOrders = new ArrayList<>();
    private final List<Order> sellOrders = new ArrayList<>();

    private final TradeConfirmer confirmer;

    // Thread-safe list for async confirmations
    private final List<CompletableFuture<Void>> futures =
            Collections.synchronizedList(new ArrayList<>());

    public MatchingEngine(BlockingQueue<Order> orderQueue,
                          List<Trade> tradeHistory,
                          TradeConfirmer confirmer) {

        this.orderQueue = orderQueue;
        this.tradeHistory = tradeHistory;
        this.confirmer = confirmer;
    }

    @Override
    public void run() {
        try {
            while (Main.marketOpen || !orderQueue.isEmpty()) {

                Order order = orderQueue.poll(100, TimeUnit.MILLISECONDS);

                if (order != null) {
                    processOrder(order);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void processOrder(Order order) {
        try {
            if (lock.tryLock(50, TimeUnit.MILLISECONDS)) {
                try {
                    if (order.getSide() == Side.BUY) {
                        matchBuy(order);
                    } else {
                        matchSell(order);
                    }
                } finally {
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void matchBuy(Order buyOrder) {

        Iterator<Order> iterator = sellOrders.iterator();

        while (iterator.hasNext()) {
            Order sellOrder = iterator.next();

            if (buyOrder.getPrice() >= sellOrder.getPrice()) {

                int qty = Math.min(buyOrder.getQty(), sellOrder.getQty());

                Trade trade = new Trade(
                        buyOrder,
                        sellOrder,
                        sellOrder.getPrice(),
                        qty
                );

                System.out.println("MATCHED: " + trade);

                // reduce quantities
                buyOrder.setQty(buyOrder.getQty() - qty);
                sellOrder.setQty(sellOrder.getQty() - qty);

                // async confirmation
                tradeHistory.add(trade);
                confirmTrade(trade);

                if (sellOrder.getQty() == 0) {
                    iterator.remove();
                }

                if (buyOrder.getQty() == 0) {
                    return;
                }
            }
        }

        // add remaining order only if qty > 0
        if (buyOrder.getQty() > 0) {
            buyOrders.add(buyOrder);
        }
    }

    private void matchSell(Order sellOrder) {

        Iterator<Order> iterator = buyOrders.iterator();

        while (iterator.hasNext()) {
            Order buyOrder = iterator.next();

            if (buyOrder.getPrice() >= sellOrder.getPrice()) {

                int qty = Math.min(buyOrder.getQty(), sellOrder.getQty());

                Trade trade = new Trade(
                        buyOrder,
                        sellOrder,
                        sellOrder.getPrice(),
                        qty
                );

                System.out.println("MATCHED: " + trade);

                // reduce quantities
                buyOrder.setQty(buyOrder.getQty() - qty);
                sellOrder.setQty(sellOrder.getQty() - qty);

                // async confirmation
                tradeHistory.add(trade);
                confirmTrade(trade);

                if (buyOrder.getQty() == 0) {
                    iterator.remove();
                }

                if (sellOrder.getQty() == 0) {
                    return;
                }
            }
        }

        // add remaining order only if qty > 0
        if (sellOrder.getQty() > 0) {
            sellOrders.add(sellOrder);
        }
    }

    private void confirmTrade(Trade trade) {
        // we used completable future as future will stop if we use .get()
        CompletableFuture<Void> future = confirmer.confirm(trade);

        // store future for later allOf().join()
        futures.add(future);
    }

    public List<CompletableFuture<Void>> getFutures() {
        return futures;
    }
}