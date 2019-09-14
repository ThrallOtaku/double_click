package com.enjoy.james.dao;


import com.enjoy.james.model.Order;


public interface OrderDao {
    int update(Order record);
    Order queryOrderByid(String orderid);
    int insert(Order record);

    int insertSelective(Order record);
    int updateByVersion(Order record);
}