import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

class OrderBookTest {
    OrderBook orderBook;

    @BeforeEach
    public void setUp() {
        orderBook = new OrderBook();
    }

    @Test
    void TestOrdersCancelOut() {
        Order[] orders = {
                new LimitOrder('B', 1, (short) 1, 4),
                new LimitOrder('S', 2, (short) 1, 4),
        };
        for (Order order: orders) {
            orderBook.addOrder(order);
        }
        OrderBookLogger.printTrade(orderBook.trades.get(0));
        assertEquals(orderBook.trades.size(), 1);
        assertEquals(orderBook.trades.get(0), new Trade(1, 2, (short) 1, 4));
        assertFalse(orderBook.iterateBuyOrders().hasNext());
        assertFalse(orderBook.iterateSellOrders().hasNext());
    }

    @Test
    void TestTradePriority() {
        Order[] orders = {
                new LimitOrder('B', 1, (short) 9, 4),
                new LimitOrder('B', 2, (short) 10, 4),
                new LimitOrder('B', 3, (short) 10, 4),
                new LimitOrder('S', 4, (short) 9, 4),
        };
        for (Order order: orders) {
            orderBook.addOrder(order);
        }
        assertFalse(orderBook.trades.isEmpty());
        assertEquals(orderBook.trades.get(0), new Trade(2, 4, (short) 10, 4));

        assertEquals(orderBook.iterateBuyOrders().next(), new LimitOrder('B', 3, (short) 10, 4));

    }

    @Test
    void TestOneSellOrderMultipleTrades() {
        Order[] orders = {
                new LimitOrder('B', 1, (short) 9, 4),
                new LimitOrder('B', 2, (short) 10, 4),
                new LimitOrder('B', 3, (short) 10, 4),
                new LimitOrder('S', 4, (short) 9, 16),
        };
        for (Order order: orders) {
            orderBook.addOrder(order);
        }
        assertEquals(3, orderBook.trades.size());
        assertEquals(orderBook.iterateSellOrders().next(), new LimitOrder('S', 4, (short) 9, 4));
    }

    @Test
    void TestOneBuyOrderMultipleTrades() {
        Order[] orders = {
                new LimitOrder('S', 1, (short) 9, 4),
                new LimitOrder('S', 2, (short) 8, 4),
                new LimitOrder('S', 3, (short) 8, 4),
                new LimitOrder('B', 4, (short) 9, 16),
        };
        for (Order order: orders) {
            orderBook.addOrder(order);
        }
        assertEquals(3, orderBook.trades.size());
        assertEquals(orderBook.iterateBuyOrders().next(), new LimitOrder('B', 4, (short) 9, 4));
    }

    @Test
    void TestOrderPriority() {
        Order[] orders = {
                new LimitOrder('S', 1, (short) 12, 4),
                new LimitOrder('S', 2, (short) 12, 4),
                new LimitOrder('S', 3, (short) 10, 4),
                new LimitOrder('B', 4, (short) 8, 16),
                new LimitOrder('B', 5, (short) 8, 16),
                new LimitOrder('B', 6, (short) 9, 16),
        };
        for (Order order: orders) {
            orderBook.addOrder(order);
        }
        assertTrue(orderBook.trades.isEmpty());
        Iterator<Order> buyOrderIterator = orderBook.iterateBuyOrders();
        Iterator<Order> sellOrderIterator = orderBook.iterateSellOrders();

        assertEquals(sellOrderIterator.next(), new LimitOrder('S', 3, (short) 10, 4));
        assertEquals(sellOrderIterator.next(), new LimitOrder('S', 1, (short) 12, 4));
        assertEquals(sellOrderIterator.next(), new LimitOrder('S', 2, (short) 12, 4));
        assertEquals(buyOrderIterator.next(), new LimitOrder('B', 6, (short) 9, 16));
        assertEquals(buyOrderIterator.next(), new LimitOrder('B', 4, (short) 8, 16));
        assertEquals(buyOrderIterator.next(), new LimitOrder('B', 5, (short) 8, 16));
    }
}
