package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author：xlg
 * @Date：2024/1/25 15:43
 */
@Mapper
public interface DishFlavorMapper {
    /**
     * 批量新增菜品口味
     * @param dishFlavors
     */
    @AutoFill(OperationType.INSERT)
    void insertBatch(List<DishFlavor> dishFlavors);

    /**
     * 通过菜品id删除相关联的口味数据
     * @param id
     */
    @Delete("delete from dish_flavor where dish_id = #{id}")
    void deleteByDishId(Long id);
}
