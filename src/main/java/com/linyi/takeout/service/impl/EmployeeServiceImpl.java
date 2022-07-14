package com.linyi.takeout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linyi.takeout.pojo.Employee;
import com.linyi.takeout.service.EmployeeService;
import com.linyi.takeout.mapper.EmployeeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
* @author linyi
* @description 针对表【employee(员工信息)】的数据库操作Service实现
* @createDate 2022-07-12 10:34:08
*/
@Service
@Slf4j
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee>
    implements EmployeeService{

}




