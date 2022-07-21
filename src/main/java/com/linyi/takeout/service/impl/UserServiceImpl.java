package com.linyi.takeout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linyi.takeout.pojo.User;
import com.linyi.takeout.service.UserService;
import com.linyi.takeout.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author linyi
* @description 针对表【user(用户信息)】的数据库操作Service实现
* @createDate 2022-07-20 14:28:45
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




