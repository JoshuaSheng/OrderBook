class IcebergOrder extends Order {
    final int peakSize;
    private int hiddenVolume;

    IcebergOrder(char type, int id, short price, int quantity, int peakSize) {
        super(type, id, price, Math.min(quantity, peakSize));
        assert(peakSize > 0);
        assert(id > 0);
        this.peakSize = peakSize;
        this.hiddenVolume = Math.max(0, quantity - peakSize);
    }

    //partially executed IcebergOrder for testing
    IcebergOrder(char type, int id, short price, int quantity, int peakSize, int workingQuantity) {
        super(type, id, price, workingQuantity);
        assert(peakSize > 0);
        assert(id > 0);
        this.peakSize = peakSize;
        this.hiddenVolume = quantity - workingQuantity;
    }

    @Override
    void decreaseQuantity(int amount) {
        assert amount <= getTotalQuantity();
        if (amount < quantity) {
            quantity -= amount;
        }
        else {
            amount -= quantity;
            int newOrderBookQuantity = Math.min(peakSize - (amount % peakSize), hiddenVolume - amount);
            hiddenVolume -= amount + newOrderBookQuantity;
            quantity = newOrderBookQuantity;
            this.updateTimePriority();
            isExecuted = true;
        }
    }

    void refillQuantity() {
        int amountToRefill = Math.min(peakSize - quantity, hiddenVolume);
        quantity += amountToRefill;
        hiddenVolume -= amountToRefill;
    }

    @Override
    int getTotalQuantity() {
        return quantity + hiddenVolume;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        IcebergOrder order = (IcebergOrder) o;
        return super.equals(order) && this.peakSize == order.peakSize && this.getTotalQuantity() == order.getTotalQuantity();
    }
}
