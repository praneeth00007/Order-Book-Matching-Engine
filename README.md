# Order Book Matching Engine


A small Java order matching engine that accepts trader input, matches buy and sell orders, and records trade confirmations asynchronously.


## Quick Setup


1. Compile the project:


```bash
javac -d out $(find src -name '*.java')
```


2. Run the app:


```bash
java -cp out main.Main
```


Enter orders one per line in the form:


```text
traderName BUY|SELL price quantity
```


Press Enter on an empty line to finish input.


## Sample Input


```text
TRADER_A BUY 102 10
TRADER_B SELL 100 10
TRADER_C BUY 99 -5
TRADER_D SELL 105 5
TRADER_E BUY 106 5
```


## Sample Output


```text
Order{trader='TRADER_A', side='BUY', price=102, qty=10}
Order{trader='TRADER_B', side='SELL', price=100, qty=10}
Order{trader='TRADER_C', side='BUY', price=99, qty=-5}
Order{trader='TRADER_D', side='SELL', price=105, qty=5}
Order{trader='TRADER_E', side='BUY', price=106, qty=5}
TRADER_A submitted: Order{trader='TRADER_A', side='BUY', price=102, qty=10}
TRADER_B submitted: Order{trader='TRADER_B', side='SELL', price=100, qty=10}
TRADER_D submitted: Order{trader='TRADER_D', side='SELL', price=105, qty=5}
TRADER_E submitted: Order{trader='TRADER_E', side='BUY', price=106, qty=5}
MATCHED: Trade{buyOrder=Order{trader='TRADER_A', side='BUY', price=102, qty=10}, sellOrder=Order{trader='TRADER_B', side='SELL', price=100, qty=10}, matchedPrice=100, quantity=10}
MATCHED: Trade{buyOrder=Order{trader='TRADER_E', side='BUY', price=106, qty=5}, sellOrder=Order{trader='TRADER_D', side='SELL', price=105, qty=5}, matchedPrice=105, quantity=5}
CONFIRMED: Trade{buyOrder=Order{trader='TRADER_A', side='BUY', price=102, qty=0}, sellOrder=Order{trader='TRADER_B', side='SELL', price=100, qty=0}, matchedPrice=100, quantity=10}
CONFIRMED: Trade{buyOrder=Order{trader='TRADER_E', side='BUY', price=106, qty=0}, sellOrder=Order{trader='TRADER_D', side='SELL', price=105, qty=0}, matchedPrice=105, quantity=5}

===== FINAL TRADES =====
Trade{buyOrder=Order{trader='TRADER_A', side='BUY', price=102, qty=0}, sellOrder=Order{trader='TRADER_B', side='SELL', price=100, qty=0}, matchedPrice=100, quantity=10}
Trade{buyOrder=Order{trader='TRADER_E', side='BUY', price=106, qty=0}, sellOrder=Order{trader='TRADER_D', side='SELL', price=105, qty=0}, matchedPrice=105, quantity=5}
Total Trades = 2

===== FAILED THREADS =====
TRADER_C
```