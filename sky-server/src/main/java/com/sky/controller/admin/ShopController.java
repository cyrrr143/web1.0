package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ShopService;
import com.sky.vo.ShopStatusVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

/**
 * 店铺操作接口
 */
@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Api(tags = "店铺操作接口")
@Slf4j
public class ShopController {

    @Autowired
    private ShopService shopService;

    /**
     * 获取店铺营业状态
     * @return 店铺营业状态
     */
    @GetMapping("/status")
    @ApiOperation("获取店铺营业状态")
    public Result<Integer> getShopStatus() {
        log.info("获取店铺营业状态");
        ShopStatusVO shopStatus = shopService.getShopStatus();
        return Result.success(shopStatus.getStatus());
    }

    /**
     * 设置店铺营业状态
     * @param status 营业状态：1为营业，0为打烊
     * @return 结果
     */
    @PutMapping("/{status}")
    @ApiOperation("设置店铺营业状态")
    @CacheEvict(value = "setmealCache", allEntries = true)
    public Result<String> setShopStatus(@PathVariable Integer status) {
        log.info("设置店铺营业状态为：{}", status);
        shopService.setShopStatus(status);
        return Result.success();
    }
}
