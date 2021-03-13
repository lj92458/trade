package com.liujun.trade;

import com.alibaba.fastjson.JSONObject;
import com.liujun.trade.core.Engine;
import com.liujun.trade.core.Trade;
import com.liujun.trade.core.okcoinF.Trade_okcoinF;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestOkcoinF extends SpringTest {

    Trade_okcoinF trade;
    @Autowired
    Engine engine;

    @BeforeAll
    void init() {
        for (Trade trade : engine.platList) {
            if (trade.getPlatName().equals(Trade_okcoinF.platName)) {
                this.trade = (Trade_okcoinF)trade;
            }
        }

    }

    @Test
    public void testGetAccountsByUnderlying()throws Exception{
        JSONObject accountsJO = trade.futuresTradeAPIService.getAccountsByUnderlying(trade.underlying);
        //JSONObject accountsJO = trade.futuresTradeAPIService.getAccounts();
        System.out.println("测试testGetAccountsByUnderlying:"+accountsJO.toJSONString());
        trade.flushAccountInfo();
    }
}
