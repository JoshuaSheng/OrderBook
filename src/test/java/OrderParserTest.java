import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class OrderParserTest {
    @Test
    void TestParseIcebergOrder() {
        String line = "B,2,3,5,4";
        Order parsedOrder = OrderParser.fromSimplifiedAscii(line);
        Assertions.assertTrue(parsedOrder instanceof IcebergOrder);
        assert(parsedOrder.type == 'B');
        assert(parsedOrder.id == 2);
        assert(parsedOrder.price == 3);
        assert(parsedOrder.getTotalQuantity() == 5);
        assert(parsedOrder.quantity == 4);

    }

    @Test
    void TestParseLimitOrder() {
        String line = "B,2,3,4";
        Order parsedOrder = OrderParser.fromSimplifiedAscii(line);
        Assertions.assertFalse(parsedOrder instanceof IcebergOrder);

        assert(parsedOrder.type == 'B');
        assert(parsedOrder.id == 2);
        assert(parsedOrder.price == 3);
        assert(parsedOrder.quantity == 4);
        assert(parsedOrder.getTotalQuantity() == 4);
    }
}
