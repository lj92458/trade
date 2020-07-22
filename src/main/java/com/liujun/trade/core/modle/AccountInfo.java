package com.liujun.trade.core.modle;


/**
 * 账户资产信息
 * 
 * @author Administrator
 * 
 */
public class AccountInfo {
	/** 账户全部资产折合货币。包括冻结的货币和货物。不包括借贷的。 */
	private double totalValue;
	/** 活动货币 */
	private double freeMoney;
	/** 活动货物 */
	private double freeGoods;
	/** 冻结货币 */
	private double freezedMoney;
	/** 冻结货物 */
	private double freezedGoods;

	public double getTotalValue() {
		return totalValue;
	}

	public void setTotalValue(double totalValue) {
		this.totalValue = totalValue;
	}

	public double getFreeMoney() {
		return freeMoney;
	}

	public void setFreeMoney(double freeMoney) {
		this.freeMoney = freeMoney;
	}

	public double getFreeGoods() {
		return freeGoods;
	}

	public void setFreeGoods(double freeGoods) {
		this.freeGoods = freeGoods;
	}

	public double getFreezedMoney() {
		return freezedMoney;
	}

	public void setFreezedMoney(double freezedMoney) {
		this.freezedMoney = freezedMoney;
	}

	public double getFreezedGoods() {
		return freezedGoods;
	}

	public void setFreezedGoods(double freezedGoods) {
		this.freezedGoods = freezedGoods;
	}

}
