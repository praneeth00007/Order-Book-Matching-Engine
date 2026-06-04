package Pitfalls;

import java.util.concurrent.atomic.AtomicInteger;

public class pitfalls {

    private static int raceCounter = 0;

    private static volatile int volatileCounter = 0;

    private static AtomicInteger atomicCounter = new AtomicInteger();

    private static final Object lockA = new Object();

    private static final Object lockB = new Object();

    // Here each thread is independent and it will produce the output
    // like for incrementing a value it takes 3 steps
    // ==> read value ==> increment by 1 ==> update
    // so in-mean time any thread enters and complete before another thread it causes
    // inconsistency of data.
    private static void raceConditionDemo()
            throws Exception {

        raceCounter = 0;

        Runnable task = () -> {

            for (int i = 0; i < 1000; i++) {

                raceCounter++;
            }
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        Thread t3 = new Thread(task);

        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();

        System.out.println(
                "Race Condition Count = "
                        + raceCounter
        );
    }

    // Volatile is only suitable for single read/write ie reading flag or
    // updating the value with a constant
    private static void volatileDemo()
            throws Exception {

        volatileCounter = 0;

        Runnable task = () -> {

            for (int i = 0; i < 1000; i++) {

                volatileCounter++;
            }
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        Thread t3 = new Thread(task);

        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();

        System.out.println(
                "Volatile Counter = " + volatileCounter
        );
    }

    // AtomicInteger makes increments atomic and safe to use from many threads.
    private static void atomicDemo()
            throws Exception {

        atomicCounter.set(0);

        Runnable task = () -> {

            for (int i = 0; i < 1000; i++) {

                atomicCounter.incrementAndGet();
            }
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        Thread t3 = new Thread(task);

        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();

        System.out.println(
                "Atomic Counter = "
                        + atomicCounter.get()
        );
    }


    private static void deadlockDemo() {

        Thread t1 = new Thread(() -> {

            synchronized (lockA) {

                System.out.println(
                        "Thread 1 acquired Lock A"
                );

                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                synchronized (lockB) {

                    System.out.println(
                            "Thread 1 acquired Lock B"
                    );
                }
            }
        });

        Thread t2 = new Thread(() -> {

            synchronized (lockB) {

                System.out.println(
                        "Thread 2 acquired Lock B"
                );

                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                synchronized (lockA) {

                    System.out.println(
                            "Thread 2 acquired Lock A"
                    );
                }
            }
        });

        t1.start();
        t2.start();
    }

    public static void main(String[] args)
            throws Exception {

//        System.out.println(
//                "\n===== RACE CONDITION DEMO ====="
//        );
//        raceConditionDemo();
//
//        System.out.println(
//                "\n===== VOLATILE DEMO ====="
//        );
//        volatileDemo();
//
//        System.out.println(
//                "\n===== ATOMIC INTEGER DEMO ====="
//        );
//        atomicDemo();

        // deadlockDemo();
    }
}