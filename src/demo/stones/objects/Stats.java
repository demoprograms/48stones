package demo.stones.objects;

import java.math.BigDecimal;
import java.util.List;
/**
 * Structure to collect statistics for (NON-)GUI admin client
 */
public class Stats {
	private List<Order> last;
	private List<Order> lastMatched;
	private List<Order> queue;
	private long matchedCount;	
	private long unmatchedCount;
	private long orderCount;
	private double averageWait;
	private BigDecimal sold = BigDecimal.ZERO.setScale(2);
	private BigDecimal bought = BigDecimal.ZERO.setScale(2);
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("Order count ").append(orderCount).append("\n");
		result.append("Match count ").append(matchedCount).append("\n");
		result.append("Unmatch count ").append(unmatchedCount).append("\n");
		result.append("Average waiting time(ms) ").append(averageWait).append("\n");
		result.append("Sold amount ").append(sold).append("\n");
		result.append("Bought amount ").append(bought).append("\n");		
		result.append("Stock win ").append(bought.subtract(sold)).append("\n");
		result.append("\n\n");
		return result.toString();
	}
	public List<Order> getLast() {
		return last;
	}
	public void setLast(List<Order> last) {
		this.last = last;
	}
	public List<Order> getLastMatched() {
		return lastMatched;
	}
	public void setLastMatched(List<Order> lastMatched) {
		this.lastMatched = lastMatched;
	}
	public List<Order> getQueue() {
		return queue;
	}
	public void setQueue(List<Order> queue) {
		this.queue = queue;
	}
	public long getMatchedCount() {
		return matchedCount;
	}
	public void setMatchedCount(long matchedCount) {
		this.matchedCount = matchedCount;
	}
	public long getUnmatchedCount() {
		return unmatchedCount;
	}
	public void setUnmatchedCount(long unmatchedCount) {
		this.unmatchedCount = unmatchedCount;
	}
	public long getOrderCount() {
		return orderCount;
	}
	public void setOrderCount(long orderCount) {
		this.orderCount = orderCount;
	}
	public double getAverageWait() {
		return averageWait;
	}
	public void setAverageWait(double averageWait) {
		this.averageWait = averageWait;
	}
	public BigDecimal getSold() {
		return sold;
	}
	public void setSold(BigDecimal sold) {
		this.sold = sold;
	}
	public BigDecimal getBought() {
		return bought;
	}
	public void setBought(BigDecimal bought) {
		this.bought = bought;
	}
	
}
