package com.sky.service;

import com.sky.dto.OrdersSubmitDTO;
import com.sky.vo.OrderVO;

public interface OrdersService {

    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    OrderVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);
}
