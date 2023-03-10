package com.liujun.trade.core.uniswap.api.service.impl;

import com.liujun.trade.core.uniswap.api.bean.APIConfiguration;
import com.liujun.trade.core.uniswap.api.bean.Book;
import com.liujun.trade.core.uniswap.api.rpc.ProductRpc;
import com.liujun.trade.core.uniswap.api.rpc.RpcClient;
import com.liujun.trade.core.uniswap.api.service.ProductAPIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProductAPIServiceImpl implements ProductAPIService {
    private static final Logger log = LoggerFactory.getLogger(ProductAPIServiceImpl.class);
    APIConfiguration config;
    ProductRpc productRpc;

    public ProductAPIServiceImpl(APIConfiguration config) {
        this.config = config;
        this.productRpc = RpcClient.getInstance(config.getUri()).useService(ProductRpc.class);
    }

    @Override
    public Book bookProductsByProductId(String coinPair, String marketOrderSize, String orderStepLength) {

        int maxRetry = 5;
        for (int retryCount = 0; ; retryCount++) {
            try {
                Book book = productRpc.bookProduct(coinPair, marketOrderSize, orderStepLength).toFuture().get();
                return book;
            } catch (Exception e) {
                if (e.getMessage().contains("failed to meet quorum")) {
                    log.error("多个节点返回值不一致(继续重试)" + e.getMessage().substring(0, 140));
                    if (retryCount >= maxRetry - 1) {
                        log.error("已经重试了" + maxRetry + "次，不再重试！");
                        throw new RuntimeException(e);
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e1) {
                        log.error("", e1);
                    }
                } else {
                    throw new RuntimeException(e);
                }
            }

        }//end for
    }

    /**
     * 查询gas费，以及eth相对某种币的价格
     *
     * @param moneySymbol 交易对中的计价货币
     * @return
     */
    @Override
    public double[] getGasPriceGweiAndEthPrice(String moneySymbol) {

        int maxRetry = 5;
        for (int retryCount = 0; ; retryCount++) {
            try {
                double[] priceArr = productRpc.getGasPriceGweiAndEthPrice(moneySymbol).toFuture().get();
                return priceArr;
            } catch (Exception e) {
                if (e.getMessage().contains("failed to meet quorum")) {
                    log.error("多个节点返回值不一致(继续重试)" + e.getMessage().substring(0, 140));
                    if (retryCount >= maxRetry - 1) {
                        log.error("已经重试了" + maxRetry + "次，不再重试！");
                        throw new RuntimeException(e);
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e1) {
                        log.error("", e1);
                    }
                } else {
                    throw new RuntimeException(e);
                }
            }

        }//end for
    }
}
