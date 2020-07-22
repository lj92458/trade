package com.liujun.trade.core;

import com.liujun.trade.core.modle.*;
import com.liujun.trade.core.thread.AccountThread;
import com.liujun.trade.core.thread.AvgpriceThread;
import com.liujun.trade.core.thread.MarketDepthThread;
import com.liujun.trade.core.thread.TradeThread;
import com.liujun.trade.core.util.HttpUtil;
import com.liujun.trade.core.util.StringUtil;
import com.liujun.trade.core.util.XmlConfigUtil;
import com.liujun.trade.utils.SpringContextUtil;
import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * 搬砖引擎,程序入口。负责总的调度。
 *
 * @author Administrator
 */
@Component
@Scope("prototype")
public class Engine {
    // ------- static 属性 ---------
    private static final Logger log = LoggerFactory.getLogger(Engine.class);
    //值得搬运
    private static final Logger log_needTrade = LoggerFactory.getLogger("need_trade");
    //已成交
    private static final Logger log_haveTrade = LoggerFactory.getLogger("have_trade");
    //差价
    private static final Logger log_diff_price = LoggerFactory.getLogger("diff_price");

    private static final String charset = "utf-8";
    private static final double FIRST_FAIL = -99999998;
    private static final double SECOND_FAIL = -99999999;
    private static final String CHANGE_PRICE = "changePrice";
    public static PriceInfo priceInfo;

    // ---- end static ------------------------------------------------

    // --------- 对象属性 -------------------------------------------------
    @Autowired
    Prop prop;
    public boolean initSuccess = false;//初始化是否成功
    public boolean stop = false;//是否结束
    public String basePath;
    public String firstBalance;
    public Document xmlDoc;// 可修改可存储的配置参数,xml文件。在jar文件外面
    public AvgpriceThread avgpriceThread;
    public int maxOrderNum = 75;//最多处理多少市场挂单

    /**
     * 间隔多久查询一次挂单.单位：秒
     */
    @Value("${time_queryOrder}")
    public int time_queryOrder;
    /**
     * 每循环一次,最大允许占用的时间.单位：秒
     */
    @Value("${time_oneCycle}")
    public int time_oneCycle;

    /**
     * 如果抛出异常,暂停多少分钟？
     */
    @Value("${waitSecondAfterException}")
    public int waitSecondAfterException;

    /**
     * 每天几点开始记录余额
     */
    @Value("${time_beginBalance}")
    public int time_beginBalance;
    /**
     * 间隔多久,计算余额并记录日志
     */
    @Value("${time_waitBalance}")
    public int time_waitBalance;
    /**
     * 余额文件的路径
     */
    @Value("${balanceFilePath}")
    public String balanceFilePath;

    /**
     * 订单匹配模式.simple简单匹配, exact精细匹配
     */
    @Value("${trade_model}")
    public String tradeModel;
    @Value("${trade.core.package}")
    public String corePackage;
    // ====重要属性=============
    /**
     * 存放各个平台的交易对象
     */
    public List<Trade> platList;
    /**
     * 存放价格限制属性名:A_B
     */
    public String[] keyArray;
    /**
     * 存放各平台价格限制
     */
    public double[] priceArray;
    /**
     * 存放汇总后的挂单
     */
    public MarketDepth totalDepth;
    public VirtualTrade virtualTrade;// 虚拟的平台

    // ====================
    /**
     * 当前最大差价,初始化为非常小的值
     */
    private double diffPrice_max = -1000;

    /**
     * 本次循环,价格最大的订单的差价方向
     */
    public String diffPriceDirection_maxPrice;
    /**
     * 最后一次记录的余额
     */
    public Balance lastBalance;
    public HttpUtil httpUtil = new HttpUtil();
    public Balance currentBalance;
    /**
     * 本次循环，已配对的订单有多少对？
     */
    public int orderPair;

    public ChangeLimit changeLimit;
    /**
     * 美元对人民币汇率。这里是用比特币给山寨币计价，不存在汇率，所以设为1
     */
    public double usdRate = 1.0;

    // --------- end 对象属性 -------------------------------------------------
    static {
        log.info("in Engine static 代码块");
    }

    @PostConstruct
    public void init() {
        try {

            // 加载外层属性文件
            basePath = Engine.class.getClassLoader().getResource("conf.xml").toString();
            // jar:file:/D:/workspace/auto-trader/target/auto-trader-0.0.1-SNAPSHOT.jar!/conf.properties
            // 不打包是file:/D:/workspace/auto-trader/target/classes/conf.properties
            basePath = basePath.substring(basePath.indexOf("/") + 0, basePath.lastIndexOf("/"));
            //basePath = basePath.substring(0, basePath.lastIndexOf("/"));


            try (InputStreamReader reader = new InputStreamReader(new FileInputStream(basePath + "/conf.xml"), charset)) {
                SAXReader sax = new SAXReader();
                xmlDoc = sax.read(reader);

            } catch (Exception e) {
                log.error("engine配置文件加载异常conf.xml", e);
                throw e;
            }

            firstBalance = readXmlProp("firstBalance");

            //---------------

        /*
        //查询汇率
        double rate1 = 0;
        try {
        	rate1 = usdrate1();
        } catch (Exception e) {

        	log.info("rate1", e);
        }
        double rate2 = 0;
        try {
        	rate2 = usdrate2();
        } catch (Exception e) {
        	log.info("rate2", e);
        }

        if (rate1 != 0 && rate2 != 0) {
        	boolean isRateOk = Math.abs(rate1 - rate1) < 0.001;
        	if (isRateOk) {
        		usdRate = rate1;
        		log.info("当前汇率:" + usdRate);
        	} else {
        		throw new Exception("汇率查询失败，相差过大:" + rate1 + " , " + rate2);
        	}
        } else if (rate1 != 0) {
        	usdRate = rate1;
        } else if (rate2 != 0) {
        	usdRate = rate2;
        } else {
        	throw new Exception("汇率查询失败，两个来源都失败");
        }
        */
            // 存放各个平台的交易对象
            platList = new ArrayList<Trade>();
            String[] paltNameArr = readXmlProp("enablePlat").split(",");
            for (String platName : paltNameArr) {//利用反射，加载各平台的对象
                Class<Trade> clazz = (Class<Trade>) Class.forName(corePackage + "." + platName + ".Trade_" + platName, true,
                        getClass().getClassLoader());
                //Trade trade = clazz.getConstructor(HttpUtil.class, int.class, double.class).newInstance(httpUtil,platList.size(), usdRate);
                Trade trade = SpringContextUtil.getBean(clazz, httpUtil, platList.size(), usdRate, prop);
                //如果初始化失败了
                if (!trade.initSuccess) {
                    throw new Exception(platName + ":初始化失败!!!");
                }
                //设置配置属性
                String changePriceStr = readXmlAttribute(platName, CHANGE_PRICE);
                if (StringUtil.isEmpty(changePriceStr)) {
                    changePriceStr = "0";
                }
                trade.setChangePrice(Double.parseDouble(changePriceStr));
                platList.add(trade);
            }
            //添加虚拟平台
            virtualTrade = SpringContextUtil.getBean(VirtualTrade.class, httpUtil, platList.size(), usdRate, prop);
            platList.add(virtualTrade);

            // 长度是 (n-1)*11+1
            keyArray = new String[(platList.size() - 1) * 11 + 1];
            priceArray = new double[(platList.size() - 1) * 11 + 1];
            for (int i = 0; i < platList.size(); i++) {
                // Trade trade = platList.get(i);
                for (int j = 0; j < platList.size(); j++) {
                    keyArray[i * 10 + j] = platList.get(i).getPlatName() + "_" + platList.get(j).getPlatName();
                    String priceStr = readXmlProp(keyArray[i * 10 + j]);
                    if (i == j || StringUtil.isEmpty(priceStr)) {
                        priceArray[i * 10 + j] = prop.minMoney;
                    } else {
                        priceArray[i * 10 + j] = Double.parseDouble(priceStr);
                    }
                }// end for

            }// end for
            if (priceInfo == null || priceInfo.platCount != platList.size()) {//只创建一次
                priceInfo = new PriceInfo(platList.size());
            }
            changeLimit = SpringContextUtil.getBean(ChangeLimit.class);
            changeLimit.setEngine(this);
            // ------设置余额记录--------------------------
            File balanceFile = new File(balanceFilePath);
            // 如果余额文件不存在,就创建并写入初始记录,并赋值给lastBalance
            if (!balanceFile.exists()) {
                OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(balanceFilePath, true), charset);
                writer.write(firstBalance);
                writer.flush();
                writer.close();
                lastBalance = new Balance(prop, firstBalance);
            } else {// 如果余额文件存在,就读取最后一行,赋值给lastBalance
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(balanceFilePath),
                        charset));
                String lastBalanceStr = null;// 最后一次余额
                // 读取最后一行记录
                for (String lastLine = reader.readLine(); lastLine != null; lastLine = reader.readLine()) {
                    lastBalanceStr = lastLine;
                }
                lastBalance = new Balance(prop, lastBalanceStr);
            }
            currentBalance = getCurrentBalance();
            // end 设置余额记录---------------------------

            //启动差价记录线程
            avgpriceThread = SpringContextUtil.getBean(AvgpriceThread.class);
            avgpriceThread.setEngine(this);
            avgpriceThread.setDaemon(true);
            avgpriceThread.start();
            log.info("启动平均值线程");

            //
            this.initSuccess = true;
        } catch (Exception e) {
            log.error("初始化 enging出现异常", e);
        }
    }

    /**
     * 启动搬运引擎
     *
     * @return 1表示正常退出,-1表示异常退出
     */
    public int startEngine() {
        stop = false;
        log.info("开始搬运......");
        try {

            for (long i = 0; !stop && i < 86400 / time_queryOrder; i++) {// 每隔24小时(),自动退出
                //
                long beginTime = System.currentTimeMillis();
                totalDepth = new MarketDepth();
                // ===== 【多线程】对各平台查询市场挂单，查询资金情况=======================
                List<MarketDepthThread> marketDepthThreadList = new ArrayList<>();
                // 为每个平台启动一个线程
                for (Trade trade : platList) {
                    // 设置“用户挂单”
                    if (trade.getUserOrderList() == null || trade.getUserOrderList().size() > 0) {
                        trade.setUserOrderList(new ArrayList<>());
                    }
                    //if (trade == virtualTrade)
                    // continue;
                    MarketDepthThread marketDepthThread = SpringContextUtil.getBean(MarketDepthThread.class, trade, this);
                    marketDepthThread.setDaemon(true);// 设为守护线程
                    marketDepthThreadList.add(marketDepthThread);
                    marketDepthThread.start();

                    //查询资金情况 -----------
                    if ((i * time_queryOrder) % 3 == 0) {//每隔3秒，查一次账户
                        AccountThread accountThread = SpringContextUtil.getBean(AccountThread.class, trade, this);
                        accountThread.setDaemon(true);//设为守护线程
                        accountThread.start();
                    }
                }
                // 等待各个线程结束,最多等5秒-------
                for (MarketDepthThread thread : marketDepthThreadList) {
                    thread.join(5 * 1000);
                }
                // 检查线程超时,
                for (MarketDepthThread thread : marketDepthThreadList) {
                    if (!thread.isSuccess()) {
                        throw new Exception(thread.getName() + "获取市场深度异常");
                    }

                }
                // ==== end【多线程】对各平台查询市场挂单=======================

                // 设置综合深度
                for (Trade trade : platList) {
                    //如果允许跨平台搬运
                    if (trade.getModeLock() == 0 || trade.getModeLock() == 1) {
                        trade.setModeLock(1);//加锁
                        totalDepth.getAskList().addAll(trade.getMarketDepth().getAskList());
                        totalDepth.getBidList().addAll(trade.getMarketDepth().getBidList());

                    }

                }

                totalSort();// 对汇总的市场挂单进行排序：卖方从大到小排序,买方从小到大排序
                log.debug(totalDepth.getBidList() + "");/////////////////
                //调节限价
                double maxMoney = 0;
                if (tradeModel.equals("simple")) {
                    maxMoney = adjustLimitPrice1();
                } else if (tradeModel.equals("exact")) {
                    maxMoney = adjustLimitPrice2();
                }

                if (orderPair > 0 && maxMoney >= prop.minMoney) {// 只有模拟生成的订单存在时，才搬运
                    log_needTrade.info("(机会)市场最大差价" + prop.fmt_money.format(diffPrice_max) + "," + diffPriceDirection_maxPrice
                            + ",预计最多能赚" + prop.fmt_money.format(maxMoney) + "元,模拟订单有" + orderPair + "对");


                    // 将各平台备份的市场挂单导入汇总挂单(因为调节限价时，会把挂单的goods量改为0)
                    totalDepth.getAskList().clear();
                    totalDepth.getBidList().clear();
                    for (Trade trade : platList) {
                        totalDepth.getAskList().addAll(trade.getBackupDepth().getAskList());
                        totalDepth.getBidList().addAll(trade.getBackupDepth().getBidList());
                    }
                    totalSort();// 对汇总的市场挂单进行排序：买方从大到小排序,卖方从小到大排序
                    log.debug(totalDepth.getBidList() + "");/////////////////
                    double diffPrice = totalDepth.getBidList().get(0).getPrice()
                            - totalDepth.getAskList().get(0).getPrice();
                    log.debug("市场买单：" + totalDepth.getBidList().toString());
                    log.debug("市场卖单：" + totalDepth.getAskList().toString());

                    //begin: 根据备份的市场深度求当前市场价格，然后设置给虚拟平台
                    double avgPrice = (totalDepth.getBidList().get(0).getPrice() + totalDepth.getAskList().get(0)
                            .getPrice()) / 2.0;
                    List<MarketOrder> virtualAskList = virtualTrade.getBackupDepth().getAskList();
                    if (virtualAskList.size() > 0) {//降低市场卖单价格，确保真实平台能卖出
                        virtualAskList.get(0).setPrice(avgPrice - prop.huaDian);
                    }
                    List<MarketOrder> virtualBidList = virtualTrade.getBackupDepth().getBidList();
                    if (virtualBidList.size() > 0) {//提高市场买单价格，确保真实平台能买到
                        virtualBidList.get(0).setPrice(avgPrice + prop.huaDian);
                    }
                    // end : 根据备份的市场深度求当前市场价格，然后设置给虚拟平台

                    int keyIndex = totalDepth.getBidList().get(0).getPlatId() * 10
                            + totalDepth.getAskList().get(0).getPlatId();
                    log.info("可搬运最大差价【" + diffPrice + "】" + keyArray[keyIndex] + " " + totalDepth.getBidList().get(0)
                            + "," + totalDepth.getAskList().get(0)); //


                    double maxMoney2 = 0;
                    if (tradeModel.equals("simple")) {
                        maxMoney2 = createOrders1();// 正式生成订单
                    } else if (tradeModel.equals("exact")) {
                        maxMoney2 = createOrders2();
                    }


                    if (orderPair > 0 && maxMoney2 >= prop.minMoney) {// (正式生成的订单数量)
                        log_needTrade.info("实际能赚" + maxMoney2 + "元，实际订单有" + orderPair + "对");
                        for (Trade trade : platList) {
                            trade.processOrders();//订单预处理 。不需要，因为跟trade.backupUsefulOrder()方法功能是重复的
                        }
                        //删掉无效订单
                        int usefulOrderCount = 0;
                        for (Trade trade : platList) {
                            List<UserOrder> userOrderList = trade.getUserOrderList();
                            for (int index = userOrderList.size() - 1; index >= 0; index--) {
                                if (userOrderList.get(index).isEnable()) {
                                    usefulOrderCount++;
                                } else {
                                    userOrderList.remove(index);// 无效订单要及时删掉
                                }
                            }// end for
                            //如果已经加锁，判断是否应该释放锁
                            if (trade.getModeLock() == 1 && userOrderList.size() == 0) {
                                trade.setModeLock(0);
                            }

                        }
                        //如果有需要执行的订单，才应该启动线程
                        if (usefulOrderCount > 0) {
                            // 【多线程】对各平台执行挂单、查订单状态、撤销没完全成交的订单、刷新账户信息==================
                            List<TradeThread> tradeThreadList = new ArrayList<>();
                            // 为每个平台启动一个线程--------
                            for (Trade trade : platList) {
                                if (trade == virtualTrade)
                                    continue;
                                TradeThread tradeThread = SpringContextUtil.getBean(TradeThread.class, trade, this);
                                tradeThread.setDaemon(true);// 设为守护线程
                                tradeThreadList.add(tradeThread);
                                tradeThread.start();
                            }// end for
                            // 等待各个线程结束,最多等30秒-------
                            for (TradeThread thread : tradeThreadList) {
                                thread.join(30 * 1000);
                            }
                            // 检查线程超时
                            for (TradeThread thread : tradeThreadList) {
                                if (!thread.isSuccess()) {
                                    throw new Exception(thread.getName() + ":TradeThread异常");
                                }
                            }
                            log_haveTrade
                                    .info("===================================================================================");
                            initVirtualPlat();
                            log.info("各线程都已结束=========");
                            // end 【多线程】对各平台执行挂单、查订单状态、撤销没完全成交的订单、刷新账户信息============
                        }//end 如果有需要执行的订单，才应该启动线程
                    } else {//end 如果正式生成的订单数量>0
                        log.info("正式订单最多赚" + maxMoney2 + "," + orderPair + "对订单(不值得/看不上)----------------------");
                    }
                } else {
                    log.info("最多赚" + maxMoney + "," + diffPriceDirection_maxPrice + "----------------------最大差价"
                            + prop.fmt_money.format(diffPrice_max));
                }

                // 检查goods总数量,如果不跟初始值相等,就立即买卖调整
                checkTotalGoods();
                // 检查各平台的goods数量,如果分布不平衡,就自动转移。
                // balanceGoods();
                // 盘点当前余额,计算盈亏------------------------
                saveBalance();

                // 计算耗时,如果大于最大限度,就报错
                long endTime = System.currentTimeMillis();
                long useTime = endTime - beginTime;// 用时
                log.info("{" + currentBalance.getPlatInfo() + "}");
                if (useTime > 60 * 1000) {// 如果用时大于60秒
                    throw new Exception("本次超时！耗时" + (useTime / 1000.0) + "秒++++++++++++++++++++++++++++++++++++++");
                } else if (useTime > (time_oneCycle * 1000)) {
                    log.warn("xxxxxxxxxxxxxx本次超时！耗时" + (useTime / 1000.0) + "秒xxxxxxxxxxxxxxxtotalEarn:"
                            + currentBalance.getTotalEarn() + "===thisEarn:" + currentBalance.getThisEarn()
                            + " xxxxx");
                } else {
                    log.info("==============本次耗时" + useTime + "毫秒=======totalEarn:" + currentBalance.getTotalEarn()
                            + "===thisEarn:" + currentBalance.getThisEarn() + "============");
                }
                //检测亏损
                if (currentBalance.getThisEarn() <= -60.0 / prop.moneyPrice) {
                    //throw new Exception("出现亏损，暂停搬运。");
                }
                // 睡眠一段时间,保证两次搬运间隔time_queryOrder秒
                TimeUnit.MILLISECONDS.sleep(time_queryOrder * 1000 - useTime);

            }// end for
            log.info("跳出for,   startEngine()结束");

            return 1;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return -1;
        } finally {

            try {
                httpUtil.getHttpClient().close();
            } catch (IOException e) {
                log.error("HttpClient关闭时出现异常", e);
            }
        }
    }

    /**
     * 对汇总的市场挂单进行排序：买方从大到小排序,卖方从小到大排序
     */
    private void totalSort() {
        // 卖
        Collections.sort(totalDepth.getAskList());
        // 对买方排序,然后颠倒
        Collections.sort(totalDepth.getBidList());
        Collections.reverse(totalDepth.getBidList());
    }

    /**
     * 简单型：任何平台差价达不到阈值，就全部停止匹配
     *
     * @return
     * @throws Exception
     */
    private double adjustLimitPrice1() throws Exception {
        diffPrice_max = -1000;// 初始化当前差价
        orderPair = 0;// 初始化已配对的订单数量
        double maxMoney = 0;// 最多能赚多少钱
        diffPriceDirection_maxPrice = null;
        List<MarketOrder> askList = totalDepth.getAskList();
        List<MarketOrder> bidList = totalDepth.getBidList();
        // 早已不满足条件？
        boolean[] passArr = new boolean[(platList.size() - 1) * 11 + 1];//
        // adjust1是否已处理过
        boolean[] passAdjust1Arr = new boolean[(platList.size() - 1) * 11 + 1];//

        while (askList.size() > 0 && bidList.size() > 0) {
            if (askList.get(0).getVolume() < prop.minCoinNum) {
                askList.remove(0);
            }
            if (bidList.get(0).getVolume() < prop.minCoinNum) {
                bidList.remove(0);
            }
            double thisMoney = helpadjustLimit(askList.get(0), bidList.get(0), passArr, passAdjust1Arr);
            if (thisMoney > FIRST_FAIL) {// 如果满足条件，才累计金额
                maxMoney += thisMoney;

            } else {
                //如果差价达不到阈值，就不会扣减双方的volume.导致该挂单没机会被删除。 就必须跳出循环，否则就是死循环。
                break;
            }
        }//end while

        //如果市场订单被全部用掉，说明获取的订单太少
        if (askList.size() == 0 || bidList.size() == 0) {
            log.warn("市场深度不足（获取的挂单太少）");
        }


        return maxMoney;
    }

    /**
     * 调整阀值。
     *
     * @param passArr        某平台是否早已不满足条件，应该跳过
     * @param passAdjust1Arr 某平台是否应该跳过adjust1的处理
     * @return
     * @throws Exception
     */
    private double helpadjustLimit(MarketOrder ask, MarketOrder bid, boolean[] passArr, boolean[] passAdjust1Arr) throws
            Exception {
        int arrayIndex = bid.getPlatId() * 10 + ask.getPlatId();
        // 计算差价
        double diffPrice = bid.getPrice() - ask.getPrice();
        if (diffPrice > diffPrice_max) {
            diffPrice_max = diffPrice;
            diffPriceDirection_maxPrice = keyArray[arrayIndex];
        }
        double amount = ask.getVolume() < bid.getVolume() ? ask.getVolume() : bid.getVolume();
        // 如果不是虚拟平台，就调节限价
        if (bid.getPlatId() != ask.getPlatId() && bid.getPlatId() != virtualTrade.platId
                && ask.getPlatId() != virtualTrade.platId) {
            changeLimit.adjust1(diffPrice, amount, arrayIndex, ask, bid, passAdjust1Arr);//
            changeLimit.adjust2(diffPrice, amount, arrayIndex, ask, bid);
            changeLimit.adjust3(diffPrice, amount, arrayIndex, ask, bid);
        }
        // 如果有差价,并且差价大于min_diffPrice元,就值得搬运
        if (diffPrice >= priceArray[arrayIndex]) {
            // 寻找两者之中较小的挂单量
            if (amount < prop.minCoinNum) {// 如果数量太小。（在简单匹配模式，数量不可能太小。太小的已经被删除了。）
                return 0.00;
            }

            orderPair++;
            log_diff_price.info("有差价:" + prop.fmt_money.format(diffPrice) + ",数量:" + prop.fmt_goods.format(amount) + ",方向:"
                    + keyArray[arrayIndex] + "," + bid.getPrice() + "_" + ask.getPrice() + "");

            // =======从卖方挂单扣除amount,===========
            ask.setVolume(ask.getVolume() - amount);
            // =====从买方挂单扣除amount,==========
            bid.setVolume(bid.getVolume() - amount);
            // ======设置配对订单============================
            return diffPrice * amount;
        } else {// 如果不满足条件
            if (passArr[arrayIndex] == true) {// 如果早已不满足条件了
                return SECOND_FAIL;
            } else {// 如果是首次不满足条件
                passArr[arrayIndex] = true;
                return FIRST_FAIL;
            }
        }
    }

    /**
     * 寻找差价,生成订单。简单型：任何平台差价达不到阈值，就全部停止匹配
     *
     * @throws Exception
     */
    private double createOrders1() throws Exception {
        orderPair = 0;// 初始化已配对的订单数量
        double maxMoney = 0;// 最多能赚多少钱
        List<MarketOrder> askList = totalDepth.getAskList();
        List<MarketOrder> bidList = totalDepth.getBidList();

        boolean[] passArr = new boolean[(platList.size() - 1) * 11 + 1];// 是否需要搬运

        while (askList.size() > 0 && bidList.size() > 0) {
            if (askList.get(0).getVolume() < prop.minCoinNum) {
                askList.remove(0);
            }
            if (bidList.get(0).getVolume() < prop.minCoinNum) {
                bidList.remove(0);
            }
            double thisMoney = helpCreateOrders(askList.get(0), bidList.get(0), passArr);
            if (thisMoney > FIRST_FAIL) {// 如果差价达到限制条件，才累计金额（thisMoney不一定是正数）
                maxMoney += thisMoney;

            } else {
                //如果差价达不到阈值，就不会扣减双方的volume.导致该挂单没机会被删除。 就必须跳出循环，否则就是死循环。
                break;
            }
        }

        //如果市场订单被全部用掉，说明获取的订单太少
        if (askList.size() == 0 || bidList.size() == 0) {
            log.warn("市场深度不足（获取的挂单太少）");
        }

        return maxMoney;
    }

    /**
     * 返回预计赚的钱。如果是FIRST_FAIL，表示首次出现不满足的情况，如果是SECOND_FAIL表示多次出现不满足的情况
     *
     * @param passArr 不满足条件吗? true表示“不满足”，false表示满足
     * @return
     * @throws Exception
     */
    private double helpCreateOrders(MarketOrder ask, MarketOrder bid, boolean[] passArr) throws Exception {

        int arrayIndex = bid.getPlatId() * 10 + ask.getPlatId();
        // 计算差价
        double diffPrice = bid.getPrice() - ask.getPrice();
        double amount = ask.getVolume() < bid.getVolume() ? ask.getVolume() : bid.getVolume();
        // 如果有差价,并且差价大于min_diffPrice元,就值得搬运
        if (diffPrice >= priceArray[arrayIndex]) {
            // 寻找两者之中较小的挂单量
            if (amount < prop.minCoinNum) {// 如果数量太小。（在简单匹配模式，数量不可能太小。太小的已经被删除了。）
                return 0.00;
            }

            orderPair++;
            log_diff_price.info("(实际订单)有差价:" + prop.fmt_money.format(diffPrice) + ",数量:" + prop.fmt_goods.format(amount) + ",方向:"
                    + keyArray[arrayIndex] + "," + bid.getPrice() + "_" + ask.getPrice() + "");

            // =======从卖方挂单扣除amount,并生成买单===========
            ask.setVolume(ask.getVolume() - amount);
            UserOrder order_buy = new UserOrder();
            order_buy.setPlatId(ask.getPlatId());
            order_buy.setType("buy");
            order_buy.setPrice(ask.getPrice());
            order_buy.setDiffPrice(diffPrice);
            order_buy.setVolume(amount);
            Trade trade1 = platList.get(ask.getPlatId());
            trade1.getUserOrderList().add(order_buy);

            // =====从买方挂单扣除amount,并生成卖单==========
            bid.setVolume(bid.getVolume() - amount);
            UserOrder order_sell = new UserOrder();
            order_sell.setPlatId(bid.getPlatId());
            order_sell.setType("sell");
            order_sell.setPrice(bid.getPrice());
            order_sell.setDiffPrice(diffPrice);
            order_sell.setVolume(amount);
            Trade trade2 = platList.get(bid.getPlatId());
            trade2.getUserOrderList().add(order_sell);
            // ======设置配对订单============================
            order_buy.setAnotherOrder(order_sell);
            order_sell.setAnotherOrder(order_buy);
            return diffPrice * amount;
        } else {// 如果不满足条件
            if (passArr[arrayIndex] == true) {// 如果早已不满足条件了
                return SECOND_FAIL;
            } else {// 如果是首次不满足条件
                passArr[arrayIndex] = true;
                return FIRST_FAIL;
            }
        }
    }

    /**
     * 精细型(穷举所有可能)：一对平台差价达不到阈值，就继续找下去。因为这不代表其他平台也达不到阈值
     *
     * @return
     * @throws Exception
     */
    private double adjustLimitPrice2() throws Exception {
        diffPrice_max = -1000;// 初始化当前差价
        orderPair = 0;// 初始化已配对的订单数量
        double maxMoney = 0;// 最多能赚多少钱
        diffPriceDirection_maxPrice = null;
        List<MarketOrder> askList = totalDepth.getAskList();
        List<MarketOrder> bidList = totalDepth.getBidList();
        // log.info(askList.toString());
        // log.info("bid:====================================");
        // log.info(bidList.toString());
        // 对角线法遍历矩阵(二维数组[askList][bidList])，竖向(第一维)是askList，横向(第二维)是bidList 。
        //参见：http://shmilyaw-hotmail-com.iteye.com/blog/1769105
        // 早已不满足条件？
        boolean[] passArr = new boolean[(platList.size() - 1) * 11 + 1];//
        // adjust1是否已处理过
        boolean[] passAdjust1Arr = new boolean[(platList.size() - 1) * 11 + 1];//
        // 遍历上半个矩阵
        int maxBidIndex = bidList.size() > maxOrderNum ? maxOrderNum : bidList.size();
        int maxAskIndex = askList.size() > maxOrderNum ? maxOrderNum : askList.size();
        int bidI = 0, askI = 0;
        for (bidI = 0; bidI < maxBidIndex; bidI++) {// 横向(第二维)bidList
            for (askI = 0; askI <= bidI && askI < maxAskIndex; askI++) {// 纵向(第一维)askList
                double thisMoney = helpadjustLimit(askList.get(askI), bidList.get(bidI - askI), passArr, passAdjust1Arr);
                if (thisMoney > FIRST_FAIL) {// 如果满足条件，才累计金额
                    maxMoney += thisMoney;
                    /*
                    if (thisMoney != 0.00) {
                    	log.info(keyArray[arrayIndex] + ":(" + bidIndex + " , " + askI + ")");
                    } */
                }
                /*
                if (thisMoney == FIRST_FAIL) {// 如果是首次不满足，就记录日志
                	log.info(keyArray[arrayIndex] + ":(" + bidIndex + " | " + askI + ")");
                } */
            }// end for

        }// end for
        if (bidI >= maxOrderNum || askI >= maxOrderNum) {
            log.warn("市场深度不足（获取的挂单太少）");
        }

        return maxMoney;
    }


    /**
     * 寻找差价,生成订单.精细型(穷举所有可能)：一对平台差价达不到阈值，就继续找下去.因为这不代表其他平台也达不到阈值
     *
     * @throws Exception
     */
    private double createOrders2() throws Exception {
        orderPair = 0;// 初始化已配对的订单数量
        double maxMoney = 0;// 最多能赚多少钱
        List<MarketOrder> askList = totalDepth.getAskList();
        List<MarketOrder> bidList = totalDepth.getBidList();
        // 对角线法遍历矩阵(二维数组[askList][bidList])，竖向(第一维)是askList，横向(第二维)是bidList 。
        boolean[] passArr = new boolean[(platList.size() - 1) * 11 + 1];// 是否需要搬运
        // 遍历上半个矩阵
        int maxBidIndex = bidList.size() > maxOrderNum ? maxOrderNum : bidList.size();
        int maxAskIndex = askList.size() > maxOrderNum ? maxOrderNum : askList.size();
        int bidI = 0, askI = 0;
        for (bidI = 0; bidI < maxBidIndex; bidI++) {// 横向(第二维)bidList
            for (askI = 0; askI <= bidI && askI < maxAskIndex; askI++) {// 纵向(第一维)askList
                double thisMoney = helpCreateOrders(askList.get(askI), bidList.get(bidI - askI), passArr);
                if (thisMoney > FIRST_FAIL) {// 如果满足条件，才累计金额
                    maxMoney += thisMoney;
                    /*
                    if (thisMoney != 0.00) {
                    	log.info(keyArray[arrayIndex] + ":(" + bidIndex + " , " + askI + ")");
                    } */
                }
                /*
                if (thisMoney == FIRST_FAIL) {// 如果是首次不满足，就记录日志
                	log.info(keyArray[arrayIndex] + ":(" + bidIndex + " | " + askI + ")");
                } */
            }// end for
        }// end for

        if (bidI >= maxOrderNum || askI >= maxOrderNum) {
            log.warn("市场深度不足（获取的挂单太少）");
        }

        return maxMoney;
    }


    /**
     * 检查各平台的goods数量,如果满足转移条件,就自动转移【尽量不要让转移发生】。 原则：1.预处理：设置”触发交易的最小差价“ < 单币手续费
     * 2.预处理： 通过日志观察,如果有价格倒挂,则不需要转移； 3.预处理：如果没有出现倒挂,通过日志观察是否有 (差价> 单币手续费)的时候?
     * 如果有,可以设置”触发交易的最小差价“ >= 单币手续费, 如果已设置【”触发交易的最小差价“ >=
     * 单币手续费】,则开启goods自动转移功能,等转移完了,人工反向转移money. 3.1 其他的情况,无解。(如果没有 (差价>
     * 单币手续费)的时候,则无解；)
     *
     * @throws Exception
     */
    public void balanceGoods() throws Exception {
        /*
        // 计算chbtc价格:卖价、买价的平均值
        List<MarketOrder> askList_chbtc = trade_chbtc.getMarketDepth().getAskList();
        double minAskPrice_chbtc = askList_chbtc.get(askList_chbtc.size() - 1).getPrice();
        List<MarketOrder> bidList_chbtc = trade_chbtc.getMarketDepth().getBidList();
        double maxBidPrice_chbtc = bidList_chbtc.get(bidList_chbtc.size() - 1).getPrice(); // ----
        double price_chbtc = (minAskPrice_chbtc + maxBidPrice_chbtc) / 2;


        // 计算okcion价格：卖价、买价的平均值 
        List<MarketOrder> askList_okcoin = trade_okcoin.getMarketDepth().getAskList();
        double minAskPrice_okcoin = askList_okcoin.get(askList_okcoin.size() - 1).getPrice();
        List<MarketOrder> bidList_okcoin = trade_okcoin.getMarketDepth().getBidList();
        double maxBidPrice_okcoin = bidList_okcoin.get(bidList_okcoin.size() - 1).getPrice();
        double price_okcoin = (minAskPrice_okcoin + maxBidPrice_okcoin) / 2; // ----


        // 单币手续费 
        double fee = Const.formatMoney(money_Draw_rate * price_chbtc);
        // 如果chbtc上面btc占总资产的比例<30%,才需要运btc过来 
        double freeGoodsValue_chbtc = price_chbtc * trade_chbtc.getAccInfo().getFreeGoods();// btc价值多少money
        double freeMoney_chbtc = trade_chbtc.getAccInfo().getFreeMoney();
        double percent = freeGoodsValue_chbtc / (freeGoodsValue_chbtc + freeMoney_chbtc);
        percent = Const.formatMoney(percent);
        if (percent < 0.9) {// 需要搬运
        	log.warn("chbtc上面缺乏btc,需要自动搬运(btc还剩" + (percent * 100) + "%)");
        	needGoods_chbtc = true;
        	if (havePriceReverse) {// 如果有价格倒挂,则等待倒挂 //
        		log.info("等待价格倒挂........................");
        	} else {// 其他情况无解
        		log.warn("btc需要搬运,却无法搬运,无解!!!!!!");
        	}
        } else {
        	needGoods_chbtc = false;
        }
        */
    }

    /**
     * 盘点当前余额,计算盈亏 。如果当前时间的hour符合配置文件设定的记录间隔,且当前这一小时内没记,才记录
     */
    public void saveBalance() throws Exception {
        // 盘点当前余额,计算盈亏------------------------
        // 如果当前时间的hour符合配置文件设定的记录间隔,且当前这一小时内没记,才记录
        DateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd HH");
        DateFormat dateFmt_HH = new SimpleDateFormat("HH");
        int currentHour = Integer.parseInt(dateFmt_HH.format(new Date()));
        String currentDateHour = dateFmt.format(new Date());
        String lastBalanceDateHour = lastBalance.getDateTime().substring(0, 0 + "yyyy-MM-dd HH".length());
        /**
         * 如果(当前hour)%(时间间隔)==(起始hour)%(时间间隔),
         * 且lastBalance中的"yyyy-MM-dd HH"不等于当前的时间
         */
        if (currentHour % time_waitBalance == time_beginBalance % time_waitBalance
                && !currentDateHour.equals(lastBalanceDateHour)) {

            // 将本次余额设置为最后一次余额
            lastBalance = currentBalance;
            // 将lastBalance写入文件
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(balanceFilePath, true), charset);
            writer.append("\n");
            writer.append(lastBalance.toString());
            writer.flush();
            writer.close();
        }
        // end 盘点当前余额,计算盈亏-------------------
    }

    /**
     * 用当前余额,减去最近一次存储的余额,计算盈亏
     */
    public Balance getCurrentBalance() {
        Balance bal = new Balance(prop);
        // 从最后一个参数开始设置
        double totalPrice = 0;
        double totalGoods = 0;
        double totalMoney = 0;
        StringBuilder platInfo = new StringBuilder();
        for (Trade trade : platList) {
            if (trade == virtualTrade)
                continue;
            log.debug(trade.getPlatName() + "当前价格" + trade.getCurrentPrice());
            AccountInfo inf = trade.getAccInfo();
            totalPrice += trade.getCurrentPrice();
            if (platInfo.length() != 0) {
                platInfo.append(",");
            }
            platInfo.append(trade.getPlatName()).append("Money").append(":")
                    .append(inf.getFreeMoney());
            platInfo.append(",").append(trade.getPlatName()).append("Goods").append(":")
                    .append(inf.getFreeGoods());
            totalGoods += inf.getFreeGoods() + inf.getFreezedGoods();
            totalMoney += inf.getFreeMoney() + inf.getFreezedMoney();
        }// end for
        bal.setPrice(totalPrice / (platList.size() - 1));//排除虚拟平台
        bal.setPlatInfo(platInfo.toString());
        bal.setTotalGoods(totalGoods);
        bal.setTotalMoney(totalMoney);
        //
        // 跟初始余额比较,计算总共盈亏
        Balance initBalance = new Balance(prop, firstBalance);
        double totalEarn = bal.getTotalMoney() - initBalance.getTotalMoney() + bal.getPrice()
                * (bal.getTotalGoods() - initBalance.getTotalGoods());
        bal.setTotalEarn(totalEarn);
        // 跟上次盈亏比较,计算本次盈亏
        bal.setThisEarn(bal.getTotalEarn() - lastBalance.getTotalEarn());
        // 设置时间
        bal.setDateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        return bal;
    }

    /**
     * 检查goods总数量,如果不跟初始值相等,就立即调整。
     *
     * @throws Exception
     */
    public void checkTotalGoods() throws Exception {
        initVirtualPlat();
        currentBalance = getCurrentBalance();
        Balance initBal = new Balance(prop, firstBalance);
        double diffAmount = currentBalance.getTotalGoods() - initBal.getTotalGoods();

        log.debug("diffAmount:" + currentBalance.getTotalGoods() + " , " + initBal.getTotalGoods());
        if (diffAmount > 0.1) {// 如果变多,就卖
            log.info("总goods增多" + diffAmount);
            // 增加一个虚拟的低价市场卖单，诱使程序在其他平台卖
            virtualTrade.setCurrentPrice(currentBalance.getPrice());
            // 设置市场挂单
            MarketDepth depth = virtualTrade.getMarketDepth();
            MarketOrder marketOrder = new MarketOrder();
            marketOrder.setPlatId(virtualTrade.platId);
            marketOrder.setPrice(currentBalance.getPrice() - prop.huaDian);//价格设置不不光是在这里，还要在下一轮比价时
            marketOrder.setVolume(diffAmount);
            depth.getAskList().add(marketOrder);
            // log.info("virtual:卖单" + depth.getAskList().size());
            // 设置账户信息
            AccountInfo accInfo = new AccountInfo();
            accInfo.setFreeMoney(diffAmount * currentBalance.getPrice());
            virtualTrade.setAccInfo(accInfo);
            //
        } else if (diffAmount < -0.1) {// 如果变少就买
            diffAmount = 0 - diffAmount;
            log.info("总goods减少" + diffAmount);
            // 增加一个虚拟的高价市场买单，诱使程序在其他平台买
            virtualTrade.setCurrentPrice(currentBalance.getPrice());
            // 设置市场挂单
            MarketDepth depth = virtualTrade.getMarketDepth();
            MarketOrder marketOrder = new MarketOrder();
            marketOrder.setPlatId(virtualTrade.platId);
            marketOrder.setPrice(currentBalance.getPrice() + prop.huaDian);//价格设置不不光是在这里，还要在下一轮比价时
            marketOrder.setVolume(diffAmount);
            depth.getBidList().add(marketOrder);
            log.info("virtual:买单" + depth.getBidList().size() + ",市场均价" + currentBalance.getPrice());
            // log.info("currentBalance.getPrice():"+currentBalance.getPrice());
            // 设置账户信息
            AccountInfo accInfo = new AccountInfo();
            accInfo.setFreeGoods(diffAmount + 10);
            virtualTrade.setAccInfo(accInfo);
            //
        } else {
            // log.info("总goods数量无变化");
        }

    }

    public void initVirtualPlat() {
        // 清空市场挂单
        MarketDepth depth = virtualTrade.getMarketDepth();
        depth.getBidList().clear();
        depth.getAskList().clear();
        // 清空账户信息
        virtualTrade.getAccInfo().setFreeGoods(0);
        virtualTrade.getAccInfo().setFreeMoney(0);
    }


    /**
     * 从xml配置文件中读取参数
     *
     * @param key
     * @return
     */
    private String readXmlProp(String key) {
        String path = key.replace("_", "/");
        Node node = xmlDoc.selectSingleNode("conf/" + path);
        if (node == null) {
            return null;
        } else {
            return node.getText();
        }
    }

    /**
     * 读取xml元素的属性
     */
    private String readXmlAttribute(String elementName, String attrName) {
        String path = elementName.replace("_", "/") + "/@" + attrName;
        Node node = xmlDoc.selectSingleNode("conf/" + path);
        if (node == null) {
            return null;
        } else {
            return node.getText();
        }
    }

    /**
     * 修改xmlDoc对象，并把xmlDoc保存到文件
     *
     * @param key
     * @param value
     * @throws Exception
     */
    private void saveXmlProp(String key, String value) throws Exception {
        synchronized (xmlDoc) {//对xmlDoc的写操作，可能引发线程安全问题，所以要加锁
            String path = key.replace("_", "/");
            xmlDoc.selectSingleNode("conf/" + path).setText(value);
            FileOutputStream fos = new FileOutputStream(basePath + "/conf.xml", false);
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding(charset);
            XMLWriter xmlWriter = new XMLWriter(fos, format);
            xmlWriter.write(xmlDoc);
            xmlWriter.flush();
            xmlWriter.close();
            fos.close();
        }
    }

    /**
     * 保存配置参数到文件
     *
     * @throws Exception
     */
    private void saveProp2(String name1, String name2, String value) throws Exception {
        String key = name1 + "_" + name2;
        for (int i = 0; i < keyArray.length; i++) {
            if (key.equals(keyArray[i])) {
                priceArray[i] = Double.parseDouble(value);
                break;
            }
        }

        saveXmlProp(key, value);

    }

    public void saveProp2(int id1, int id2, double value) throws Exception {
        int index = id1 * 10 + id2;
        priceArray[index] = value;

        saveXmlProp(keyArray[index], "" + value);

    }

    private double usdrate1() throws Exception {

        String str = HttpUtil.getInstance().requestHttpGet("http://www.usd-cny.com/t3.js", "", "");
        String str2 = "price['CNY:CUR'] = ";
        int index1 = str.indexOf(str2) + str2.length();
        int index2 = str.indexOf(";", index1);
        return Double.parseDouble(str.substring(index1, index2));
    }

    private double usdrate2() throws Exception {

        String str = HttpUtil.getInstance().requestHttpGet("http://qq.ip138.com/hl.asp?from=USD&to=CNY&q=2", "", "");
        String str2 = "<td>2</td><td>";
        int index1 = str.indexOf(str2) + str2.length();
        int index2 = str.indexOf("</td>", index1);
        return Double.parseDouble(str.substring(index1, index2));
    }

    public void stopEngine() {
        stop = true;
    }

    /**
     * 手动设置偏差。<b>设置后必须重启!!!</b>当两个平台之间长期不交叉时，把平台价格看作是围绕现在的价格波动。
     *
     * @param adjustPrice 价格偏差设置。格式 okcoin:1.2,btcchina:-1.2
     */
    public void saveAdjustPrice(String adjustPrice) throws Exception {
        synchronized (this) {
            String filePath = basePath + "/conf.xml";

            String[] adjustArr = adjustPrice.split(",");
            for (int i = 0; i < adjustArr.length; i++) {//处理每一个平台
                String adjStr = adjustArr[i];
                String[] arr = adjStr.split(":");// okcoin:1.2
                if (arr.length != 2 || arr[1] == null || arr[1].equals("")) {//如果某平台值是空的，就跳过
                    continue;
                } else {
                    //试着转成double，看看是否报错
                    Double.parseDouble(arr[1]);
                }
                //设置偏差（大标签）
                XmlConfigUtil.saveXmlAttribute(filePath, "conf/" + arr[0], "changePrice", arr[1]);
                //初始化阀值（小标签），将大标签里面的每个小标签都设为1
                for (int j = 0; j < adjustArr.length; j++) {
                    if (i != j) {
                        String elementPath = "conf/" + arr[0] + "/" + adjustArr[j].split(":")[0];
                        XmlConfigUtil.saveXmlProp(filePath, elementPath, "" + (1 / prop.moneyPrice));
                    }
                }
            }

        }//synchronized
    }

    public String[] getEnablePlat() {
        return readXmlProp("enablePlat").split(",");
    }
}
