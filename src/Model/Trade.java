package Model;

public class Trade
{
    // we made it as final as we want to lock as in the time when the user changes price it leads to data corruption ,
    // inconsistency too .
    private final Order buyOrder;

    @Override
    public String toString() {
        return "Trade{" +
                "buyOrder=" + buyOrder +
                ", sellOrder=" + sellOrder +
                ", matchedPrice=" + matchedPrice +
                ", quantity=" + quantity +
                '}';
    }

    private final Order sellOrder;
    private final int matchedPrice;
    private final int quantity;

    public Order getSellOrder() {
        return sellOrder;
    }

    public Order getBuyOrder() {
        return buyOrder;
    }

    public int getMatchedPrice() {
        return matchedPrice;
    }

    public int getQuantity() {
        return quantity;
    }




    public Trade(Order buyOrder, Order sellOrder, int matchedPrice, int quantity)
    {
        this.buyOrder =buyOrder;
        this.sellOrder = sellOrder;
        this.matchedPrice = matchedPrice;
        this.quantity = quantity;

    }




}
