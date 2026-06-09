package com.sky.controller.user;

import com.sky.result.Result;
import com.sky.service.ShopService;
import com.sky.vo.ShopStatusVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 店铺操作接口
 */
@RestController("userShopController")
@RequestMapping("/user/shop")
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


}
