import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        OrderBook orderBook = new OrderBook();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.trim().isEmpty() || line.contains("#")) {
                continue;
            }
            Order order = OrderParser.fromSimplifiedAscii(line);
            orderBook.addOrder(order);
        }
        OrderBookLogger.printOrderBook(orderBook);
    }
}
