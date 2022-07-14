package com.linyi.takeout.common;

/**
 * @author linyi
 * @date 2022/7/14
 * 1.0
 */

/**
 * 自定义业务异常
 */
public class CustomException extends RuntimeException{
    public CustomException(String message){
        super(message);
    }
}
