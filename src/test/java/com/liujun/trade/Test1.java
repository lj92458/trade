package com.liujun.trade;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

public class Test1 {

    public static String getUnixTime() {
        StringBuilder nowStr = new StringBuilder(Instant.now().toString());
        return new StringBuilder().append(nowStr.substring(0,nowStr.lastIndexOf("."))).append(nowStr.substring(nowStr.lastIndexOf(".")).substring(0,4)).append(nowStr.substring(nowStr.length()-1)).toString();
    }
    public static void main(String[] args) {
        Clock offsetClock = Clock.offset(Clock.systemUTC(), Duration.ofHours(8));
        System.out.println(Instant.now(Clock.systemUTC()).toEpochMilli());
        System.out.println(Instant.now(offsetClock).toEpochMilli());//北京时间
        //币安 1592474623419  2020/6/18 18:3:43
        //本机 1592503436067  2020/6/19 2:3:56
        System.out.println("utc:"+getUnixTime());
    }
}
