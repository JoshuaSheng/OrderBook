public class Trade {
    int buyOrderId;
    int sellOrderId;
    short price;
    int quantity;

    Trade(int buyOrderId, int sellOrderId, short price, int quantity) {
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.price = price;
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Trade trade = (Trade) o;
        return this.buyOrderId == trade.buyOrderId && this.sellOrderId == trade.sellOrderId && this.price == trade.price && this.quantity == trade.quantity;
    }
}
