package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;

/**
 * @Author：xlg
 * @Date：2024/1/29 19:52
 */
public interface SetmealService {
    /**
     * 新增套餐
     * @param setmealDTO
     */
    void saveWithDish(SetmealDTO setmealDTO);

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 删除套餐
     * @param ids
     */
    void delete(List<Long> ids);

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    SetmealVO getSetmealVOById(Long id);

    /**
     * 套餐起售和禁售
     * @param id
     * @param status
     */
    void startOrStop(Long id, Integer status);

    /**
     * 更新套餐
     * @param setmealDTO
     */
    void update(SetmealDTO setmealDTO);

    /**
     * 查询套餐
     * @param setmeal
     * @return
     */
    List<Setmeal> list(Setmeal setmeal);

    /**
     * 根据id查询套餐详情
     * @param id
     * @return
     */
    List<DishItemVO> getDishItemById(Long id);
}
