package com.sky.service.impl;

import ch.qos.logback.classic.Logger;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author：xlg
 * @Date：2024/1/25 11:11
 */
@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;
    /**
     * 新增菜品
     * @param dishDTO
     */
    @Override
    public void saveWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        // 属性拷贝
        BeanUtils.copyProperties(dishDTO,dish);
        // 新增菜品
        dishMapper.insert(dish);

        // 新增菜品口味
        List<DishFlavor> dishFlavors = dishDTO.getFlavors();
        Long dishId = dish.getId();  // 通过generatedKeys获取到菜品id

        //  将菜品id设置到菜品口味中
        if(dishFlavors != null && dishFlavors.size() > 0){
            dishFlavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            // 批量插入
            dishFlavorMapper.insertBatch(dishFlavors);
        }
    }

    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        // 分页查询
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> dishVOS = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(dishVOS.getTotal(),dishVOS.getResult());
    }

    /**
     * 菜品批量删除
     * @param ids
     */
    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        // 不能删除的
        ids.forEach(id ->{Dish dish = dishMapper.getById(id);
        // 判断当前要删除的菜品的状态是否为起售状态
        if(dish.getStatus() == StatusConstant.ENABLE)
        {
            throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
        }
        });

        // 判断当前要删除的菜品是否被套餐关联
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if(setmealIds != null && setmealIds.size() > 0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        // 可以删除的，但还需要进一步删除相关联的口味的
        ids.forEach(id -> {dishMapper.deleteById(id);
        //删除口味中的数据
        dishFlavorMapper.deleteByDishId(id);
        });
    }

    /**
     * 菜品起售和停售
     * @param status
     */
    @Override
    @Transactional
    public void startOrStop(Integer status, Long id) {
        Dish dish = Dish.builder()
                            .id(id)
                            .status(status)
                            .build();
        dishMapper.update(dish);

        // 状态为停售，则相关的套餐也要停售
        if(Objects.equals(status, StatusConstant.DISABLE)) {
            // 查询有该菜品的套餐ids
            List<Long> dishIds = new ArrayList<>();
            dishIds.add(id);
            List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(dishIds);
            if(setmealIds != null && setmealIds.size() > 0){
                for(Long setmealId : setmealIds){
                    Setmeal setmeal = Setmeal.builder()
                                              .id(setmealId)
                                              .status(status)
                                              .build();
                    setmealMapper.update(setmeal);
                }
            }
        }
    }

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    @Override
    public DishVO getByIdWithFlavor(Long id) {
        // 获取菜品
        Dish dish = dishMapper.getById(id);

        // 查询相关菜品口味
        List<DishFlavor> dishFlavors = dishFlavorMapper.getByDishId(id);

        // 封装数据VO
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(dishFlavors);
        return dishVO;
    }

    /**
     * 更新菜品，入库多个表
     * @param dishDTO
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDTO dishDTO) {
        // 更新菜品
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.update(dish);

        // 更新菜品口味
        List<DishFlavor> dishFlavors = dishDTO.getFlavors();
        Long dishId = dish.getId();

        // 先删除
        dishFlavorMapper.deleteByDishId(dishId);
        // 再新增
        if(dishFlavors != null && dishFlavors.size() > 0){
            dishFlavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            System.out.println("新增口味："+dishFlavors);
            dishFlavorMapper.insertBatch(dishFlavors);
        }
    }

    @Override
    public List<Dish> list(Long categoryId) {
        Dish dish = Dish.builder()
                        .categoryId(categoryId)
                        .status(StatusConstant.ENABLE)
                        .build();
        return dishMapper.list(dish);
    }

}
