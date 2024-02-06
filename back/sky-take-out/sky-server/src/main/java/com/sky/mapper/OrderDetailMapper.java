package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author：xlg
 * @Date：2024/2/6 10:44
 */
@Mapper
public interface OrderDetailMapper {
    /**
     * 批量插入订单明细
     * @param orderDetails
     */
    void insertBatch(List<OrderDetail> orderDetails);
}
