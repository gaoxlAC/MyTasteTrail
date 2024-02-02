package com.sky.controller.admin;

import com.sky.constant.RedisKeyConstant;
import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * @Author：xlg
 * @Date：2024/2/2 14:33
 */
@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Slf4j
@Api(tags = "管理端商铺相关接口")
public class ShopController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @PutMapping("/{status}")
    @ApiOperation(value = "设置商铺状态")
    public Result setShopStatus(@PathVariable Integer status){
        log.info("设置商铺状态 {}", status == 1 ? "营业中" : "打烊中");
        redisTemplate.opsForValue().set(RedisKeyConstant.SHOP_STATUS, status);
        return Result.success();
    }

    @GetMapping("/status")
    @ApiOperation(value = "获取商铺状态")
    public Result<Integer> getShopStatus(){
        Integer status = (Integer) redisTemplate.opsForValue().get(RedisKeyConstant.SHOP_STATUS);
        if(status == null){
            return Result.error("店铺状态为空");
        }
        return Result.success(status);
    }
}
