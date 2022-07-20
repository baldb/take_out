package com.linyi.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.linyi.takeout.common.R;
import com.linyi.takeout.pojo.User;
import com.linyi.takeout.service.UserService;
import com.linyi.takeout.utils.HanZiUtil;
import com.linyi.takeout.utils.SMSUtils;
import com.linyi.takeout.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 发送手机短信验证码
     *
     * @param user
     * @return
     */
    @PostMapping("sendMsg")
    public R<String> sendPhone(@RequestBody User user, HttpSession session) {

        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)) {
            //生成随机的4位验证码
            String code = ValidateCodeUtils.generateValidateCode(6).toString();
           log.info("验证码code: {}", code);
            //SMSUtils.sendPhone(phone, code);
            //需要将生成的验证码保存到Session, 通过手机号查验证码
            session.setAttribute("phone", code);

            //redisTemplate.opsForValue().set(phone, code, 5, TimeUnit.MINUTES);

            return R.success("发送短信成功");
        }

        return R.error("发送短信失败");
    }


    /**
     * 用户登录
     *
     * @param map
     * @param session
     * @return
     */
    @PostMapping("login")
    public R<User> login(@RequestBody Map map, HttpSession session) {
//            获取用户信息
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();

        //获取该手机的验证码
        Object codeInSession = session.getAttribute("phone");
//        Object codeInSession = redisTemplate.opsForValue().get(phone);

        //从Session中获取保存的验证码
        if (codeInSession != null && code.equals(codeInSession)) {
//            登录成功,判断是否为新用户
            QueryWrapper<User> wrapper = new QueryWrapper<>();
            wrapper.eq("phone", phone);
            User user = userService.getOne(wrapper);
            if (user == null) {
//                新用户
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                user.setName(HanZiUtil.getRandomHanZi(3));
                userService.save(user);
            }
            session.setAttribute("user", user.getId());

//            登录成功
//            redisTemplate.delete(phone);
            //移除验证码
            //session.removeAttribute("phone");
            return R.success(user);
        }

        log.info("验证码错误，请重新输入～");
        return R.error("登录失败");
    }
}