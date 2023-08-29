class IcebergOrder extends Order {
    final int peakSize;

    IcebergOrder(char type, int id, short price, int quantity, int peakSize) {
        super(type, id, price, quantity);
        assert(peakSize > 0);
        assert(id > 0);
        this.peakSize = peakSize;
    }

    @Override
    int getOrderBookQuantity() {
        return Math.min(peakSize, quantity);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        IcebergOrder order = (IcebergOrder) o;
        return super.equals(order) && this.peakSize == order.peakSize;
    }
}
