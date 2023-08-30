import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.Iterator;
import java.util.Objects;

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
        assertEquals(1, orderBook.trades.size());
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

    @Test
    void TestIcebergOrderPriority() {
        Order[] orders = {
                new IcebergOrder('S', 1, (short) 8, 100, 10),
                new IcebergOrder('S', 2, (short) 8, 100, 10),
                new IcebergOrder('S', 3, (short) 9, 100, 10),
                new LimitOrder('B', 4, (short) 9, 16),
        };
        for (Order order: orders) {
            orderBook.addOrder(order);
        }

        Iterator<Order> sellOrderIterator = orderBook.iterateSellOrders();

        assertEquals(2, orderBook.trades.size());
        assertEquals(orderBook.trades.get(0), new Trade(4, 1, (short) 8, 10));
        assertEquals(orderBook.trades.get(1), new Trade(4, 2, (short) 8, 6));

        assertEquals(sellOrderIterator.next(), new IcebergOrder('S', 2, (short) 8, 94, 10, 4));
        assertEquals(sellOrderIterator.next(), new IcebergOrder('S', 1, (short) 8, 90, 10));
        assertEquals(sellOrderIterator.next(), new IcebergOrder('S', 3, (short) 9, 100, 10));
    }

    @Test
    void TestIcebergWorkingQuantity() {
        Order[] orders = {
                new IcebergOrder('S', 1, (short) 8, 85, 10),
                new LimitOrder('B', 4, (short) 9, 10),
                new LimitOrder('B', 5, (short) 9, 9),
                new LimitOrder('B', 6, (short) 9, 2),
                new LimitOrder('B', 7, (short) 9, 60),
        };
        orderBook.addOrder(orders[0]);
        orderBook.addOrder(orders[1]);
        assertEquals(orderBook.iterateSellOrders().next(), new IcebergOrder('S', 1, (short) 8, 75, 10, 10));
        orderBook.addOrder(orders[2]);
        assertEquals(orderBook.iterateSellOrders().next(), new IcebergOrder('S', 1, (short) 8, 66, 10, 1));
        orderBook.addOrder(orders[3]);
        assertEquals(orderBook.iterateSellOrders().next(), new IcebergOrder('S', 1, (short) 8, 64, 10, 9));
        orderBook.addOrder(orders[4]);
        assertEquals(orderBook.iterateSellOrders().next(), new IcebergOrder('S', 1, (short) 8, 4, 10, 4));
    }

    @Test
    void TestIcebergTradePriority() {
        Order[] orders = {
                new IcebergOrder('S', 1, (short) 8, 85, 10),
                new IcebergOrder('S', 2, (short) 8, 85, 10),
                new LimitOrder('S', 3, (short) 8, 85),
                new LimitOrder('B', 4, (short) 8, 245),
        };
        for (Order order: orders) {
            orderBook.addOrder(order);
        }

        Iterator<Order> sellOrderIterator = orderBook.iterateSellOrders();

        assertEquals(3, orderBook.trades.size());
        assertEquals(orderBook.trades.get(0), new Trade(4, 1, (short) 8, 80));
        assertEquals(orderBook.trades.get(1), new Trade(4, 2, (short) 8, 80));
        assertEquals(orderBook.trades.get(2), new Trade(4, 3, (short) 8, 85));

        assertEquals(sellOrderIterator.next(), new IcebergOrder('S', 1, (short) 8, 5, 10));
        assertEquals(sellOrderIterator.next(), new IcebergOrder('S', 2, (short) 8, 5, 10));

    }
    @Test
    void TestIcebergExecution() {
        Order[] orders = {
                new LimitOrder('S', 1, (short) 101, 200),
                new LimitOrder('B', 2, (short) 99,500),
                new LimitOrder('B', 3, (short) 98, 255),
                new IcebergOrder('B', 4, (short) 100, 200, 100),
                new IcebergOrder('S', 5, (short) 100, 300, 100),
                new LimitOrder('B', 6, (short) 100, 200),
        };
        for (Order order: orders) {
            orderBook.addOrder(order);
            OrderBookLogger.printOrderBook(orderBook);
        }

        assertEquals(orderBook.trades.get(0), new Trade(4, 5, (short) 100, 200));
        assertEquals(orderBook.trades.get(1), new Trade(6, 5, (short) 100, 100));
    }

    @Test
    void TestAggressiveIcebergEntry() {
        Order[] orders = {
                new LimitOrder('B', 1, (short) 100,225),
                new IcebergOrder('S', 2, (short) 100, 350, 100),
        };
        for (Order order: orders) {
            orderBook.addOrder(order);
        }

        assertEquals(orderBook.trades.get(0), new Trade(1, 2, (short) 100, 225));
        assertEquals(orderBook.iterateSellOrders().next(), new IcebergOrder('S', 2, (short) 100, 125, 100));
    }

    @Test
    void TestAggressiveIcebergEntryTwoTrades() {
        Order[] orders = {
                new LimitOrder('B', 1, (short) 100,100),
                new LimitOrder('B', 3, (short) 100,125),
                new IcebergOrder('S', 2, (short) 100, 350, 100),
        };
        for (Order order: orders) {
            orderBook.addOrder(order);
        }

        assertEquals(orderBook.trades.get(0), new Trade(1, 2, (short) 100, 100));
        assertEquals(orderBook.trades.get(1), new Trade(3, 2, (short) 100, 125));
        assertEquals(orderBook.iterateSellOrders().next(), new IcebergOrder('S', 2, (short) 100, 125, 100));
    }
}
