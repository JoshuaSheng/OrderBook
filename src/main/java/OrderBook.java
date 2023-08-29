import java.util.*;

public class OrderBook {
    private final SortedMap<Short, PriceLevel> sortedBuyPrices = new TreeMap<>(Comparator.reverseOrder());
    private final SortedMap<Short, PriceLevel> sortedSellPrices = new TreeMap<>();
    private final Map<Short, PriceLevel> buyPrices = new HashMap<>();
    private final Map<Short, PriceLevel> sellPrices = new HashMap<>();
    ArrayList<Trade> trades = new ArrayList<>();

    private void createTrade(Order bid, Order ask) {
        int tradeVolume = Math.min(bid.quantity, ask.quantity);
        short tradePrice = (bid.instanceNumber < ask.instanceNumber) ? bid.price : ask.price;
        bid.quantity -= tradeVolume;
        ask.quantity -= tradeVolume;

        Trade trade = new Trade(bid.id, ask.id, tradePrice, tradeVolume);
        trades.add(trade);
        OrderBookLogger.printTrade(trade);
    }

    private void matchOrders() {
        Iterator<Order> buyOrderIterator = iterateBuyOrders();
        Iterator<Order> sellOrderIterator = iterateSellOrders();

        Order topBuy = buyOrderIterator.hasNext() ? buyOrderIterator.next() : null;
        Order topSell = sellOrderIterator.hasNext() ? sellOrderIterator.next() : null;

        while (topBuy != null && topSell != null && topBuy.price >= topSell.price) {
            createTrade(topBuy, topSell);

            if (topBuy.quantity == 0) {
                buyOrderIterator.remove();
                topBuy = buyOrderIterator.hasNext() ? buyOrderIterator.next() : null;
            }
            if (topSell.quantity == 0) {
                sellOrderIterator.remove();
                topSell = sellOrderIterator.hasNext() ? sellOrderIterator.next() : null;
            }
        }
    }

    public void addOrder(Order order) {
        SortedMap<Short, PriceLevel> sortedPrices = (order.type == 'B') ? sortedBuyPrices : sortedSellPrices;
        Map<Short, PriceLevel> prices = (order.type == 'B') ? buyPrices : sellPrices;

        if (prices.containsKey(order.price)) {
            prices.get(order.price).addOrder(order);
        }
        else {
            PriceLevel newPriceLevel = new PriceLevel(order.price);
            newPriceLevel.addOrder(order);
            sortedPrices.put(order.price, newPriceLevel);
            prices.put(order.price, newPriceLevel);
        }

        matchOrders();
    }

    public Iterator<Order> iterateBuyOrders() {
        return new OrderBookIterator(this.sortedBuyPrices);
    }

    public Iterator<Order> iterateSellOrders() {
        return new OrderBookIterator(this.sortedSellPrices);
    }

    //represents all orders of a given price on either the bid or ask side.
    static class PriceLevel implements Comparable<PriceLevel>{
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

    //iterates over all the bids or asks in priority order
    private static class OrderBookIterator implements Iterator<Order> {
        private final Iterator<PriceLevel> currentPriceIterator;
        private Iterator<Order> currentOrderIterator;

        public OrderBookIterator(SortedMap<Short, PriceLevel> priceLevelMap) {
            this.currentPriceIterator = priceLevelMap.values().iterator();
            this.currentOrderIterator = null;
        }

        @Override
        public boolean hasNext() {
            while (currentOrderIterator == null || !currentOrderIterator.hasNext()) {
                if (currentPriceIterator.hasNext()) {
                    currentOrderIterator = currentPriceIterator.next().orders.iterator();
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

        @Override
        public void remove() {
            currentOrderIterator.remove();
        }
    }
}