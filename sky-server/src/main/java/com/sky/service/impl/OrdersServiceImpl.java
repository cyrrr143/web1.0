package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrdersMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.OrdersService;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 订单服务实现类
 * 处理用户下单相关的业务逻辑
 */
@Service
@Slf4j
public class OrdersServiceImpl implements OrdersService {

    // 注入订单Mapper，用于操作orders表
    @Autowired
    private OrdersMapper ordersMapper;

    // 注入订单明细Mapper，用于操作order_detail表
    @Autowired
    private OrderDetailMapper orderDetailMapper;

    // 注入地址簿Mapper，用于查询用户收货地址
    @Autowired
    private AddressBookMapper addressBookMapper;

    // 注入购物车Mapper，用于查询和清空购物车
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    /**
     * 用户下单
     * 业务流程：
     * 1. 获取当前登录用户ID
     * 2. 验证收货地址是否存在
     * 3. 查询购物车商品
     * 4. 创建订单主记录
     * 5. 创建订单明细记录
     * 6. 清空购物车
     * 7. 返回订单详情
     *
     * @param ordersSubmitDTO 订单提交DTO，包含地址ID、支付方式、备注、配送时间等信息
     * @return OrderVO 订单视图对象，包含订单基本信息和订单明细列表
     */
    @Transactional  // 开启事务，保证订单和订单明细的原子性操作
    @Override
    public OrderVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        // 记录日志，方便调试和追踪
        log.info("用户下单，参数：{}", ordersSubmitDTO);

        // 1. 从线程上下文中获取当前登录用户的ID（由JWT拦截器设置）
        Long userId = BaseContext.getCurrentId();

        // 2. 根据地址簿ID查询收货地址信息
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        // 如果地址不存在，抛出业务异常
        if (addressBook == null) {
            throw new AddressBookBusinessException("地址簿信息异常！");
        }

        // 3. 查询当前用户的购物车列表
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(userId);
        // 如果购物车为空，不允许下单
        if (shoppingCartList == null || shoppingCartList.isEmpty()) {
            throw new ShoppingCartBusinessException("购物车为空，不能下单！");
        }

        // 4. 创建订单对象，准备保存订单信息
        Orders orders = new Orders();
        // 将DTO中的属性复制到订单对象（如：支付方式、备注、配送时间、打包费、总金额等）
        BeanUtils.copyProperties(ordersSubmitDTO, orders);

        // 5. 生成唯一订单号（使用UUID，去除横杠）
        String orderNumber = UUID.randomUUID().toString().replace("-", "");
        orders.setNumber(orderNumber);

        // 6. 设置订单状态为"待付款"
        orders.setStatus(Orders.PENDING_PAYMENT);

        // 7. 设置下单用户ID
        orders.setUserId(userId);

        // 8. 设置下单时间为当前时间
        orders.setOrderTime(LocalDateTime.now());

        // 9. 设置支付状态为"未支付"
        orders.setPayStatus(Orders.UN_PAID);

        // 10. 从地址簿中获取收货人手机号
        orders.setPhone(addressBook.getPhone());

        // 11. 拼接完整收货地址（省+市+区+详细地址）
        orders.setAddress(addressBook.getProvinceName() + addressBook.getCityName() +
                addressBook.getDistrictName() + addressBook.getDetail());

        // 12. 设置收货人姓名
        orders.setConsignee(addressBook.getConsignee());

        // 13. 将订单信息插入数据库
        ordersMapper.insert(orders);
        // 注意：insert后，orders.getId()会自动回填生成的主键ID

        // 14. 创建订单明细列表
        List<OrderDetail> orderDetailList = new ArrayList<>();
        // 遍历购物车中的每个商品
        for (ShoppingCart cart : shoppingCartList) {
            // 创建订单明细对象
            OrderDetail orderDetail = new OrderDetail();
            // 将购物车商品的属性复制到订单明细（如：商品名、ID、口味、数量、金额、图片等）
            BeanUtils.copyProperties(cart, orderDetail);
            // 设置订单ID，关联订单主表
            orderDetail.setOrderId(orders.getId());
            // 添加到订单明细列表
            orderDetailList.add(orderDetail);
        }

        // 15. 批量插入订单明细到数据库
        orderDetailMapper.insertBatch(orderDetailList);

        // 16. 下单成功后，清空当前用户的购物车
        shoppingCartMapper.deleteByUserId(userId);

        // 17. 构建返回给前端的订单视图对象
        OrderVO orderVO = new OrderVO();
        // 将订单基本信息复制到OrderVO
        BeanUtils.copyProperties(orders, orderVO);
        // 设置订单明细列表
        orderVO.setOrderDetailList(orderDetailList);

        // 记录下单成功的日志
        log.info("下单成功，订单号：{}", orderNumber);

        // 18. 返回订单详情给前端
        return orderVO;
    }
}
