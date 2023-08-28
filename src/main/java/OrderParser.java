public class OrderParser {
    public static Order fromSimplifiedAscii(String line) {
        String[] args = line.split(",");
        char type = args[0].charAt(0);
        int id = Integer.parseInt(args[1]);
        short price = Short.parseShort(args[2]);
        int quantity = Integer.parseInt(args[3]);
        if (args.length == 5) {
            int peak = Integer.parseInt(args[4]);
            return new IcebergOrder(type, id, price, quantity, peak);
        }
        else {
            return new Order(type, id, price, quantity);
        }
    }
}
