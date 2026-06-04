package Trader;

import Model.Order;

import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

public class TraderTask implements Runnable {

    private final List<Order> orders;

    // Here Blocking queue is used to safely share orders bw multiple threads
    // without requiring explicit synchronization
    private final BlockingQueue<Order> orderQueue;
    private final Random random = new Random();

    public TraderTask(List<Order> orders, BlockingQueue<Order> orderQueue) {
        this.orders = orders;
        this.orderQueue = orderQueue;
    }

    @Override
    public void run() {
        try {
            for (Order order : orders) {

                // submit order to shared queue
                orderQueue.put(order);

                System.out.println(Thread.currentThread().getName() + " submitted: " + order);

                // simulate delay between orders
                Thread.sleep(random.nextInt(100));

            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}