package com.liujun.trade.dao;

import com.liujun.trade.common.mybatis._annotation.MyBatisRepository;
import com.liujun.trade.model.UserAccount;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisRepository
public interface UserAccountMapper {
    int deleteByPrimaryKey(String userAccount);

    int insert(UserAccount record);

    int insertSelective(UserAccount record);

    UserAccount selectByPrimaryKey(String userAccount);

    int updateByPrimaryKeySelective(UserAccount record);

    int updateByPrimaryKey(UserAccount record);
    //

    List<UserAccount> selectByConditions(UserAccount params);

    UserAccount selectByAccountAndPassword(@Param("account")String account, @Param("password")String password);
}