package com.liujun.trade.core.binance.api.service.spot;

import com.liujun.trade.core.binance.api.bean.spot.result.Account;
import com.liujun.trade.core.binance.api.bean.spot.result.Depth;

public interface SpotProductAPIService {

    Depth marketDepth(String symbol, Integer limit);


}
