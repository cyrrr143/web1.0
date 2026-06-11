package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    
    @Autowired
    private DishMapper dishMapper;
    
    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    public List<ShoppingCart> list() {
        Long userId = BaseContext.getCurrentId();
        List<ShoppingCart> list = shoppingCartMapper.list(userId);
        return list;
    }

    @Override
    public void cleanShoppingCart() {
        log.info("清空购物车");
        Long userId = BaseContext.getCurrentId();
        shoppingCartMapper.deleteByUserId(userId);
    }

    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        log.info("添加购物车：{}", shoppingCartDTO);
        
        // 1. 获取当前用户ID
        Long userId = BaseContext.getCurrentId();

        // 2. 构建查询条件，用来判断购物车中是否已存在该商品
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(userId);
        
        // 3. 查询购物车中是否存在该商品
        ShoppingCart cart = shoppingCartMapper.get(shoppingCart);
        
        // 4. 如果已存在，数量+1
        if (cart != null) {
            cart.setNumber(cart.getNumber() + 1);
            shoppingCartMapper.updateNumberById(cart);
            log.info("商品已存在，数量+1，当前数量：{}", cart.getNumber());
        } else {
            // 5. 如果不存在，新增购物车记录
            
            // 判断是菜品还是套餐
            Long dishId = shoppingCartDTO.getDishId();
            Long setmealId = shoppingCartDTO.getSetmealId();
            
            if (dishId != null) {
                // 添加的是菜品
                Dish dish = dishMapper.getDishById(dishId);
                
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
                shoppingCart.setNumber(1);
                shoppingCart.setDishFlavor(shoppingCartDTO.getDishFlavor());
                
            } else if (setmealId != null) {
                // 添加的是套餐
                com.sky.vo.SetmealVO setmealVO = setmealMapper.getByIdWithCategoryName(setmealId);
                
                shoppingCart.setName(setmealVO.getName());
                shoppingCart.setImage(setmealVO.getImage());
                shoppingCart.setAmount(setmealVO.getPrice());
                shoppingCart.setNumber(1);
                
            }
            
            // 设置其他属性
            shoppingCart.setUserId(userId);
            shoppingCart.setCreateTime(LocalDateTime.now());
            
            // 插入购物车
            shoppingCartMapper.insert(shoppingCart);
            log.info("新增购物车商品");
        }
    }
}
