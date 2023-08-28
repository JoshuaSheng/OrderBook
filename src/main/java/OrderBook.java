import java.lang.reflect.Array;
import java.util.*;

public class OrderBook {
    private final TreeMap<Short, PriceLevel> buyOrders;
    private final TreeMap<Short, PriceLevel> sellOrders;
    ArrayList<Trade> trades = new ArrayList<>();

    OrderBook() {
        buyOrders = new TreeMap<>(Comparator.reverseOrder());
        sellOrders = new TreeMap<>();
    }

    private void matchTrades() {
        Map.Entry<Short, PriceLevel> buyPriceEntry = buyOrders.firstEntry();
        Map.Entry<Short, PriceLevel> sellPriceEntry = sellOrders.firstEntry();

        while (buyPriceEntry != null && sellPriceEntry != null && buyPriceEntry.getKey() >= sellPriceEntry.getKey()) {
            PriceLevel buyPriceLevel = buyPriceEntry.getValue();
            PriceLevel sellPriceLevel = sellPriceEntry.getValue();

            while (!buyPriceLevel.orders.isEmpty() && !sellPriceLevel.orders.isEmpty()) {
                //find min vol, subtract from both, create and print a trade
                Order earliestBuy = buyPriceLevel.orders.getFirst();
                Order earliestSell = sellPriceLevel.orders.getFirst();

                int tradeVolume = Math.min(earliestBuy.quantity, earliestSell.quantity);
                short executionPrice = (earliestBuy.timeCreated < earliestSell.timeCreated) ? earliestBuy.price : earliestSell.price;
                earliestBuy.quantity -= tradeVolume;
                earliestSell.quantity -= tradeVolume;

                Trade executedTrade = new Trade(earliestBuy.id, earliestSell.id, executionPrice, tradeVolume);
                trades.add(executedTrade);
                OrderBookLogger.printTrade(executedTrade);

                if (earliestBuy.quantity == 0) {
                    buyPriceLevel.orders.removeFirst();
                }
                else {
                    sellPriceLevel.orders.removeFirst();
                }
            }
            if (buyPriceLevel.orders.isEmpty()) {
                buyOrders.remove(buyPriceLevel.price);
                buyPriceEntry = buyOrders.firstEntry();
            }
            if (sellPriceLevel.orders.isEmpty()) {
                sellOrders.remove(sellPriceLevel.price);
                sellPriceEntry = sellOrders.firstEntry();
            }
        }
    }

    public void addOrder(Order order) {
        TreeMap<Short, PriceLevel> pricesList = (order.type == 'B') ? buyOrders : sellOrders;

        if (pricesList.containsKey(order.price)) {
            pricesList.get(order.price).addOrder(order);
        }
        else {
            PriceLevel newPriceLevel = new PriceLevel(order.price);
            newPriceLevel.addOrder(order);
            pricesList.put(order.price, newPriceLevel);
        }

        matchTrades();
    }

    public Iterator<Order> iterateBuyOrders() {
        return new PriceLevelOrderIterator(this.buyOrders);
    }

    public Iterator<Order> iterateSellOrders() {
        return new PriceLevelOrderIterator(this.sellOrders);
    }
}

class PriceLevel implements Comparable<PriceLevel>{
    short price;
    LinkedList<Order> orders = new LinkedList<>();

    PriceLevel(short price) {
        this.price = price;
    }

    public void addOrder(Order order) {
        orders.add(order);
    }

    public int compareTo(PriceLevel other) {
        return Short.compare(price, other.price);
    }
}

class PriceLevelOrderIterator implements Iterator<Order> {
    private final Iterator<PriceLevel> priceLevelIterator;
    private Iterator<Order> currentOrderIterator;

    public PriceLevelOrderIterator(TreeMap<Short, PriceLevel> priceLevelMap) {
        this.priceLevelIterator = priceLevelMap.values().iterator();
        this.currentOrderIterator = null;
    }

    @Override
    public boolean hasNext() {
        while (currentOrderIterator == null || !currentOrderIterator.hasNext()) {
            if (priceLevelIterator.hasNext()) {
                currentOrderIterator = priceLevelIterator.next().orders.iterator();
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    public Order next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return currentOrderIterator.next();
    }
}