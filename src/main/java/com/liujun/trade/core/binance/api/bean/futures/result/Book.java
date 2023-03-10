package com.liujun.trade.core.binance.api.bean.futures.result;

import com.alibaba.fastjson.JSONArray;

/**
 * futures contract product book
 *
 * @author Tony Tian
 * @version 1.0.0
 * @date 2018/3/12 15:14
 */
public class Book {

    /**
     * asks book
     */
    JSONArray asks;
    /**
     * bids book
     */
    JSONArray bids;
    /**
     * time
     */
    String timestamp;

    public JSONArray getAsks() { return asks; }

    public void setAsks(JSONArray asks) { this.asks = asks; }

    public JSONArray getBids() { return bids; }

    public void setBids(JSONArray bids) { this.bids = bids; }

    public String getTimestamp() { return timestamp; }

    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return "Book{" +
                "asks=" + asks +
                ", bids=" + bids +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
