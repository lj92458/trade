package com.liujun.trade.core.uniswap.api.rpc;

import com.liujun.trade.core.uniswap.api.bean.Book;
import hprose.util.concurrent.Promise;

public interface ProductRpc {
    //最多需要6秒返回
    Promise<Book> bookProduct(String coinPair, String marketOrderSize, String orderStepLength);


    
}
