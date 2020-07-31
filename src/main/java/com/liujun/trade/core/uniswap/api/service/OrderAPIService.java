package com.liujun.trade.core.uniswap.api.service;

import com.liujun.trade.core.uniswap.api.bean.AddOrderResult;

public interface OrderAPIService {

    /**
     * 挂单。
     * @param CoinPair 格式：goods-money，例如：eth-usdc
     * @param orderType buy,sell
     * @param price
     * @param volume
     * @return
     */
    AddOrderResult addOrder(String CoinPair, String orderType, String price, String volume);

    //返回status
    String queryOrder(String coinPair, String orderId);

    void cancelOrder(String coinPair, String orderId);
}
