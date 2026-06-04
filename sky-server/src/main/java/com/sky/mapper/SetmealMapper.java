package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SetmealMapper {

    /**
     * 根据分类id查询套餐的数量
     * @param id
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    /**
     * 根据菜品id查询关联的套餐数量
     * @param dishId
     * @return
     */
    @Select("select count(s.id) from setmeal s left join setmeal_dish sd on s.id = sd.setmeal_id where sd.dish_id = #{dishId}")
    Integer countByDishId(Long dishId);

}
