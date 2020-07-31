package com.liujun.trade.core.uniswap.api.service.impl;

import com.liujun.trade.core.uniswap.api.bean.APIConfiguration;
import com.liujun.trade.core.uniswap.api.bean.Book;
import com.liujun.trade.core.uniswap.api.rpc.ProductRpc;
import com.liujun.trade.core.uniswap.api.rpc.RpcClient;
import com.liujun.trade.core.uniswap.api.service.ProductAPIService;

public class ProductAPIServiceImpl implements ProductAPIService {
    APIConfiguration config;
    ProductRpc productRpc;

    public ProductAPIServiceImpl(APIConfiguration config) {
        this.config = config;
        this.productRpc = RpcClient.getInstance(config.getUri()).useService(ProductRpc.class);
    }

    @Override
    public Book bookProductsByProductId(String coinPair, String marketOrderSize, String orderStepLength) {


        try {
            Book book = productRpc.bookProduct(coinPair, marketOrderSize, orderStepLength).toFuture().get();
            return book;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
