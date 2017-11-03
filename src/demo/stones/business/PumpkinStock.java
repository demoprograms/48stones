package demo.stones.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

import demo.stones.objects.Customer;
import demo.stones.objects.Order;
import demo.stones.objects.Stats;
/** Main class of Demo */
public class PumpkinStock {
  /* Singleton */
  private static PumpkinStock stock = new PumpkinStock();
  /* Monitoring structure */
  private StockStats stats = new StockStats();
  private PumpkinStock() {
	  //Thread to match sell and buy that not matched at once due to concurency
	  //Thread to clean up buy and sell maps from matched orders by creating new Map and registering in parallel orders in new and old and then substituting old map with new
	  //Thread to put orders to statistics
  }
  
  /** Buy and Sell operations in separate maps for fast navigation and to avoid money calculation problems */
  private NavigableMap<BigDecimal, List<Order>> buy = new ConcurrentSkipListMap<>();
  private NavigableMap<BigDecimal, List<Order>> sell = new ConcurrentSkipListMap<>();

  /** Order tracking by customer*/
  private HashMap<String, Customer> customers = new HashMap<>();
  /** Closed deals*/
  private List<Order> deals = Collections.synchronizedList(new ArrayList<>());
  
  /** For Test porpuses*/
  public void reset() {
	  stats = new StockStats();
	  buy = new ConcurrentSkipListMap<>();
	  sell = new ConcurrentSkipListMap<>();
	  customers = new HashMap<>();
	  deals = Collections.synchronizedList(new ArrayList<>());
  }
    /** Search from lowest selling price to highest*/
	private Order matchBuy(BigDecimal price) {
		if(sell.size()>0) {
			BigDecimal f = sell.firstKey();
			while (f != null && price.compareTo(f) >= 0) {
				List<Order> ord = sell.get(f);
				for (int i = 0; i < ord.size(); i++) {
					Order o = ord.get(i);
					if (!o.isMatched() && o.registerMatch()) {
						return o;
					}
				}
				f = sell.higherKey(f);
			}
		}
		return null;
	}
	/** Search from highest buy price to lowest*/
	private Order matchSell(BigDecimal price) {
		if(buy.size()>0) {
			BigDecimal f = buy.lastKey();
			while (f != null && price.compareTo(f) <= 0) {
				List<Order> ord = buy.get(f);
				for (int i = 0; i < ord.size(); i++) {
					Order o = ord.get(i);
					if (!o.isMatched() && o.registerMatch()) {
						return o;
					}
				}
				f = buy.lowerKey(f);
			}
		}
		return null;
	}

  /** Singleton can be also configured with Spring*/
  public static PumpkinStock getInstance() {
	  return stock;
  }
  /** Collecting orders by customer */
  private void customerStats(Order order) {
	  String client = order.getClient();
	  Customer cust = customers.get(client);
	  if(cust == null) {
		  cust = new Customer(client);
		  Customer cust2 = customers.put(client, cust);
		  if(cust2 != null) {
			  cust.setOrders(cust2.getOrders());
		  }
	  }
	  cust.addOrder(order);
  }
  /** Register order in sell or by maps*/
  private void registerOrder(Order order, NavigableMap<BigDecimal, List<Order>> map) {
	  List<Order> data = map.get(order.getPrice());
	  if(data == null) {
		  data = Collections.synchronizedList(new ArrayList<>());
		  List<Order> data2 = map.put(order.getPrice(), data);
		  if(data2!=null) {
			  data.addAll(data2);
		  }
	  }
	  data.add(order);
  }
  /** Add order to maps if pair not found at once*/
  private void add(Order order, NavigableMap<BigDecimal, List<Order>> map) {
	  registerOrder(order, map);
	  stats.addBuy(order);
	  customerStats(order);
  }
  

  /** Link 2 orders together*/
  private void syncMatch(Order match, Order newOrder) {
	  newOrder.setMatched(true);
	  newOrder.setMatchTime(new Date());
	  newOrder.setMatchedOrder(match);
	  match.setMatchTime(new Date());
	  match.setMatchedOrder(newOrder);
	  if(newOrder.getSellPrice()==null) {
		stats.addSell(newOrder);
	    stats.addMatch(newOrder, match);
	  } else {
		stats.addBuy(newOrder);
		stats.addMatch(match, newOrder);
	  }
	  deals.add(newOrder);
  }
  
  
  //Operational methods
  public Order buy(BigDecimal price, String client) {
	  if(price==null || BigDecimal.ZERO.compareTo(price)>0) {
		  throw new RuntimeException("Invalid price");
	  }
	  Order match = matchBuy(price);
	  Order newOrder = new Order(client);
	  newOrder.setBuyPrice(price);
	  if(match!=null) {
		  syncMatch(match, newOrder);		  
	  } else {
		  add(newOrder, buy);
	  }
	  return newOrder;
  }
  public Order sell(BigDecimal price, String client) {
	  if(price==null || BigDecimal.ZERO.compareTo(price)>0) {
		  throw new RuntimeException("Invalid price");
	  }
	  Order match = matchSell(price);
	  Order newOrder = new Order(client);
	  newOrder.setSellPrice(price);	  
	  if(match!=null) {
		  syncMatch(match, newOrder);		  
	  } else {
		  add(newOrder, sell);
	  }
	  return newOrder;
  }
  //Statistics methods
  
  public Customer getCustomerInfo(String client) {
	  return customers.get(client);
  }
  public Stats getStats() {
	  Stats result = new Stats();
	  stats.fillStats(result);
	  return result;
  }
  public List<Order> getDeals() {
	  return deals;
  }
}
