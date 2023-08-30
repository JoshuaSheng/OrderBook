
class LimitOrder extends Order {
    LimitOrder(char type, int id, short price, int quantity) {
        super(type, id, price, quantity);
    }
    @Override
    void decreaseQuantity(int amount) {
        assert quantity >= amount;
        quantity -= amount;
        if (quantity == 0) {
            isExecuted = true;
        }
    }

    @Override
    int getTotalQuantity() {
        return quantity;
    }
}