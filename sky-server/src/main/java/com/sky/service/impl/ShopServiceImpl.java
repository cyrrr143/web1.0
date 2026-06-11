package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.service.ShopService;
import com.sky.vo.ShopStatusVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 店铺服务实现类
 */
@Service
@Slf4j
public class ShopServiceImpl implements ShopService {

    // Redis中存储店铺营业状态的key
    private static final String SHOP_STATUS_KEY = "SHOP_STATUS";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 获取店铺营业状态
     * @return 店铺营业状态
     */
    @Override
    public ShopStatusVO getShopStatus() {
        log.info("获取店铺营业状态");
        
        // 从Redis中获取店铺营业状态
        String status = stringRedisTemplate.opsForValue().get(SHOP_STATUS_KEY);
        
        // 如果Redis中没有数据，默认返回营业状态（1）
        if (status == null) {
            status = String.valueOf(StatusConstant.ENABLE);
            stringRedisTemplate.opsForValue().set(SHOP_STATUS_KEY, status);
        }
        
        return ShopStatusVO.builder()
                .status(Integer.parseInt(status))
                .build();
    }

    /**
     * 设置店铺营业状态
     * @param status 营业状态：1为营业，0为打烊
     */
    @Override
    public void setShopStatus(Integer status) {
        log.info("设置店铺营业状态为：{}", status);
        
        // 将营业状态存储到Redis中
        stringRedisTemplate.opsForValue().set(SHOP_STATUS_KEY, String.valueOf(status));
    }
}