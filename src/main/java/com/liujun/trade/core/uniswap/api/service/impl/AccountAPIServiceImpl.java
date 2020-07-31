package com.liujun.trade.core.uniswap.api.service.impl;

import com.liujun.trade.core.uniswap.api.bean.APIConfiguration;
import com.liujun.trade.core.uniswap.api.bean.Account;
import com.liujun.trade.core.uniswap.api.rpc.AccountRpc;
import com.liujun.trade.core.uniswap.api.rpc.RpcClient;
import com.liujun.trade.core.uniswap.api.service.AccountAPIService;

import java.util.Arrays;
import java.util.List;

public class AccountAPIServiceImpl implements AccountAPIService {
    APIConfiguration config;
    AccountRpc accountRpc;

    public AccountAPIServiceImpl(APIConfiguration config) {
        this.config = config;
        this.accountRpc = RpcClient.getInstance(config.getUri()).useService(AccountRpc.class);
    }

    @Override
    public List<Account> getAccounts(String... symbolArr) {
        try {
            List<Account> list = accountRpc.queryTokenBalance(config.getAddress(), symbolArr).toFuture().get();


            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
