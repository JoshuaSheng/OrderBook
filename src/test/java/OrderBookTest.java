import org.junit.jupiter.api.BeforeEach;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrderBookTest {
    OrderBook orderBook;

    @BeforeEach
    public void setUp() {
        orderBook = new OrderBook();
    }

    public void TestOrdersCancelOut() {
        Order[] orders = {
                new Order('B', 1, (short) 1, 4),
                new Order('S', 2, (short) 1, 4),
        };
        for (Order order: orders) {
            orderBook.addOrder(order);
        }

        assertFalse(orderBook.trades.isEmpty());
        assertTrue(Objects.equals(orderBook.trades.get(0), new Trade(1, 2, (short) 1, 4)));
    }
    public void TestOrderPriority() {

    }
}
