package cn.helloworld1999.myweb.controller;

import cn.helloworld1999.myweb.dto.ResponseResult;
import cn.helloworld1999.myweb.entity.Users;
import cn.helloworld1999.myweb.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // 使用@RestController替代@Controller
@RequestMapping("/users")
public class UsersController {

    @Autowired
    private UsersService usersService;

    @PostMapping("/login")
    public ResponseResult login(@RequestBody Users user) {
        if (user == null || user.getUsername() == null || user.getPassword() == null) {
            return ResponseResult.error(400, "用户名或密码不能为空");
        }

        Users authenticatedUser = usersService.login(user.getUsername(), user.getPassword());
        if (authenticatedUser != null) {
            return ResponseResult.success(authenticatedUser);
        } else {
            return ResponseResult.error(400, "用户名或密码错误或用户未激活");
        }
    }
}