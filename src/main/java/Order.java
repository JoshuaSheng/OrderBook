import java.time.LocalDateTime;

public class Order {
    char type;
    int id;
    short price;
    int quantity;
    long timeCreated;

    Order(char type, int id, short price, int quantity) {
        this.type = type;
        this.id = id;
        this.price = price;
        this.quantity = quantity;
        this.timeCreated = System.currentTimeMillis();
    }

    int getOrderBookQuantity() {
        return quantity;
    };
}

class IcebergOrder extends Order {
    int peakSize;

    IcebergOrder(char type, int id, short price, int quantity, int peakSize) {
        super(type, id, price, quantity);
        this.peakSize = peakSize;
    }

    @Override
    int getOrderBookQuantity() {
        return Math.min(peakSize, quantity);
    }
}