package com.liujun.trade.core.uniswap.api.service.impl;
import com.liujun.trade.core.uniswap.api.bean.APIConfiguration;
import com.liujun.trade.core.uniswap.api.bean.AddOrderResult;
import com.liujun.trade.core.uniswap.api.service.OrderAPIService;
public class OrderApiServiceImpl implements OrderAPIService{

    public OrderApiServiceImpl(APIConfiguration config) {
    }

    @Override
    public AddOrderResult addOrder(String CoinPair, String orderType, String price, String volume) {
        return null;
    }

    @Override
    public String queryOrder(String coinPair, String orderId) {
        return null;
    }

    @Override
    public void cancelOrder(String coinPair, String orderId) {

    }
}
