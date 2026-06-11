package com.sky.controller.user;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrdersService;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/order")
@Api(tags = "C端-订单相关接口")
@Slf4j
public class UserOrderController {

    @Autowired
    private OrdersService ordersService;

    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    @PostMapping("/submit")
    @ApiOperation("用户下单")
    public Result<OrderVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO) {
        log.info("用户下单，参数：{}", ordersSubmitDTO);
        OrderVO orderVO = ordersService.submitOrder(ordersSubmitDTO);
        return Result.success(orderVO);
    }

    /**
     * 模拟支付（测试用）
     * @param ordersPaymentDTO
     * @return
     */
    @PostMapping("/payment")
    @ApiOperation("模拟支付")
    public Result<String> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) {
        log.info("模拟支付，订单号：{}", ordersPaymentDTO.getOrderNumber());
        try {
            ordersService.paymentSuccess(ordersPaymentDTO.getOrderNumber());
            return Result.success("支付成功");
        } catch (Exception e) {
            log.error("支付失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 历史订单查询（分页）
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    @GetMapping("/historyOrders")
    @ApiOperation("历史订单查询")
    public Result<PageResult> page(int page, int pageSize, Integer status) {
        log.info("历史订单查询，page={}, pageSize={}, status={}", page, pageSize, status);
        
        OrdersPageQueryDTO ordersPageQueryDTO = new OrdersPageQueryDTO();
        ordersPageQueryDTO.setPage(page);
        ordersPageQueryDTO.setPageSize(pageSize);
        ordersPageQueryDTO.setStatus(status);
        
        PageResult pageResult = ordersService.pageQueryForUser(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 查询订单详情
     * @param id
     * @return
     */
    @GetMapping("/orderDetail/{id}")
    @ApiOperation("查询订单详情")
    public Result<OrderVO> details(@PathVariable Long id) {
        log.info("查询订单详情，订单ID：{}", id);
        OrderVO orderVO = ordersService.details(id);
        return Result.success(orderVO);
    }

    /**
     * 取消订单
     * @param id
     * @return
     */
    @PutMapping("/cancel/{id}")
    @ApiOperation("取消订单")
    public Result<String> cancel(@PathVariable Long id) {
        log.info("取消订单，订单ID：{}", id);
        ordersService.userCancelById(id);
        return Result.success("订单已取消");
    }

    /**
     * 再来一单
     * @param id
     * @return
     */
    @PostMapping("/repetition/{id}")
    @ApiOperation("再来一单")
    public Result<String> repetition(@PathVariable Long id) {
        log.info("再来一单，订单ID：{}", id);
        ordersService.repetition(id);
        return Result.success("已将商品加入购物车");
    }

    /**
     * 催单
     * @param id
     * @return
     */
    @GetMapping("/reminder/{id}")
    @ApiOperation("催单")
    public Result<String> reminder(@PathVariable Long id) {
        log.info("催单，订单ID：{}", id);
        ordersService.reminder(id);
        return Result.success("催单成功，我们会尽快为您配送");
    }
}
