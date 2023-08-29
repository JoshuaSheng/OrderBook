
class LimitOrder extends Order {
    LimitOrder(char type, int id, short price, int quantity) {
        super(type, id, price, quantity);
    }

    @Override
    int getOrderBookQuantity() {
        return quantity;
    }
}