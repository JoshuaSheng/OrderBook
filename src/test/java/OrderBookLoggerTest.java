import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderBookLoggerTest {
    private ByteArrayOutputStream output;
    private final PrintStream originalOutput = System.out;

    @BeforeEach
    public void setUp() {
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
    }

    @Test
    void TestPrintTrades() {
        Trade trade = new Trade(1, 1, (short) 1, 1);
        OrderBookLogger.printTrade(trade);
        String printedOutput = output.toString().trim();
        assertEquals("1,1,1,1", printedOutput);
    }

    @Test
    void TestPrintOrderBook() {
        OrderBook orderBook = new OrderBook();

        Order[] orders = {
                new LimitOrder('S', 2, (short) 2, 1),
                new LimitOrder('B', 1, (short) 1, 1),
        };

        for (Order order: orders) {
            orderBook.addOrder(order);
        }

        OrderBookLogger.printOrderBook(orderBook);

        String printedOutput = output.toString().trim();

        String expectedOutput = String.join(System.lineSeparator(),
                "+-----------------------------------------------------------------+",
                "| BUY                            | SELL                           |",
                "| Id       | Volume      | Price | Price | Volume      | Id       |",
                "+----------+-------------+-------+-------+-------------+----------+",
                "|         1|            1|      1|      2|            1|         2|",
                "+-----------------------------------------------------------------+");

        assertEquals(expectedOutput, printedOutput);
    }

    @Test
    void TestPrintEmptyOrderBook() {
        OrderBook orderBook = new OrderBook();

        OrderBookLogger.printOrderBook(orderBook);

        String printedOutput = output.toString().trim();

        String expectedOutput = String.join(System.lineSeparator(),
                "+-----------------------------------------------------------------+",
                "| BUY                            | SELL                           |",
                "| Id       | Volume      | Price | Price | Volume      | Id       |",
                "+----------+-------------+-------+-------+-------------+----------+",
                "+-----------------------------------------------------------------+");

        assertEquals(expectedOutput, printedOutput);
    }

    @Test
    void TestUnbalancedOrderBook() {
        OrderBook orderBook = new OrderBook();

        Order[] orders = {
                new LimitOrder('B', 1, (short) 1, 1),
        };

        for (Order order: orders) {
            orderBook.addOrder(order);
        }

        OrderBookLogger.printOrderBook(orderBook);

        String printedOutput = output.toString().trim();

        String expectedOutput = String.join(System.lineSeparator(),
                "+-----------------------------------------------------------------+",
                "| BUY                            | SELL                           |",
                "| Id       | Volume      | Price | Price | Volume      | Id       |",
                "+----------+-------------+-------+-------+-------------+----------+",
                "|         1|            1|      1|       |             |          |",
                "+-----------------------------------------------------------------+");

        assertEquals(expectedOutput, printedOutput);
    }

    @Test
    void TestFormatsNumbersCorrectly() {
        OrderBook orderBook = new OrderBook();

        Order[] orders = {
                new LimitOrder('S', 2000000000, (short) 20000, 1000000000),
                new LimitOrder('B', 1000000000, (short) 10000, 1000000000),
        };

        for (Order order: orders) {
            orderBook.addOrder(order);
        }

        OrderBookLogger.printOrderBook(orderBook);

        String printedOutput = output.toString().trim();

        String expectedOutput = String.join(System.lineSeparator(),
                "+-----------------------------------------------------------------+",
                "| BUY                            | SELL                           |",
                "| Id       | Volume      | Price | Price | Volume      | Id       |",
                "+----------+-------------+-------+-------+-------------+----------+",
                "|1000000000|1,000,000,000| 10,000| 20,000|1,000,000,000|2000000000|",
                "+-----------------------------------------------------------------+");

        assertEquals(expectedOutput, printedOutput);
    }
    @Test
    void TestPrintOrderBookIcebergOrder() {
        OrderBook orderBook = new OrderBook();

        Order[] orders = {
                new IcebergOrder('S', 2, (short) 2, 10000, 1),
                new IcebergOrder('B', 1, (short) 1, 10000, 1),
        };

        for (Order order: orders) {
            orderBook.addOrder(order);
        }

        OrderBookLogger.printOrderBook(orderBook);

        String printedOutput = output.toString().trim();

        String expectedOutput = String.join(System.lineSeparator(),
                "+-----------------------------------------------------------------+",
                "| BUY                            | SELL                           |",
                "| Id       | Volume      | Price | Price | Volume      | Id       |",
                "+----------+-------------+-------+-------+-------------+----------+",
                "|         1|            1|      1|      2|            1|         2|",
                "+-----------------------------------------------------------------+");

        assertEquals(expectedOutput, printedOutput);
    }

    /*
    I was having a bit of fun with this one. The idea is that a big order smashes into a bunch of iceberg orders
    and the last iceberg ends up with a quantity less than its peak. It should probably be named something like
    TestIcebergOrderDisplaysVolumeLessThanPeak
     */
    @Test
    void Titanic() {
        OrderBook orderBook = new OrderBook();

        Order[] orders = {
                new IcebergOrder('S', 1, (short) 2, 10000, 7500),
                new IcebergOrder('S', 2, (short) 2, 10000, 7500),
                new IcebergOrder('S', 3, (short) 2, 10000, 7500),
                new LimitOrder('B', 4, (short) 3, 25000),
        };

        for (Order order: orders) {
            orderBook.addOrder(order);
        }

        OrderBookLogger.printOrderBook(orderBook);

        String printedOutput = output.toString().trim();

        String expectedOutput = String.join(System.lineSeparator(),
                "4,1,2,10000",
                "4,2,2,10000",
                "4,3,2,5000",
                "+-----------------------------------------------------------------+",
                "| BUY                            | SELL                           |",
                "| Id       | Volume      | Price | Price | Volume      | Id       |",
                "+----------+-------------+-------+-------+-------------+----------+",
                "|          |             |       |      2|        5,000|         3|",
                "+-----------------------------------------------------------------+");

        assertEquals(expectedOutput, printedOutput);
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOutput);
    }
}