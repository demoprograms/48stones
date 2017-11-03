package demo.stones.business;

import java.math.BigDecimal;

import demo.stones.objects.Order;
import demo.stones.objects.Stats;
/**
 * Stats can be collected after client thread finished
 * by separete thread 
 */
public class StockStats {
	private long waited;
	private long sellCnt;
	private long buyCnt;
	private long matchedCnt;
	private BigDecimal sold = BigDecimal.ZERO.setScale(2);
	private BigDecimal bought = BigDecimal.ZERO.setScale(2);
	
	public synchronized void addSell(Order order) {
		sellCnt++;
	}
	public synchronized void addBuy(Order order) {
		buyCnt++;
	}
	public synchronized void addMatch(Order buy, Order sell) {
		waited += buy.getMatchTime().getTime()-buy.getTime().getTime();
		waited += sell.getMatchTime().getTime()-sell.getTime().getTime();
		matchedCnt ++;
		sold = sold.add(sell.getSellPrice());
		bought = bought.add(buy.getBuyPrice());
	}
	public synchronized void fillStats(Stats stats) {
		stats.setAverageWait((0.0+waited)/matchedCnt);
		stats.setBought(bought);
		stats.setSold(sold);
		stats.setMatchedCount(matchedCnt);
		long orderCnt = sellCnt+buyCnt;
		stats.setOrderCount(orderCnt);
		stats.setUnmatchedCount(orderCnt-matchedCnt*2);
	}
}
