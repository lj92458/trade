package com.liujun.trade.core.binance.api.service.wallet;

import com.liujun.trade.core.binance.api.bean.wallet.param.WithdrawParam;
import com.liujun.trade.core.binance.api.bean.wallet.result.WithdrawResult;


public interface WalletAPIService {


    WithdrawResult withdraw(WithdrawParam param) throws Exception;
}
