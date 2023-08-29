import java.util.Iterator;

public class OrderBookLogger {

    private OrderBookLogger() {
        throw new IllegalStateException("Utility Class");
    }

    public static void printTrade(Trade trade) {
        System.out.printf("%d,%d,%d,%d%n", trade.buyOrderId, trade.sellOrderId, trade.price, trade.quantity);
    }

    /*
    Prints a line of dashes and crosses like so:
    +-----+--------+----+
    with the number of dashes between each cross determined by the columnLengths parameters
     */
    private static void printTableLine(int... columnLengths) {
        StringBuilder line = new StringBuilder("+");
        for (int columnLength: columnLengths) {
            line.append("-".repeat(columnLength)).append('+');
        }
        System.out.println(line);
    }

    /*
    Prints a line like so:
    |value|value|value|
    with the contents between each bar determined by the values parameters
     */
    private static void printTableRow(String... values) {
        StringBuilder line = new StringBuilder("|");
        for (String value: values) {
            line.append(value).append('|');
        }
        System.out.println(line);
    }

    /*
    passes a value to make it exactly totalLength, and right or left justified. the direction it is justified has 1
    space of padding.
     */
    private static String padValue(String value, int totalLength, boolean rightJustify) {
        StringBuilder formattedValue = new StringBuilder();
        if (rightJustify) {
            int whitespaceLength = totalLength - value.length();
            formattedValue.append(" ".repeat(whitespaceLength));
            formattedValue.append(value);
        }
        else {
            int padding = 1;
            int whitespaceLength = totalLength - value.length() - padding;
            formattedValue.append(" ".repeat(padding));
            formattedValue.append(value);
            formattedValue.append(" ".repeat(whitespaceLength));
        }
        return formattedValue.toString();
    }

    private static String padValue(String value, int totalLength) {
        return padValue(value, totalLength, false);
    }

    /*
    Prints all bids and asks in the order book, giving priority to price and then time created.
     */
    public static void printOrderBook(OrderBook orderBook) {
        final int TOTAL_WIDTH = 67;
        final int ID_COLUMN_WIDTH = 10;
        final int VOLUME_COLUMN_WIDTH = 13;
        final int PRICE_COLUMN_WIDTH = 7;

        final int FULL_TABLE_COLUMN_WIDTH = TOTAL_WIDTH - 2;
        final int HALF_TABLE_COLUMN_WIDTH = (TOTAL_WIDTH - 3)/2;

        //Table Header
        printTableLine(FULL_TABLE_COLUMN_WIDTH);
        printTableRow(
                padValue("BUY", HALF_TABLE_COLUMN_WIDTH),
                padValue("SELL", HALF_TABLE_COLUMN_WIDTH)
        );
        printTableRow(
                padValue("Id", ID_COLUMN_WIDTH),
                padValue("Volume", VOLUME_COLUMN_WIDTH),
                padValue("Price", PRICE_COLUMN_WIDTH),
                padValue("Price", PRICE_COLUMN_WIDTH),
                padValue("Volume", VOLUME_COLUMN_WIDTH),
                padValue("Id", ID_COLUMN_WIDTH)
        );
        printTableLine(ID_COLUMN_WIDTH, VOLUME_COLUMN_WIDTH, PRICE_COLUMN_WIDTH,
                PRICE_COLUMN_WIDTH, VOLUME_COLUMN_WIDTH, ID_COLUMN_WIDTH);

        //Iterate over buy and sell orders to build the table body

        Iterator<Order> currentBuyPriceLevel = orderBook.iterateBuyOrders();
        Iterator<Order> currentSellPriceLevel = orderBook.iterateSellOrders();

        while (currentBuyPriceLevel.hasNext() || currentSellPriceLevel.hasNext()) {
            String buyId = padValue("", ID_COLUMN_WIDTH);
            String buyVolume = padValue("", VOLUME_COLUMN_WIDTH);
            String buyPrice = padValue("", PRICE_COLUMN_WIDTH);
            String sellId = padValue("", ID_COLUMN_WIDTH);
            String sellVolume = padValue("", VOLUME_COLUMN_WIDTH);
            String sellPrice = padValue("", PRICE_COLUMN_WIDTH);

            if (currentBuyPriceLevel.hasNext()) {
                Order order = currentBuyPriceLevel.next();
                buyId = padValue(Integer.toString(order.id), ID_COLUMN_WIDTH, true);
                buyVolume = padValue(String.format("%,d", order.getOrderBookQuantity()), VOLUME_COLUMN_WIDTH, true);
                buyPrice = padValue(String.format("%,d", order.price), PRICE_COLUMN_WIDTH, true);
            }

            if (currentSellPriceLevel.hasNext()) {
                Order order = currentSellPriceLevel.next();
                sellId = padValue(Integer.toString(order.id), ID_COLUMN_WIDTH, true);
                sellVolume = padValue(String.format("%,d", order.getOrderBookQuantity()), VOLUME_COLUMN_WIDTH, true);
                sellPrice = padValue(String.format("%,d", order.price), PRICE_COLUMN_WIDTH, true);
            }

            printTableRow(buyId, buyVolume, buyPrice, sellPrice, sellVolume, sellId);
        }

        printTableLine(FULL_TABLE_COLUMN_WIDTH);

    }
}
