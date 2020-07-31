package com.liujun.trade;

import com.liujun.trade.core.Engine;
import com.liujun.trade.core.Trade;
import com.liujun.trade.core.binance.Trade_binance;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TestBinance extends SpringTest {

     Trade trade;
    @Autowired
    Engine engine;

    @BeforeAll
     void init() {
        for (Trade trade : engine.platList) {
            if (trade.getPlatName().equals(Trade_binance.platName)) {
                this.trade = trade;
            }
        }

    }

    @Test
    public void testMarketDepth() {
        try {
            trade.flushMarketDeeps();
            System.out.println("bid:"+trade.getMarketDepth().getBidList().size()+",ask:"+trade.getMarketDepth().getAskList().size());
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
