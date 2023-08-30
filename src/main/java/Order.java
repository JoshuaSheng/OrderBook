abstract class Order {
    final char type;
    final int id;
    final short price;
    int quantity;
    boolean isExecuted = false;
    /*
    this time variable used to determine which objects were created first in place of timestamps, which are challenging
    to use in a test environment where everything executes at the same time.
     */
    protected static int time = 0;
    int timestamp;

    Order(char type, int id, short price, int quantity) {
        assert(price > 0);
        assert(quantity > 0);
        this.type = type;
        this.id = id;
        this.price = price;
        this.quantity = quantity;
        time += 1;
        this.timestamp = time;
    }

    abstract void decreaseQuantity(int amount);

    abstract int getTotalQuantity();

    void updateTimePriority() {
        time += 1;
        this.timestamp = time;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Order order = (Order) o;
        return this.type == order.type && this.id == order.id && this.price == order.price && this.quantity == order.quantity;
    }
}