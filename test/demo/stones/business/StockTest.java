package demo.stones.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import demo.stones.objects.Order;
import junit.framework.TestCase;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StockTest extends TestCase{
  PumpkinStock stock = PumpkinStock.getInstance();
  @SuppressWarnings("unused")
public void test01MatchedOperation() {
	   
	  Order a = stock.buy(new BigDecimal("10.10"), "1");
	  Order b = stock.sell(new BigDecimal("10.10"), "1");
	  assertTrue("sums match", b.isMatched());
	  Order c = stock.sell(new BigDecimal("10.10"), "2");
	  Order d = stock.buy(new BigDecimal("10.10"), "2");
	  assertTrue("sums match", d.isMatched());
	  System.out.println(stock.getStats());
	  System.out.println(stock.getCustomerInfo("1"));
	  System.out.println(stock.getCustomerInfo("2"));
	  System.out.println(stock.getDeals());
  }
  
  @SuppressWarnings("unused")
public void test02UnmatchedOperation() {
	  Order a = stock.buy(new BigDecimal("10.10"), "3");
	  Order b = stock.sell(new BigDecimal("10.11"), "3");
	  assertFalse("sums not match", b.isMatched());
	  Order c = stock.sell(new BigDecimal("10.11"), "4");
	  Order d = stock.buy(new BigDecimal("10.10"), "4");
	  assertFalse("sums not match", d.isMatched());
	  System.out.println(stock.getStats());
	  System.out.println(stock.getCustomerInfo("3"));
	  System.out.println(stock.getCustomerInfo("4"));
	  System.out.println(stock.getDeals());
  }
  public void test03Example() {
	  stock.reset();
	  assertFalse("This is the first order so it can’t be satisfied yet.",stock.buy(new BigDecimal(10), "A").isMatched());
	  assertFalse("Still no one wants to sell a pumpkin.", stock.buy(new BigDecimal(11), "B").isMatched());
	  assertFalse("Neither A nor B wants such an expensive pumpkin.", stock.sell(new BigDecimal(15), "C").isMatched());
	  assertEquals("Finally a trade can happen. Following the rules above, D will sell his pumpkin for 9€ to the client B.", "B", stock.sell(new BigDecimal(9), "D").getMatchedOrder().getClient());
	  assertFalse("Still no trade.", stock.buy(new BigDecimal(10), "E").isMatched());
	  assertEquals("F sells his pumpkin for 10€ to the client A (as A stated his order earlier than E)", "A", stock.sell(new BigDecimal(10), "F").getMatchedOrder().getClient());
	  assertEquals("Client C is the only one selling a pumpkin now, so the deal is made for 100€", "C", stock.buy(new BigDecimal(100), "G").getMatchedOrder().getClient());
	  System.out.println(stock.getStats());
	  System.out.println(stock.getCustomerInfo("C"));
	  System.out.println(stock.getDeals());
  }
  List<BigDecimal> random;
  int clientNum = 0;
  class Cust implements Runnable {
	 int client = clientNum++;
	 Throwable e;
	 public void run() {
		try {
			try{Thread.sleep(100);}catch(Exception e) {}
			stock.buy(random.get(client), "C"+client);
			try{Thread.sleep(100);}catch(Exception e) {}
			stock.sell(random.get(99-client), "C"+client);
			try{Thread.sleep(100);}catch(Exception e) {}
			stock.sell(random.get(client), "C"+client);
			try{Thread.sleep(100);}catch(Exception e) {}
			stock.buy(random.get(99-client), "C"+client);
		} catch (Throwable e) {
			e.printStackTrace();
			this.e = e;
		}
	 }
  }
  public void test04Concurency() {
	  stock.reset();
	  random = new ArrayList<>();
	  for(int i=0;i<100;i++) {
		  random.add(new BigDecimal(Math.abs(new Random().nextDouble())).setScale(2, BigDecimal.ROUND_HALF_EVEN));//Fanny rounding mode especially with money :)
	  }
	  Cust customers[] = new Cust[100];
	  Thread threads[] = new Thread[100];
	  for(int i=0;i<100;i++) {
		  customers[i] = new Cust();
		  threads[i] = new Thread(customers[i]);
		  threads[i].start();
	  }
	  for(int i=0;i<100;i++) {
		  try{threads[i].join();}catch(Exception e) {}
		  assertNull("No errors expected",customers[i].e);
	  }
	  assertEquals("OrderCount", 400, stock.getStats().getOrderCount());
	  System.out.println(stock.getStats());
	  System.out.println(stock.getDeals());
  }
}
