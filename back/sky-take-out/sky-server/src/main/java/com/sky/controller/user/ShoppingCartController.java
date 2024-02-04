package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author：xlg
 * @Date：2024/2/4 18:04
 */
@RestController
@RequestMapping("/user/shoppingCart")
@Api(tags = "C端-购物车接口")
@Slf4j
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     * @param shoppingCartDTO
     * @return
     */
    @PostMapping("/add")
    @ApiOperation(value = "添加购物车")
    public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO){
        shoppingCartService.add(shoppingCartDTO);
        return Result.success();
    }

    /**
     * 展示购物车列表
     * @return
     */
    @GetMapping("/list")
    @ApiOperation(value = "购物车列表")
    public Result<List<ShoppingCart>> list(){
        return Result.success(shoppingCartService.showShoppingCartList());
    }

    /**
     * 清空购物车
     * @param
     * @return
     */
    @DeleteMapping("/clean")
    @ApiOperation(value = "清空除购物车")
    public Result delete(){
        shoppingCartService.cleanShoppingCart();
        return Result.success();
    }

    /**
     * 减少购物车
     * @return
     */
    @PostMapping("/sub")
    @ApiOperation(value = "减少购物车")
    public Result sub(@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info("减少购物车 {}", shoppingCartDTO);
        shoppingCartService.sub(shoppingCartDTO);
        return Result.success();
    }
}
