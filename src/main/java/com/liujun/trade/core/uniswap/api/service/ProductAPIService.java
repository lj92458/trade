package com.liujun.trade.core.uniswap.api.service;

import com.liujun.trade.core.uniswap.api.bean.Book;

public interface ProductAPIService {
     /**
      *
      * @param coinPair
      * @param marketOrderSize
      * @param orderStepLength 两个相邻的挂单之间价格差距多少？对少于这个差距的挂单合并。建议为价格的万一/万五
      * @return
      */
     Book bookProductsByProductId(String coinPair, String marketOrderSize, String orderStepLength);
}
