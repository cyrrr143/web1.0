package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrdersMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.result.PageResult;
import com.sky.service.OrdersService;
import com.sky.vo.OrderStatisticsVO;
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
import java.util.stream.Collectors;

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

    /**
     * 查询历史订单（分页）
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQueryForUser(OrdersPageQueryDTO ordersPageQueryDTO) {
        log.info("查询用户历史订单，参数：{}", ordersPageQueryDTO);

        // 1. 获取当前用户ID
        Long userId = BaseContext.getCurrentId();
        ordersPageQueryDTO.setUserId(userId);

        // 2. 设置分页参数
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());

        // 3. 调用Mapper查询订单列表
        List<Orders> ordersList = ordersMapper.pageQuery(ordersPageQueryDTO);

        // 4. 封装返回结果
        PageResult pageResult = new PageResult();
        pageResult.setTotal(((Page<Orders>) ordersList).getTotal());

        // 5. 为每个订单填充订单明细
        List<OrderVO> orderVOList = new ArrayList<>();
        for (Orders orders : ordersList) {
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(orders, orderVO);

            // 查询订单明细
            List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(orders.getId());
            orderVO.setOrderDetailList(orderDetailList);

            orderVOList.add(orderVO);
        }

        pageResult.setRecords(orderVOList);
        return pageResult;
    }

    /**
     * 查询订单详情
     * @param id
     * @return
     */
    @Override
    public OrderVO details(Long id) {
        log.info("查询订单详情，订单ID：{}", id);

        // 1. 根据ID查询订单
        Orders orders = ordersMapper.getById(id);

        if (orders == null) {
            throw new OrderBusinessException("订单不存在！");
        }

        // 2. 校验是否为当前用户的订单
        Long userId = BaseContext.getCurrentId();
        if (!orders.getUserId().equals(userId)) {
            throw new OrderBusinessException("无权查看此订单！");
        }

        // 3. 构建OrderVO
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);

        // 4. 查询订单明细
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);
        orderVO.setOrderDetailList(orderDetailList);

        return orderVO;
    }

    /**
     * 取消订单
     * @param id
     */
    @Override
    @Transactional
    public void userCancelById(Long id) {
        log.info("用户取消订单，订单ID：{}", id);

        // 1. 根据ID查询订单
        Orders orders = ordersMapper.getById(id);

        if (orders == null) {
            throw new OrderBusinessException("订单不存在！");
        }

        // 2. 校验是否为当前用户的订单
        Long userId = BaseContext.getCurrentId();
        if (!orders.getUserId().equals(userId)) {
            throw new OrderBusinessException("无权操作此订单！");
        }

        // 3. 校验订单状态，只有待付款和待接单可以取消
        if (!orders.getStatus().equals(Orders.PENDING_PAYMENT) && 
            !orders.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
            throw new OrderBusinessException("订单状态不正确，无法取消！");
        }

        // 4. 更新订单状态为已取消
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason("用户取消");
        orders.setCancelTime(LocalDateTime.now());

        ordersMapper.update(orders);

        log.info("订单取消成功，订单ID：{}", id);
    }

    /**
     * 再来一单
     * @param id
     */
    @Override
    @Transactional
    public void repetition(Long id) {
        log.info("再来一单，订单ID：{}", id);

        // 1. 获取当前用户ID
        Long userId = BaseContext.getCurrentId();

        // 2. 查询原订单的订单明细
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);

        if (orderDetailList == null || orderDetailList.isEmpty()) {
            throw new OrderBusinessException("订单明细不存在！");
        }

        // 3. 将订单明细转换为购物车商品
        List<ShoppingCart> shoppingCartList = new ArrayList<>();
        for (OrderDetail orderDetail : orderDetailList) {
            ShoppingCart shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(orderDetail, shoppingCart, "id");
            shoppingCart.setUserId(userId);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartList.add(shoppingCart);
        }

        // 4. 批量插入购物车
        shoppingCartMapper.insertBatch(shoppingCartList);

        log.info("再来一单成功，已将商品加入购物车");
    }

    /**
     * 催单
     * @param id
     */
    @Override
    public void reminder(Long id) {
        log.info("用户催单，订单ID：{}", id);

        // 1. 根据ID查询订单
        Orders orders = ordersMapper.getById(id);

        if (orders == null) {
            throw new OrderBusinessException("订单不存在！");
        }

        // 2. 校验是否为当前用户的订单
        Long userId = BaseContext.getCurrentId();
        if (!orders.getUserId().equals(userId)) {
            throw new OrderBusinessException("无权操作此订单！");
        }

        // 3. 校验订单状态，只有已接单的订单才能催单
        if (!orders.getStatus().equals(Orders.CONFIRMED) && 
            !orders.getStatus().equals(Orders.DELIVERY_IN_PROGRESS)) {
            throw new OrderBusinessException("订单状态不正确，无法催单！");
        }

        // 4. 这里只是记录催单操作，实际项目中可以通过WebSocket推送给商家
        log.info("催单成功，订单ID：{}，订单号：{}", id, orders.getNumber());
    }

    /**
     * 管理端条件搜索订单
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        log.info("管理端订单搜索，参数：{}", ordersPageQueryDTO);

        // 1. 设置分页参数
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());

        // 2. 调用Mapper查询订单列表
        List<Orders> ordersList = ordersMapper.pageQuery(ordersPageQueryDTO);

        // 3. 封装返回结果
        PageResult pageResult = new PageResult();
        pageResult.setTotal(((Page<Orders>) ordersList).getTotal());

        // 4. 为每个订单填充订单明细和菜品字符串
        List<OrderVO> orderVOList = new ArrayList<>();
        for (Orders orders : ordersList) {
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(orders, orderVO);

            // 查询订单明细
            List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(orders.getId());
            orderVO.setOrderDetailList(orderDetailList);

            // 拼接订单菜品的字符串描述
            String orderDishes = getOrderDishesStr(orderDetailList);
            orderVO.setOrderDishes(orderDishes);

            orderVOList.add(orderVO);
        }

        pageResult.setRecords(orderVOList);
        return pageResult;
    }

    /**
     * 各个状态的订单数量统计
     * @return
     */
    @Override
    public OrderStatisticsVO statistics() {
        log.info("统计各个状态的订单数量");

        // 1. 查询待接单订单数量（状态=2）
        OrdersPageQueryDTO toBeConfirmedQuery = new OrdersPageQueryDTO();
        toBeConfirmedQuery.setStatus(Orders.TO_BE_CONFIRMED);
        List<Orders> toBeConfirmedList = ordersMapper.pageQuery(toBeConfirmedQuery);
        int toBeConfirmedCount = toBeConfirmedList.size();

        // 2. 查询待派送订单数量（状态=3）
        OrdersPageQueryDTO confirmedQuery = new OrdersPageQueryDTO();
        confirmedQuery.setStatus(Orders.CONFIRMED);
        List<Orders> confirmedList = ordersMapper.pageQuery(confirmedQuery);
        int confirmedCount = confirmedList.size();

        // 3. 查询派送中订单数量（状态=4）
        OrdersPageQueryDTO deliveryQuery = new OrdersPageQueryDTO();
        deliveryQuery.setStatus(Orders.DELIVERY_IN_PROGRESS);
        List<Orders> deliveryList = ordersMapper.pageQuery(deliveryQuery);
        int deliveryCount = deliveryList.size();

        // 4. 构建统计结果
        return OrderStatisticsVO.builder()
                .toBeConfirmed(toBeConfirmedCount)
                .confirmed(confirmedCount)
                .deliveryInProgress(deliveryCount)
                .build();
    }

    /**
     * 查询订单详情（管理端）
     * @param id
     * @return
     */
    @Override
    public OrderVO adminDetails(Long id) {
        log.info("管理端查询订单详情，订单ID：{}", id);

        // 1. 根据ID查询订单
        Orders orders = ordersMapper.getById(id);

        if (orders == null) {
            throw new OrderBusinessException("订单不存在！");
        }

        // 2. 构建OrderVO
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);

        // 3. 查询订单明细
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);
        orderVO.setOrderDetailList(orderDetailList);

        // 4. 拼接订单菜品字符串
        String orderDishes = getOrderDishesStr(orderDetailList);
        orderVO.setOrderDishes(orderDishes);

        return orderVO;
    }

    /**
     * 接单
     * @param ordersConfirmDTO
     */
    @Override
    @Transactional
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        log.info("接单，订单ID：{}", ordersConfirmDTO.getId());

        // 1. 根据ID查询订单
        Orders orders = ordersMapper.getById(ordersConfirmDTO.getId());

        if (orders == null) {
            throw new OrderBusinessException("订单不存在！");
        }

        // 2. 校验订单状态，只有待接单的订单才能接单
        if (!orders.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
            throw new OrderBusinessException("订单状态不正确，无法接单！");
        }

        // 3. 更新订单状态为已接单
        orders.setStatus(Orders.CONFIRMED);
        ordersMapper.update(orders);

        log.info("接单成功，订单ID：{}", ordersConfirmDTO.getId());
    }

    /**
     * 拒单
     * @param ordersRejectionDTO
     */
    @Override
    @Transactional
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) {
        log.info("拒单，订单ID：{}，原因：{}", ordersRejectionDTO.getId(), ordersRejectionDTO.getRejectionReason());

        // 1. 根据ID查询订单
        Orders orders = ordersMapper.getById(ordersRejectionDTO.getId());

        if (orders == null) {
            throw new OrderBusinessException("订单不存在！");
        }

        // 2. 校验订单状态，只有待接单的订单才能拒单
        if (!orders.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
            throw new OrderBusinessException("订单状态不正确，无法拒单！");
        }

        // 3. 更新订单状态为已取消，并记录拒单原因
        orders.setStatus(Orders.CANCELLED);
        orders.setRejectionReason(ordersRejectionDTO.getRejectionReason());
        orders.setCancelTime(LocalDateTime.now());

        ordersMapper.update(orders);

        log.info("拒单成功，订单ID：{}", ordersRejectionDTO.getId());
    }

    /**
     * 取消订单（管理端）
     * @param ordersCancelDTO
     */
    @Override
    @Transactional
    public void cancel(OrdersCancelDTO ordersCancelDTO) {
        log.info("管理端取消订单，订单ID：{}，原因：{}", ordersCancelDTO.getId(), ordersCancelDTO.getCancelReason());

        // 1. 根据ID查询订单
        Orders orders = ordersMapper.getById(ordersCancelDTO.getId());

        if (orders == null) {
            throw new OrderBusinessException("订单不存在！");
        }

        // 2. 更新订单状态为已取消，并记录取消原因
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason(ordersCancelDTO.getCancelReason());
        orders.setCancelTime(LocalDateTime.now());

        ordersMapper.update(orders);

        log.info("管理端取消订单成功，订单ID：{}", ordersCancelDTO.getId());
    }

    /**
     * 派送订单
     * @param id
     */
    @Override
    @Transactional
    public void delivery(Long id) {
        log.info("派送订单，订单ID：{}", id);

        // 1. 根据ID查询订单
        Orders orders = ordersMapper.getById(id);

        if (orders == null) {
            throw new OrderBusinessException("订单不存在！");
        }

        // 2. 校验订单状态，只有已接单的订单才能派送
        if (!orders.getStatus().equals(Orders.CONFIRMED)) {
            throw new OrderBusinessException("订单状态不正确，无法派送！");
        }

        // 3. 更新订单状态为派送中
        orders.setStatus(Orders.DELIVERY_IN_PROGRESS);
        ordersMapper.update(orders);

        log.info("订单派送成功，订单ID：{}", id);
    }

    /**
     * 完成订单
     * @param id
     */
    @Override
    @Transactional
    public void complete(Long id) {
        log.info("完成订单，订单ID：{}", id);

        // 1. 根据ID查询订单
        Orders orders = ordersMapper.getById(id);

        if (orders == null) {
            throw new OrderBusinessException("订单不存在！");
        }

        // 2. 校验订单状态，只有派送中的订单才能完成
        if (!orders.getStatus().equals(Orders.DELIVERY_IN_PROGRESS)) {
            throw new OrderBusinessException("订单状态不正确，无法完成！");
        }

        // 3. 更新订单状态为已完成
        orders.setStatus(Orders.COMPLETED);
        orders.setDeliveryTime(LocalDateTime.now());
        ordersMapper.update(orders);

        log.info("订单完成成功，订单ID：{}", id);
    }

    /**
     * 将订单明细列表转换为菜品字符串描述
     * @param orderDetailList
     * @return
     */
    private String getOrderDishesStr(List<OrderDetail> orderDetailList) {
        if (orderDetailList == null || orderDetailList.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < orderDetailList.size(); i++) {
            OrderDetail detail = orderDetailList.get(i);
            sb.append(detail.getName())
              .append("*")
              .append(detail.getNumber());

            if (i < orderDetailList.size() - 1) {
                sb.append(";");
            }
        }

        return sb.toString();
    }

    /**
     * 模拟支付成功（测试用）
     * 直接将订单状态更新为已支付
     * @param orderNumber
     */
    @Override
    @Transactional
    public void paymentSuccess(String orderNumber) {
        log.info("模拟支付成功，订单号：{}", orderNumber);

        // 1. 根据订单号查询订单
        Orders orders = ordersMapper.getByNumber(orderNumber);
        
        // 2. 校验订单是否存在
        if (orders == null) {
            throw new OrderBusinessException("订单不存在！");
        }
        
        // 3. 校验订单状态
        if (!orders.getStatus().equals(Orders.PENDING_PAYMENT)) {
            throw new OrderBusinessException("订单状态不正确，无法支付！");
        }

        // 4. 更新订单状态为"待接单"（已支付）
        orders.setStatus(Orders.TO_BE_CONFIRMED);
        orders.setPayStatus(Orders.PAID);
        orders.setCheckoutTime(LocalDateTime.now());
        
        // 5. 保存更新后的订单信息
        ordersMapper.update(orders);
        
        log.info("订单支付成功，订单号：{}，状态已更新为已支付", orderNumber);
    }
}
