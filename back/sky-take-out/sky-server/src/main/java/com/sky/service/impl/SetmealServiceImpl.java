package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @Author：xlg
 * @Date：2024/1/29 19:53
 */
@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private DishMapper dishMapper;

    @Override
    @Transactional
    public void saveWithDish(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        String[] ignoreProperties = {"setmealDishes"};
        BeanUtils.copyProperties(setmealDTO, setmeal, ignoreProperties);
        // 添加套餐
        setmealMapper.save(setmeal);

        // 获取套餐id
        Long setmealId = setmeal.getId();

        // 添加套餐和菜品的关系, 批量插入菜品和套餐的关系
        List<SetmealDish> setmealDishList = setmealDTO.getSetmealDishes();

        if(setmealDishList.size() > 0){
            setmealDishList.forEach(setmealDish -> {
                setmealDish.setSetmealId(setmealId);
            });
        }
        setmealDishMapper.saveBatch(setmealDishList);
    }

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> pages = setmealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(pages.getTotal(), pages.getResult());
    }

    /**
     * 套餐删除
     * @param ids
     */
    @Override
    @Transactional
    public void delete(List<Long> ids) {
        // 根据id查询并判断当前套餐是否禁售，若禁售可以删除，否则抛出不可删除异常
        ids.forEach(id -> {
            Setmeal setmeal = setmealMapper.getById(id);
            if(Objects.equals(setmeal.getStatus(), StatusConstant.ENABLE)){
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
            setmealMapper.deleteById(id);
        });

        // 直接删除套餐和菜品的关系
        setmealDishMapper.deleteBySetmealIds(ids);   // 可以使用统一的接口deleteBySetmealId一个一个删除
    }

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @Override
    public SetmealVO getSetmealVOById(Long id) {
        SetmealVO setmealVO = new SetmealVO();
        // 根据id查询套餐
        Setmeal setmeal = setmealMapper.getById(id);
        BeanUtils.copyProperties(setmeal, setmealVO);

        // 根据id查询套餐下的菜品
        List<SetmealDish> setmealDishes = setmealDishMapper.getBySetmealId(id);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    /**
     * 套餐起售和禁售
     * @param id
     * @param status
     */
    @Override
    public void startOrStop(Long id, Integer status) {
        // 起售套餐时，若该套餐下的菜品有禁售的，则不允许起售
        if(Objects.equals(status, StatusConstant.ENABLE)){
            Set<Long> dishIds = setmealDishMapper.getDishIdsBySetmealId(id);
            dishIds.forEach(dishId -> {
                Integer dishStatus = dishMapper.getDishStatusById(dishId);
                if(Objects.equals(dishStatus, StatusConstant.DISABLE)){
                    throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                }
            });
        }

        // 更新套餐状态
        Setmeal setmeal = Setmeal.builder()
                                  .id(id)
                                  .status(status)
                                  .build();
        setmealMapper.update(setmeal);
    }

    /**
     * 更新套餐
     * @param setmealDTO
     */
    @Override
    @Transactional
    public void update(SetmealDTO setmealDTO) {
        // 更新套餐
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.update(setmeal);

        // 删除套餐和菜品的关系
        List<Long> ids = Collections.singletonList(setmeal.getId());  // 创建一个包含单个元素的不可变的 List
        setmealDishMapper.deleteBySetmealIds(ids);

        // 添加套餐和菜品的关系
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if(setmealDishes.size() > 0){
            setmealDishes.forEach(setmealDish -> {
                setmealDish.setSetmealId(setmeal.getId());
            });
        }
        setmealDishMapper.saveBatch(setmealDishes);
    }

    /**
     * 查询所有套餐
     * @param setmeal
     * @return
     */
    @Override
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    @Override
    public List<DishItemVO> getDishItemById(Long id) {
        return null;
    }
}
