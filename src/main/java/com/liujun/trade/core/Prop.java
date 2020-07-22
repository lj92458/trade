package com.liujun.trade.core;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by fengping on 2017/5/14.
 */
@Component
public class Prop {
    @Value("${trade.formatGoodsStr}")
    public  String formatGoodsStr;
    @Value("${trade.formatMoneyStr}")
    public  String formatMoneyStr;
    @Value("${trade.minCoinNum}")
    public  Double minCoinNum;//买卖币时，最小交易金额
    @Value("${trade.moneyPrice}")
    public  Double moneyPrice;//计价货币的人民币价格
    public  Double minMoney;//一次最少要赚的钱
    public  Double huaDian;//滑点，用来强制调平资金
    public  Double huaDian2;//滑点，正常下单时，为了买到。
    public  DecimalFormat fmt_goods;
    public  DecimalFormat fmt_money;

    @Value("${trade.goods}")
    public String goods;
    @Value("${trade.money}")
    public String money;
    @Value("${time_sleep}")
    public int time_sleep;
    @Value("${trade.orderStepLength}")
    public Double orderStepLength;//按价格合并订单，例如：0.1或0.001
    @Value("${trade.marketOrderSize}")
    public int marketOrderSize;// 获取多少个市场挂单？

    @PostConstruct
    public void init(){
        minMoney = 0.1 / this.moneyPrice;//一次最少要赚的钱
        huaDian = 3.0 / this.moneyPrice;//滑点，用来强制调平资金
        huaDian2 = 0.5 / this.moneyPrice;//滑点，正常下单时，为了买到。
        fmt_goods = new DecimalFormat(this.formatGoodsStr);
        fmt_goods.setRoundingMode(RoundingMode.HALF_UP);

        fmt_money = new DecimalFormat(this.formatMoneyStr);
        fmt_money.setRoundingMode(RoundingMode.HALF_UP);


    }

    public  Double formatMoney(Double money) {
        synchronized (fmt_money) {
            return Double.parseDouble(fmt_money.format(money));
        }
    }

    public  Double formatGoods(Double goods) {
        synchronized (fmt_goods) {
            return Double.parseDouble(fmt_goods.format(goods));
        }
    }
}
