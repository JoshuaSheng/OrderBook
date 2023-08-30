## SETSmm Iceberg Order Exercise

The following is my take on building an order book that supports both limit and iceberg orders. It's based this old
article on implementing trading systems:

https://web.archive.org/web/20110219163418/http://howtohft.wordpress.com/2011/02/15/building-a-trading-system-general-considerations/

### General Flow:

All input is parsed by the OrderParser and turned into Orders. They're inserted into the OrderBook, where they're
executed and turned into trades. Trades are printed immediately, but they're also stored for testing purposes. At the
very end, the order book is printed to stdout. All output is handled by the OrderBookLogger, including both Trades and 
the OrderBook itself.

### OrderBook Design

In order to keep the operations as efficient as possible, The order book is designed as a SortedMap of PriceLevels. PriceLevels
are objects that represent all the orders on one side of the spread, and store the orders inside a linked list.

The assumption I made here is that generally, spreads tend to be fixed around a certain fairly narrow range of values.
(that's why we're using a Short!). By using Price Levels, we don't have to iterate over every single order on insertion
and execution.

When we add an order to the book, we check if the price level exists. If it does, we can look it up and add it to the
end of the list. If it doesn't we create a new level and store the order on it. Then, we match all the orders based
on priority (price and then order created), and create and output trades.

### Efficiency:

For this implementation, adding orders is O(log(p)), where p is the number of distinct number of prices between all the 
orders if there has never been an order with that price. If there has, then adding the order is O(1). 

If this results in an execution, then it becomes O(t), where t is the number of trades. 

Printing the orderbook is O(n), where n is the number of orders. 

### Improvements and Other Considerations

Even though TreeMaps are generally O(logn), because we're storing prices, in practice the number of different prices
is much less than the number of orders. 

We could potentially support  deletions using this implementation. We could do this by implementing a hashmap between orders and their
location in the linked list. (Maybe keyed by the order_id if it is unique?). This would allow us to delete in O(1) at the cost
of indexing all the orders.

