package com.sky.service;

import com.sky.vo.ShopStatusVO;

/**
 * 店铺服务接口
 */
public interface ShopService {

    /**
     * 获取店铺营业状态
     * @return 店铺营业状态
     */
    ShopStatusVO getShopStatus();

    /**
     * 设置店铺营业状态
     * @param status 营业状态：1为营业，0为打烊
     */
    void setShopStatus(Integer status);
}