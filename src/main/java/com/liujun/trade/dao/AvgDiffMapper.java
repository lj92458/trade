package com.liujun.trade.dao;

import com.liujun.trade.common.mybatis._annotation.MyBatisRepository;
import com.liujun.trade.model.AvgDiff;
import com.liujun.trade.model.AvgDiffKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisRepository
public interface AvgDiffMapper {
    int deleteByPrimaryKey(AvgDiffKey key);

    int insert(AvgDiff record);

    int insertSelective(AvgDiff record);

    AvgDiff selectByPrimaryKey(AvgDiffKey key);

    int updateByPrimaryKeySelective(AvgDiff record);

    int updateByPrimaryKey(AvgDiff record);

    /**
     * 查询某平台的价格偏差, 按时间降序排列
     * @param plat 平台名称
     * @param timeUnit 两个时间点之间间隔多少分钟
     * @param beginTime 查询起始时间
     * @return
     */
    List<AvgDiff> select(@Param("plat") String plat, @Param("timeUnit") int timeUnit, @Param("beginTime") String beginTime);
}