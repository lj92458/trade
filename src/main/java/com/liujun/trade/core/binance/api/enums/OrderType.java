package com.liujun.trade.core.binance.api.enums;

public enum OrderType {
    LIMIT,// 限价单
    MARKET,// 市价单
    STOP_LOSS,//止损单
    STOP_LOSS_LIMIT,//限价止损单
    TAKE_PROFIT,// 止盈单
    TAKE_PROFIT_LIMIT,// 限价止盈单
    LIMIT_MAKER,// 限价卖单
}
