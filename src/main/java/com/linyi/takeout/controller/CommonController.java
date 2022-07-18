package com.linyi.takeout.controller;

/**
 * @author linyi
 * @date 2022/7/14
 * 1.0
 */

import com.linyi.takeout.common.CustomException;
import com.linyi.takeout.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传与下载
 */

@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {

        //获取原图片名
        String originalFilename1 = file.getOriginalFilename();
        //截取.jpg
        String fileName = originalFilename1.substring(originalFilename1.lastIndexOf("."));
        log.info("原图片名带后缀的：{}",originalFilename1);
        log.info("原图片名后缀：{}",fileName);
        fileName = UUID.randomUUID().toString()+fileName;
        log.info("UUID后图片名：{}",fileName);

        //创建图片路径
        File path = new File(basePath);
        //判断图片存放路径是否存在
        if (!path.exists()) {
            //目录不存在则创建目录
            path.mkdirs();
        }

        //存放图片的路径
        try {
            file.transferTo(new File(basePath + fileName));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(fileName);
    }


    /**
     * 文件下载
     *
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response)  {
        try {
            //读取文件内容

            //输入流，通过输入流读取文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));

            // 输出流，通过输出流将文件写入到浏览器，在浏览器展示图片
            ServletOutputStream outputStream = response.getOutputStream();
            //设置响应方式，图片文件
            response.setContentType("image/jpeg");

            //读取一行一行
            int len = 0;

            byte[] bytes = new byte[1024];

            while ((len = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }
            //关闭资源
            outputStream.close();
            fileInputStream.close();
        } catch (IOException e) {
            throw  new CustomException("没有找到该图片:"+name);
        }
    }


}



