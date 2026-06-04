package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    /**
     * 新增菜品，同时保存对应的口味数据
     * @param dishDTO
     */
    @Transactional//添加事务
    public void saveWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        // 属性拷贝
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.insert(dish);
        //获取新插入的菜品的id
        Long dishId = dish.getId();
        //口味表插入数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0)
        {
            for (DishFlavor flavor : flavors)
            {
                flavor.setDishId(dishId);
            }
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        // 遍历每个菜品id进行校验
        for (Long id : ids) {
            Dish dish = dishMapper.getDishById(id);
            // 校验菜品是否起售中
            if (dish.getStatus() == 1) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
            // 校验菜品是否关联了套餐
            Integer count = setmealMapper.countByDishId(id);
            if (count > 0) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
            }
        }
        // 删除菜品关联的口味数据
        dishFlavorMapper.deleteByDishIds(ids);
        // 删除菜品数据
        dishMapper.deleteByIds(ids);
    }

    @Override
    public DishVO getByIdWithFlavor(Long id) {
        // 查询菜品基本信息 + 分类名称
        DishVO dishVO = dishMapper.getByIdWithCategoryName(id);
        // 查询该菜品关联的口味数据
        List<DishFlavor> flavors = dishFlavorMapper.getByDishId(id);

        // 组装口味列表
        dishVO.setFlavors(flavors);
        return dishVO;
    }

    @Override
    /**
     * 修改菜品，同时更新对应的口味数据
     * @param dishDTO
     */
    @Transactional
    public void updateWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        // 属性拷贝
        BeanUtils.copyProperties(dishDTO, dish);
        
        // 更新菜品基本信息
        dishMapper.update(dish);
        
        // 删除该菜品原有的口味数据
        dishFlavorMapper.deleteByDishIds(Collections.singletonList(dish.getId()));
        
        // 插入新的口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            for (DishFlavor flavor : flavors) {
                flavor.setDishId(dish.getId());
            }
            dishFlavorMapper.insertBatch(flavors);
        }
    }
}
