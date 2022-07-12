package com.linyi.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.linyi.takeout.common.R;
import com.linyi.takeout.pojo.Employee;
import com.linyi.takeout.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.apache.commons.lang.StringUtils;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author linyi
 * @date 2022/7/12
 * 1.0
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登陆功能
     * @param employee
     * @param request
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(@RequestBody Employee employee,
                             HttpServletRequest request){
        String username = employee.getUsername();
        String password = employee.getPassword();
        if(StringUtils.isEmpty(username) || StringUtils.isEmpty(password)){
            return R.error("账号密码格式有误，请重新输入～");
        }
        //将密码进行MD5加密
        password= DigestUtils.md5DigestAsHex(password.getBytes());
        //查询数据库
        LambdaQueryWrapper<Employee> employeeLQW = new LambdaQueryWrapper<>();
        employeeLQW.eq(Employee::getUsername,username);
        Employee employeeRes = employeeService.getOne(employeeLQW);
        //登陆失败的情况：用户名不存在，密码错误，状态异常
        if(employeeRes==null){
            return R.error("用户名不存在～");
        }
        if(!employeeRes.getPassword().equals(password)){
            return R.error("密码错误，请重新输入～");
        }
        if(employeeRes.getStatus()!=1){
            return R.error("账户异常，请申诉～");
        }
        request.getSession().setAttribute("employee",employeeRes.getId());

        return R.success(employeeRes);
    }

    /**
     * 退出员工登陆系统
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }
}
