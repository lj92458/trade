package com.liujun.trade.core.binance.api.enums;

/**
 * 有效方式
 */
public enum TimeInForce {
    GTC,// 成交为止
    IOC,// 无法立即成交的部分就撤销
    FOK,// 无法全部立即成交就撤销
}
