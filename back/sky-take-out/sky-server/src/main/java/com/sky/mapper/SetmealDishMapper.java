package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import io.swagger.annotations.SwaggerDefinition;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

@Mapper
public interface SetmealDishMapper {


    /**
     * 根据菜品id获取对应的套餐id
     * @param dishIds
     * @return
     */
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);

    /**
     * 批量插入菜品和套餐的关系
     * @param setmealDishes
     */
    void saveBatch(List<SetmealDish> setmealDishes);

    /**
     * 根据套餐id删除套餐和菜品的关系
     * @param ids
     */
    void deleteBySetmealIds(List<Long> ids);

    /**
     * 根据套餐id查询菜品id
     * @param id
     * @return
     */
    @Select("select dish_id from setmeal_dish where setmeal_id = #{id}")
    Set<Long> getDishIdsBySetmealId(Long id);

    /**
     * 根据套餐id查询菜品
     * @param id
     * @return
     */
    @Select("select * from setmeal_dish where setmeal_id = #{id}")
    List<SetmealDish> getBySetmealId(Long id);
}
