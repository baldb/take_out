package com.linyi.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linyi.takeout.common.R;
import com.linyi.takeout.pojo.Employee;
import com.linyi.takeout.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

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
     *
     * @param employee
     * @param request
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(@RequestBody Employee employee,
                             HttpServletRequest request) {
        log.info("员工登陆，login{}", employee.toString());
        String username = employee.getUsername();
        String password = employee.getPassword();
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return R.error("账号密码格式有误，请重新输入～");
        }
        //将密码进行MD5加密
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //查询数据库
        LambdaQueryWrapper<Employee> employeeLQW = new LambdaQueryWrapper<>();
        employeeLQW.eq(Employee::getUsername, username);
        Employee employeeRes = employeeService.getOne(employeeLQW);
        //登陆失败的情况：用户名不存在，密码错误，状态异常
        if (employeeRes == null) {
            return R.error("用户名不存在～");
        }
        if (!employeeRes.getPassword().equals(password)) {
            return R.error("密码错误，请重新输入～");
        }
        if (employeeRes.getStatus() != 1) {
            return R.error("账户异常，请申诉～");
        }
        request.getSession().setAttribute("employee", employeeRes.getId());

        return R.success(employeeRes);
    }

    /**
     * 退出员工登陆系统
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R logout(HttpServletRequest request) {
        log.info("退出登陆");
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工信息
     *
     * @param request
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("新增员工，员工信息：{}", employee.toString());
        //设置初始密码123456，需要进行md5加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        //获得当前登录用户的id
        Long empId = (Long) request.getSession().getAttribute("employee");
        //设置创建人
        employee.setCreateUser(empId);
        //设置最后修改人
        employee.setUpdateUser(empId);
        //新增员工信息
        employeeService.save(employee);
        return R.success("新增员工成功");
    }


    /**
     *     http://localhost:8080/employee/page?page=1&pageSize=2
     * http://localhost:8080/employee/page?page=1&pageSize=2&name=%E5%BC%A0%E4%B8%89
     */
    @GetMapping("/page")
    public R<Page<Employee>> page(int page, int pageSize, String name) {
        log.info("page = {},pageSize = {},name = {}", page, pageSize, name);
        //构造分页构造器
        Page<Employee> pageInfo = new Page(page, pageSize);

        //根据姓名查询时的条件分页查询
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        //当name不为空时，条件才会生效
        wrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        //添加排序条件,根据更新时间进行降序排序
        wrapper.orderByDesc(Employee::getUpdateTime);
        employeeService.page(pageInfo, wrapper);

        return R.success(pageInfo);
    }

    /**
     * 修改员工状态信息
     * 修改其更新时间即可，不用修改创建时间
     * @param request
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {
        //特别注意18位的id会不会造成精度缺失的问题
        log.info("修改的员工信息：{}",employee.toString());
        //employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        Long id = (Long) request.getSession().getAttribute("employee");
        employee.setUpdateUser(id);
        employeeService.updateById(employee);

        return R.success("修改成功");
    }

}
