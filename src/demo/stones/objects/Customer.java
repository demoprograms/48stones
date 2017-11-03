package demo.stones.objects;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Collects customer info and used to transferer it to GUI*/
public class Customer {
	private String id;
	private List<Order> orders = Collections.synchronizedList(new ArrayList<>());
	
	private BigDecimal moneyBalance = BigDecimal.ZERO.setScale(2);
	private int pupkinBalance;
	private int wantsToBuy;
	private BigDecimal wantsToBuyAmount = BigDecimal.ZERO.setScale(2);
	private int wantsToSell;
	private BigDecimal wantsToSellAmount = BigDecimal.ZERO.setScale(2);
	
	public Customer() {}
	public Customer(String client) {
		id = client;
	}
	public String toString() {
		refreshStats();
		StringBuilder result = new StringBuilder();
		result.append(id).append(" has ").append(pupkinBalance).append(" pupkins and ").append(moneyBalance).append("€");
		if(wantsToBuy>0 || wantsToSell>0) {
		  result.append(" wants ");
		  if(wantsToSell>0) {
			  result.append(" sell ").append(wantsToSell).append(" pupkins for ").append(wantsToSellAmount);
		  }
		  if(wantsToBuy>0) {
			  result.append(" buy ").append(wantsToBuy).append(" pupkins for ").append(wantsToBuyAmount);
		  }
		}
		return result.toString();
	}
	public void refreshStats() {
		pupkinBalance = 0;
		moneyBalance = BigDecimal.ZERO.setScale(2);
		wantsToBuy = 0;
		wantsToBuyAmount = BigDecimal.ZERO.setScale(2);
		wantsToSell = 0;
		wantsToSellAmount = BigDecimal.ZERO.setScale(2);
		for(int i=0; i<orders.size(); i++) {
			Order o = orders.get(i);
			if(o.getSellPrice() != null) {
				if(o.isMatched()) {
					pupkinBalance--;
					moneyBalance = moneyBalance.add(o.getSellPrice());
				} else {
					wantsToSell++;
					wantsToSellAmount = wantsToSellAmount.add(o.getSellPrice());
				}
			} else {
				if(o.isMatched()) {
					pupkinBalance++;
					moneyBalance = moneyBalance.subtract(o.getBuyPrice());
				} else {
					wantsToBuy++;
					wantsToBuyAmount = wantsToBuyAmount.add(o.getBuyPrice());
				}
			}			
		}
	}
	
	public void addOrder(Order order) {
		orders.add(order);
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Order> getOrders() {
		return orders;
	}

	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}


}
