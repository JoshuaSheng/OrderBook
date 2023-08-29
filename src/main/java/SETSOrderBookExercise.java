import java.util.Scanner;

public class SETSOrderBookExercise {

    public static void main(String[] args) {

        // This is the entrypoint to your solution - add your code here. args will be empty.
        // Do not remove this file as it's required by our test harness to validate your code before submission.

        Scanner scanner = new Scanner(System.in);
        OrderBook orderBook = new OrderBook();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.trim().isEmpty() || line.contains("#")) {
                continue;
            }
            Order order = OrderParser.fromSimplifiedAscii(line);
            orderBook.addOrder(order);
            OrderBookLogger.printOrderBook(orderBook);
        }
    }
}