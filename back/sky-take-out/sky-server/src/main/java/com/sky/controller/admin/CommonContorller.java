package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * @Author：xlg
 * @Date：2024/1/25 11:22
 */
@RestController
@RequestMapping("/admin/common")
@Api(tags = "公共接口")
@Slf4j
public class CommonContorller {

    @Autowired
    private AliOssUtil aliOssUtil;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file){
        log.info("文件上传：{}", file.getName());

        // 获取原始文件名
        String originalFilename = file.getOriginalFilename();
        // 获取文件后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        // 上传文件到OSS
        String filename = UUID.randomUUID().toString() + suffix;
        try{
            String filePath = aliOssUtil.upload(file.getBytes(), filename);
            return Result.success(filePath);
        }catch (IOException e){
            log.error("文件上传失败：{}", e.getMessage());
        }
        return Result.error("文件上传失败");
    }
}
