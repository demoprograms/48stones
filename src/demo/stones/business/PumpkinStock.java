package demo.stones.business;

import java.util.List;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import demo.stones.objects.Customer;
import demo.stones.objects.Order;
import demo.stones.objects.Stats;

public class PumpkinStock {
  private static PumpkinStock stock = new PumpkinStock();
  private StockStats stats = new StockStats();
  private PumpkinStock() {}
  
  private List<Order> buy = Collections.synchronizedList(new ArrayList<>());
  private List<Order> sell = Collections.synchronizedList(new ArrayList<>());
  private HashMap<String, Customer> customers = new HashMap<>();
  private List<Order> deals = Collections.synchronizedList(new ArrayList<>());
  public void reset() {
	  stats = new StockStats();
	  buy = Collections.synchronizedList(new ArrayList<>());
	  sell = Collections.synchronizedList(new ArrayList<>());
	  customers = new HashMap<>();
	  deals = Collections.synchronizedList(new ArrayList<>());
  }
  private synchronized Order matchBuy(BigDecimal price) {
	  int minPrice = -1;
	  for(int i=0;i<sell.size();i++) {
		  Order o = sell.get(i);
		  if(o!=null && !o.isMatched() && o.getSellPrice().compareTo(price)<=0) {
			  if(minPrice == -1) {
				  minPrice = i;
			  } else if(sell.get(minPrice).getSellPrice().compareTo(o.getSellPrice())<0) {
				  minPrice = i;
			  }
		  }
	  }
	  if(minPrice>-1) {
		  Order o = sell.get(minPrice);
		  o.setMatched(true);
		  sell.set(minPrice, null);
		  return o;
	  }
	  return null;
  }
  private synchronized Order matchSell(BigDecimal price) {
	  int maxPrice = -1;
	  for(int i=0;i<buy.size();i++) {
		  Order o = buy.get(i);
		  if(o!=null && !o.isMatched() && o.getBuyPrice().compareTo(price)>=0) {
			  if(maxPrice == -1) {
				  maxPrice = i;
			  } else if(buy.get(maxPrice).getBuyPrice().compareTo(o.getBuyPrice())<0) {
				  maxPrice = i;
			  }
		  }
	  }
	  if(maxPrice>-1) {
		  Order o = buy.get(maxPrice);
		  o.setMatched(true);
		  buy.set(maxPrice, null);
		  return o;
	  }
	  return null;
  }
  
  public static PumpkinStock getInstance() {
	  return stock;
  }
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
  private void addBuy(Order order) {
	  buy.add(order);
	  stats.addBuy(order);
	  customerStats(order);
  }
  private void addSell(Order order) {
	  sell.add(order);
	  stats.addSell(order);
	  customerStats(order);
  }
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
		  addBuy(newOrder);
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
		  addSell(newOrder);
	  }
	  return newOrder;
  }
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
