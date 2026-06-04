package Model;

// Yeah order is the model as we are doing stock exchange the key material is order
// we made the all fields as final except the qty as we will update it so
// it should not be final
public class Order
{


    @Override
    public String toString() {
        return "Order{" +
                "trader='" + trader + '\'' +
                ", side='" + side + '\'' +
                ", price=" + price +
                ", qty=" + qty +
                '}';
    }
    private final String trader;

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public int getPrice() {
        return price;
    }

    public Side getSide() {
        return side;
    }

    public String getTrader() {
        return trader;
    }

    private final Side side;
    private final int  price;
    private int qty;

    public Order(String trader, Side side, int price, int qty) {
        this.trader = trader;
        this.side = side;
        this.price = price;
        this.qty = qty;
    }

    
}
