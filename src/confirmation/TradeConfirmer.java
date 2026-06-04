package confirmation;

import Model.Trade;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class TradeConfirmer {

    private final ExecutorService executor;
    private final List<Trade> tradeHistory;
    private final Random random = new Random();

    public TradeConfirmer(ExecutorService executor, List<Trade> tradeHistory) {
        this.executor = executor;
        this.tradeHistory = tradeHistory;
    }

    // We used completable future i nstead of future because future will stop the thread
    // which calls the .get() so here main thread will interrupted and it
    // doesnt continues asynchronously
    public CompletableFuture<Void> confirm(Trade trade) {
        // supplyAsync executes confirmation logic asynchronously using thread pool

        return CompletableFuture
                .supplyAsync(() -> doConfirm(trade), executor)

                .thenAccept(success -> {
                    if (success) {
                        // synchronized ensures thread-safe writes to shared tradeHistory list as multiple async confirmations may complete concurrently
                        System.out.println("CONFIRMED: " + trade);
                    }
                })

                .exceptionally(ex -> {
                    System.out.println("FAILED: " + trade);
                    return null;
                });
    }

    private boolean doConfirm(Trade trade) {

        try {
            Thread.sleep(random.nextInt(100));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // simulate 10% failure
        if (random.nextInt(10) == 0) {
            throw new RuntimeException("Confirmation failed");
        }

        return true;
    }


    // we need to avoid calling .get as defeat the purpose of not selecting future .
}