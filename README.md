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