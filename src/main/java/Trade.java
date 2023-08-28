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
}
