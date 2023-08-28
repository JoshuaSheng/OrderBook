Thanks for considering me at GSA Capital!

The following is ny take on building an order book that supports both limit and iceberg orders. It's based this old
article on implementing trading systems:

https://web.archive.org/web/20110219163418/http://howtohft.wordpress.com/2011/02/15/building-a-trading-system-general-considerations/

To make things easier for you, the reviewer, here's a high level explanation of my system:

### General Flow:

All input is parsed by the OrderParser and turned into Orders. They're inserted into the OrderBook, where they're
executed and turned into trades. Trades are printed immediately, but they're also stored for testing purposes. At the
very end, the order book is printed to stdout. All output is handled by the OrderBookLogger, including both Trades and 
the OrderBook itself.

### OrderBook Design

In order to keep the operations as efficient as possible, The order book is designed as a TreeMap of PriceLevels. PriceLevels
are objects that represent all the orders at a certain price. They store the orders using a linkedlist. 

Since generally for stock exchanges prices tend to be fixed around a certain range (That's why we're using a short!), 
using PriceLevels keeps us from iterating over all orders.

When we add an order to the book, we check if the price level exists. If it does, we can look it up and add it to the
end of the list. If it doesn't we create a new level and store the order on it. Then, we resolve all of the matched orders,
iterating through successive price levels until we can't find a match anymore.

### Efficiency:

For this implementation, adding orders is O(log(p)), where p is the number of distinct number of prices between all of
the orders. Printing the order book is O(n), where n is the number of orders. 

### Improvements and Other Considerations

Even though TreeMaps are generally O(logn), because we're storing prices, in practice the number of different prices
is much less than the number of orders. If we wanted to make this even more efficient at the cost of indexing all of the 
orders, we could use a proper hashmap with the price as the key and the PriceLevel as the value. This would mean that we 
wouldn't have to use a treemap if we just wanted to insert an order.

Another consideration is supporting deletions. We could do this by implementing a hashmap between orders and their
location in the linked list. (Maybe keyed by the order_id?). This would allow us to delete, again, in O(1) at the cost
of indexing all the orders.

