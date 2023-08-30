import java.util.*;

public class OrderBook {
    private final SortedMap<Short, PriceLevel> sortedBuyPrices = new TreeMap<>(Comparator.reverseOrder());
    private final SortedMap<Short, PriceLevel> sortedSellPrices = new TreeMap<>();
    private final Map<Short, PriceLevel> buyPrices = new HashMap<>();
    private final Map<Short, PriceLevel> sellPrices = new HashMap<>();
    //for testing purposes
    ArrayList<Trade> trades = new ArrayList<>();

    private void createTradeIfNotExists(Order bid, Order ask, Map<Tuple<Order, Order>, Trade> tradeByOrderIds, List<Trade> pendingTrades) {
        int tradeVolume = Math.min(bid.quantity, ask.quantity);
        short tradePrice = (bid.timestamp < ask.timestamp) ? bid.price : ask.price;

        bid.decreaseQuantity(tradeVolume);
        ask.decreaseQuantity(tradeVolume);

        Tuple<Order, Order> orderIds = new Tuple<>(bid, ask);
        if (tradeByOrderIds.containsKey(orderIds)) {
            Trade trade = tradeByOrderIds.get(orderIds);
            trade.quantity += tradeVolume;
        }
        else {
            Trade trade = new Trade(bid.id, ask.id, tradePrice, tradeVolume);
            trades.add(trade);
            pendingTrades.add(trade);
            tradeByOrderIds.put(orderIds, trade);
        }
    }

    private Order clearOrderIfComplete(Order order, OrderBookIterator orderBookIterator) {
        if (order.isExecuted) {
            if (order.quantity > 0) {
                order.isExecuted = false;
                orderBookIterator.addToPriceLevel(order);
            }
            orderBookIterator.remove();
            return orderBookIterator.hasNext() ? orderBookIterator.next() : null;
        }
        else {
            return order;
        }
    }

    private void matchOrders() {
        Map<Tuple<Order, Order>, Trade> tradeByOrderIds = new HashMap<>();
        List<Trade> pendingTrades = new LinkedList<>();

        OrderBookIterator buyOrderIterator = iterateBuyOrders();
        OrderBookIterator sellOrderIterator = iterateSellOrders();

        Order topBuy = buyOrderIterator.hasNext() ? buyOrderIterator.next() : null;
        Order topSell = sellOrderIterator.hasNext() ? sellOrderIterator.next() : null;

        while (topBuy != null && topSell != null && topBuy.price >= topSell.price) {
            createTradeIfNotExists(topBuy, topSell, tradeByOrderIds, pendingTrades);

            topBuy = clearOrderIfComplete(topBuy, buyOrderIterator);
            topSell = clearOrderIfComplete(topSell, sellOrderIterator);
        }

        for (Trade trade: pendingTrades) {
            OrderBookLogger.printTrade(trade);
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

        if (order instanceof IcebergOrder) {
            ((IcebergOrder) order).refillQuantity();
        }
    }

    public OrderBookIterator iterateBuyOrders() {
        return new OrderBookIterator(this.sortedBuyPrices);
    }

    public OrderBookIterator iterateSellOrders() {
        return new OrderBookIterator(this.sortedSellPrices);
    }

    //represents all orders of a given price on either the bid or ask side.
    static class PriceLevel implements Comparable<PriceLevel>{
        short price;
        LinkedList<Order> orders = new LinkedList<>();
        //we use nextOrders to avoid adding to a list we're currently iterating on.
        //we only add to nextOrders, and whenever we finish iterating on orders, we load in nextOrders
        LinkedList<Order> nextOrders = new LinkedList<>();

        PriceLevel(short price) {
            this.price = price;
        }

        public void addOrder(Order order) {
            nextOrders.add(order);
        }

        public int compareTo(PriceLevel other) {
            return Short.compare(price, other.price);
        }
    }

    //iterates over all the bids or asks in priority order
    protected static class OrderBookIterator implements Iterator<Order> {
        private PriceLevel currentPriceLevel;
        private final Iterator<PriceLevel> currentPriceIterator;
        private ListIterator<Order> currentOrderIterator;

        public OrderBookIterator(SortedMap<Short, PriceLevel> priceLevelMap) {
            this.currentPriceIterator = priceLevelMap.values().iterator();
            this.currentOrderIterator = null;
            this.currentPriceLevel = null;
        }

        private void updatePriceLevelOrders() {
            currentPriceLevel.orders.addAll(currentPriceLevel.nextOrders);
            currentPriceLevel.nextOrders = new LinkedList<>();
            currentOrderIterator = this.currentPriceLevel.orders.listIterator();
        }

        @Override
        public boolean hasNext() {

            while (currentOrderIterator == null || !currentOrderIterator.hasNext()) {
                //update previous PriceLevel
                if (currentOrderIterator != null && !currentPriceLevel.nextOrders.isEmpty()) {
                    updatePriceLevelOrders();
                    break;
                }

                //update next PriceLevel
                if (currentPriceIterator.hasNext()) {
                    currentPriceLevel = currentPriceIterator.next();
                    updatePriceLevelOrders();
                    currentOrderIterator = this.currentPriceLevel.orders.listIterator();
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

        public void addToPriceLevel(Order order) {
            currentPriceLevel.nextOrders.add(order);
        }

        @Override
        public void remove() {
            currentOrderIterator.remove();
        }
    }
}