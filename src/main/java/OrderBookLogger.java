import java.text.NumberFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;

public class OrderBookLogger {
    public static void printTrade(Trade trade) {
        System.out.printf("%d,%d,%d,%d", trade.buyOrderId, trade.sellOrderId, trade.price, trade.quantity);
    }

    /*
    Prints a line like so:
    +-----+--------+----+
    with the number of dashes determined by the length of the columns
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
    with the contents between each bar determined by the value parameters
     */
    private static void printTableRow(String... values) {
        StringBuilder line = new StringBuilder("|");
        for (String value: values) {
            line.append(value).append('|');
        }
        System.out.println(line);
    }

    private static String padValue(String value, int totalLength) {
        return padValue(value, totalLength, false);
    }

    /*
    passes a value to make it exactly totalLength, and right or left justified. the direction it is justified has 1
    space of padding.
     */
    private static String padValue(String value, int totalLength, boolean rightJustify) {
        StringBuilder formattedValue = new StringBuilder();
        int padding = 1;
        int whitespaceLength = totalLength - value.length() - padding;
        if (rightJustify) {
            formattedValue.append(" ".repeat(whitespaceLength));
            formattedValue.append(value);
            formattedValue.append(" ".repeat(padding));
        }
        else {
            formattedValue.append(" ".repeat(padding));
            formattedValue.append(value);
            formattedValue.append(" ".repeat(whitespaceLength));
        }
        return formattedValue.toString();
    }

    public static void printOrderBook(OrderBook orderBook) {
        int TOTAL_WIDTH = 67;
        int ID_COLUMN_WIDTH = 10;
        int VOLUME_COLUMN_WIDTH = 13;
        int PRICE_COLUMN_WIDTH = 7;

        int fullTableColumnWidth = TOTAL_WIDTH - 2;
        int halfTableColumnWidth = (TOTAL_WIDTH - 3)/2;

        //Table Header
        printTableLine(fullTableColumnWidth);
        printTableRow(
                padValue("BUY", halfTableColumnWidth),
                padValue("SELL", halfTableColumnWidth)
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

        //Iterate over buy and sell orders to build table body

        Iterator<Order> currentBuyPriceLevel = orderBook.iterateBuyOrders();
        Iterator<Order> currentSellPriceLevel = orderBook.iterateSellOrders();

        while (currentBuyPriceLevel.hasNext() || currentSellPriceLevel.hasNext()) {
            String buyId = "";
            String buyVolume = "";
            String buyPrice = "";
            String sellId = "";
            String sellVolume = "";
            String sellPrice = "";

            if (currentBuyPriceLevel.hasNext()) {
                Order order = currentBuyPriceLevel.next();
                buyId = padValue(Integer.toString(order.id), ID_COLUMN_WIDTH, true);
                buyVolume = padValue(String.format("%,d", order.getOrderBookQuantity()), VOLUME_COLUMN_WIDTH, true);
                buyPrice = padValue(String.format("%,d", order.price), PRICE_COLUMN_WIDTH, true);
            }

            if (currentSellPriceLevel.hasNext()) {
                Order order = currentBuyPriceLevel.next();
                sellId = padValue(Integer.toString(order.id), ID_COLUMN_WIDTH, true);
                sellVolume = padValue(String.format("%,d", order.getOrderBookQuantity()), VOLUME_COLUMN_WIDTH, true);
                sellPrice = padValue(String.format("%,d", order.price), PRICE_COLUMN_WIDTH, true);
            }

            printTableRow(buyId, buyVolume, buyPrice, sellPrice, sellVolume, sellId);
        }

        printTableLine(fullTableColumnWidth);

    }
}
