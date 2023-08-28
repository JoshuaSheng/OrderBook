import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderBookLoggerTest {
    private ByteArrayOutputStream output;
    private final PrintStream originalOutput = System.out;

    @BeforeEach
    public void setUp() {
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
    }

    @Test
    public void TestPrintTrades() {
        Trade trade = new Trade(1, 1, (short) 1, 1);
        OrderBookLogger.printTrade(trade);
        String printedOutput = output.toString().trim();
        assertEquals("1,1,1,1", printedOutput);
    }

    @Test
    public void TestPrintOrderBook() {

    }

    @After
    public void tearDown() {
        System.setOut(originalOutput);
    }
}