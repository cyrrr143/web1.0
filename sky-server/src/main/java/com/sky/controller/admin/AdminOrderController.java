package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrdersService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端订单管理
 */
@RestController
@RequestMapping("/admin/order")
@Api(tags = "管理端-订单管理接口")
@Slf4j
public class AdminOrderController {

    @Autowired
    private OrdersService ordersService;

    /**
     * 订单搜索（条件查询）
     * @param page
     * @param pageSize
     * @param number
     * @param phone
     * @param status
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/conditionSearch")
    @ApiOperation("订单搜索")
    public Result<PageResult> conditionSearch(int page, int pageSize,
                                              @RequestParam(required = false) String number,
                                              @RequestParam(required = false) String phone,
                                              @RequestParam(required = false) Integer status,
                                              @RequestParam(required = false) String beginTime,
                                              @RequestParam(required = false) String endTime) {
        log.info("订单搜索，page={}, pageSize={}, number={}, phone={}, status={}", 
                page, pageSize, number, phone, status);

        OrdersPageQueryDTO ordersPageQueryDTO = new OrdersPageQueryDTO();
        ordersPageQueryDTO.setPage(page);
        ordersPageQueryDTO.setPageSize(pageSize);
        ordersPageQueryDTO.setNumber(number);
        ordersPageQueryDTO.setPhone(phone);
        ordersPageQueryDTO.setStatus(status);

        PageResult pageResult = ordersService.conditionSearch(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 各个状态的订单数量统计
     * @return
     */
    @GetMapping("/statistics")
    @ApiOperation("各个状态的订单数量统计")
    public Result<OrderStatisticsVO> statistics() {
        log.info("统计各个状态的订单数量");
        OrderStatisticsVO statistics = ordersService.statistics();
        return Result.success(statistics);
    }

    /**
     * 查询订单详情
     * @param id
     * @return
     */
    @GetMapping("/details/{id}")
    @ApiOperation("查询订单详情")
    public Result<OrderVO> details(@PathVariable Long id) {
        log.info("查询订单详情，订单ID：{}", id);
        OrderVO orderVO = ordersService.adminDetails(id);
        return Result.success(orderVO);
    }

    /**
     * 接单
     * @param ordersConfirmDTO
     * @return
     */
    @PutMapping("/confirm")
    @ApiOperation("接单")
    public Result<String> confirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO) {
        log.info("接单，订单ID：{}", ordersConfirmDTO.getId());
        ordersService.confirm(ordersConfirmDTO);
        return Result.success("接单成功");
    }

    /**
     * 拒单
     * @param ordersRejectionDTO
     * @return
     */
    @PutMapping("/rejection")
    @ApiOperation("拒单")
    public Result<String> rejection(@RequestBody OrdersRejectionDTO ordersRejectionDTO) {
        log.info("拒单，订单ID：{}", ordersRejectionDTO.getId());
        ordersService.rejection(ordersRejectionDTO);
        return Result.success("拒单成功");
    }

    /**
     * 取消订单
     * @param ordersCancelDTO
     * @return
     */
    @PutMapping("/cancel")
    @ApiOperation("取消订单")
    public Result<String> cancel(@RequestBody OrdersCancelDTO ordersCancelDTO) {
        log.info("取消订单，订单ID：{}", ordersCancelDTO.getId());
        ordersService.cancel(ordersCancelDTO);
        return Result.success("订单已取消");
    }

    /**
     * 派送订单
     * @param id
     * @return
     */
    @PutMapping("/delivery/{id}")
    @ApiOperation("派送订单")
    public Result<String> delivery(@PathVariable Long id) {
        log.info("派送订单，订单ID：{}", id);
        ordersService.delivery(id);
        return Result.success("订单已派送");
    }

    /**
     * 完成订单
     * @param id
     * @return
     */
    @PutMapping("/complete/{id}")
    @ApiOperation("完成订单")
    public Result<String> complete(@PathVariable Long id) {
        log.info("完成订单，订单ID：{}", id);
        ordersService.complete(id);
        return Result.success("订单已完成");
    }
}
