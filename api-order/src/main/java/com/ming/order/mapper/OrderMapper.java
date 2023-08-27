package com.ming.order.mapper;

import com.ming.order.pojo.Order;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 86135
* @description 针对表【t_order】的数据库操作Mapper
* @createDate 2023-08-07 10:29:31
* @Entity com.ming.order.pojo.TOrder
*/
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

}




