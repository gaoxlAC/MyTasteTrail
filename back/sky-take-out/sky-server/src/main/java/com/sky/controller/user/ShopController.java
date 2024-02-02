package com.sky.controller.user;

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
@RestController("userShopController")
@RequestMapping("/user/shop")
@Slf4j
@Api(tags = "用户端商铺相关接口")
public class ShopController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


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
