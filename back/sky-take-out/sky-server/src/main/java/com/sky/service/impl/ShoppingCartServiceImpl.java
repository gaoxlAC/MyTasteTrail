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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author：xlg
 * @Date：2024/2/4 18:07
 */
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService{

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        // 判断当前购物车记录是否存在
        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.list(shoppingCart);
        // 若存在则对应的菜品或者套餐数量+1
        if(shoppingCarts != null && shoppingCarts.size() > 0){
            shoppingCart = shoppingCarts.get(0);
            shoppingCart.setNumber(shoppingCart.getNumber() + 1);
            shoppingCartMapper.updateNumberById(shoppingCart);  // 更新购物车记录对应的数量+1
        }else{
            // 若不存在则将该购物车记录添加到数据库中
            Long dishId = shoppingCartDTO.getDishId();
            // 判断添加的是菜品还是套餐
            // 若是菜品则根据菜品id查询菜品信息并添加进购物车
            if(dishId != null){
                Dish dish = dishMapper.getById(dishId);
                shoppingCart.setName(dish.getName());  // 菜品名称
                shoppingCart.setAmount(dish.getPrice());  // 菜品单价
                shoppingCart.setImage(dish.getImage()); // 菜品图片

            }else{
                Setmeal setmeal = setmealMapper.getById(shoppingCartDTO.getSetmealId());
                shoppingCart.setName(setmeal.getName());  // 套餐名称
                shoppingCart.setAmount(setmeal.getPrice());  // 套餐单价
                shoppingCart.setImage(setmeal.getImage());  // 套餐图片
            }
            shoppingCart.setNumber(1);  // 菜品数量默认为1
            shoppingCart.setCreateTime(LocalDateTime.now());  // 创建时间默认为当前时间
            shoppingCartMapper.insert(shoppingCart);  // 添加购物车记录
        }
    }

    /**
     * 展示购物车列表
     * @return
     */
    @Override
    public List<ShoppingCart> showShoppingCartList() {
        // 获取当前的用户id
        Long userId = BaseContext.getCurrentId();

        // 通过动态sql查询购物车列表
        ShoppingCart shoppingCart = ShoppingCart.builder()
                                            .userId(userId)
                                            .build();
        return shoppingCartMapper.list(shoppingCart);
    }

    /**
     * 删除购物车记录
     */
    @Override
    public void cleanShoppingCart() {
        // 获取当前的用户id
        Long userId = BaseContext.getCurrentId();
        // 删除购物车记录
        shoppingCartMapper.deleteByUserId(userId);
    }

    /**
     * 减少购物车
     * @param shoppingCartDTO
     */
    @Override
    @Transactional
    public void sub(ShoppingCartDTO shoppingCartDTO) {
        // 从数据库中查询对应的购物车记录
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.list(shoppingCart);
        // 对应的菜品或者套餐数量-1并更新，若减为0则删除该购物车记录
        if (shoppingCarts != null && shoppingCarts.size() > 0) {
            shoppingCart = shoppingCarts.get(0);
            if(shoppingCart.getNumber() == 1){
                shoppingCartMapper.deleteById(shoppingCart.getId());
            }else{
                shoppingCart.setNumber(shoppingCart.getNumber() - 1);
                shoppingCartMapper.updateNumberById(shoppingCart);
            }
        }
    }
}
