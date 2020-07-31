package com.liujun.trade;

import com.alibaba.fastjson.JSON;
import com.liujun.trade.core.uniswap.api.bean.APIConfiguration;
import com.liujun.trade.core.uniswap.api.bean.Account;
import com.liujun.trade.core.uniswap.api.bean.Book;
import com.liujun.trade.core.uniswap.api.service.AccountAPIService;
import com.liujun.trade.core.uniswap.api.service.ProductAPIService;
import com.liujun.trade.core.uniswap.api.service.impl.AccountAPIServiceImpl;
import com.liujun.trade.core.uniswap.api.service.impl.ProductAPIServiceImpl;
import com.okcoin.commons.okex.open.api.bean.spot.result.Product;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestUniswap {
    APIConfiguration config;

    @BeforeAll
    void init() {
        config = new APIConfiguration();
        config.setUri("http://127.0.0.1:8080/");
        config.setAddress("0xb61d572D3f626C0e4cdffAe8559aD838D839F229");
    }

    @Test
    public void testGetAccounts() {

        AccountAPIService accountAPIService = new AccountAPIServiceImpl(config);
        long begin=System.currentTimeMillis();
        List<Account> list = accountAPIService.getAccounts("eth", "dai");
        long end=System.currentTimeMillis();
        System.out.println("毫秒"+(end-begin));
        System.out.println(list);
    }
    @Test
    public void testBookProduct(){
        ProductAPIService productAPIService=new ProductAPIServiceImpl(config);
        long begin=System.currentTimeMillis();
        Book book = productAPIService.bookProductsByProductId("eth-usdc","100","0.0316");
        long end=System.currentTimeMillis();
        System.out.println("毫秒"+(end-begin));
        System.out.println(JSON.toJSONString(book.getAsks()) );
        System.out.println(JSON.toJSONString(book.getBids()) );
    }
}
