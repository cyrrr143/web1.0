package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 根据id查询套餐（含关联菜品）
     * @param id
     * @return
     */
    @Override
    public SetmealVO getByIdWithDish(Long id) {
        // 查询套餐基本信息 + 分类名称
        SetmealVO setmealVO = setmealMapper.getByIdWithCategoryName(id);
        
        // 查询该套餐关联的菜品列表
        List<SetmealDish> setmealDishes = setmealDishMapper.getBySetmealId(id);
        
        // 组装菜品列表
        setmealVO.setSetmealDishes(setmealDishes);
        
        return setmealVO;
    }

    /**
     * 新增套餐，同时保存套餐和菜品的关联关系
     * @param setmealDTO
     */
    @Override
    @Transactional
    public void saveWithDishes(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        // 属性拷贝
        BeanUtils.copyProperties(setmealDTO, setmeal);
        
        // 插入套餐主表
        setmealMapper.insert(setmeal);
        
        // 获取生成的套餐ID
        Long setmealId = setmeal.getId();
        
        // 处理套餐菜品关系
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && setmealDishes.size() > 0) {
            for (SetmealDish setmealDish : setmealDishes) {
                // 设置套餐ID
                setmealDish.setSetmealId(setmealId);
            }
            // 批量插入套餐菜品关系
            setmealDishMapper.insertBatch(setmealDishes);
        }
    }

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        // 开启分页
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        
        // 执行查询
        Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);
        
        // 封装结果
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 启用、禁用套餐
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        Setmeal setmeal = Setmeal.builder()
                .id(id)
                .status(status)
                .build();
        setmealMapper.update(setmeal);
    }

    /**
     * 批量删除套餐
     * @param ids
     */
    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        // 遍历每个套餐id进行校验
        for (Long id : ids) {
            SetmealVO setmealVO = setmealMapper.getByIdWithCategoryName(id);
            // 校验套餐是否起售中
            if (setmealVO.getStatus() == 1) {
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }
        
        // 删除套餐关联的菜品关系数据
        setmealDishMapper.deleteBySetmealIds(ids);
        
        // 删除套餐数据
        setmealMapper.deleteByIds(ids);
    }

    /**
     * 修改套餐，同时更新套餐和菜品的关联关系
     * @param setmealDTO
     */
    @Override
    @Transactional
    public void updateWithDishes(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        // 属性拷贝
        BeanUtils.copyProperties(setmealDTO, setmeal);
        
        // 更新套餐主表
        setmealMapper.update(setmeal);
        
        // 获取套餐ID
        Long setmealId = setmealDTO.getId();
        
        // 删除该套餐原有的菜品关联关系
        setmealDishMapper.deleteBySetmealIds(java.util.Collections.singletonList(setmealId));
        
        // 插入新的套餐菜品关系
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && setmealDishes.size() > 0) {
            for (SetmealDish setmealDish : setmealDishes) {
                // 设置套餐ID
                setmealDish.setSetmealId(setmealId);
            }
            // 批量插入套餐菜品关系
            setmealDishMapper.insertBatch(setmealDishes);
        }
    }

    /**
     * 根据分类id查询套餐
     * @param categoryId
     * @return
     */
    @Override
    public List<Setmeal> listByCategoryId(Long categoryId) {
        return setmealMapper.listByCategoryId(categoryId);
    }
    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    @Override
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }
    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }
}
