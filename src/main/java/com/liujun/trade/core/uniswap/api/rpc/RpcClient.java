package com.liujun.trade.core.uniswap.api.rpc;

import hprose.client.HproseHttpClient;

/**
 * 单例模式。远程调用客户端
 */
public class RpcClient {
    private static RpcClient rpcClient;
    public static RpcClient getInstance(String uri) {
        if (rpcClient == null) {
            rpcClient = new RpcClient(uri);
        }
        return rpcClient;

    }

    private final HproseHttpClient client;

    private RpcClient(String uri) {
        client = new HproseHttpClient(uri);
    }

    public <T> T useService(Class<T> t){
        return client.useService(t);
    }
}
