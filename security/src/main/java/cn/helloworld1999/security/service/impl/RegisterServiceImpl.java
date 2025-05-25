package cn.helloworld1999.security.service.impl;

import cn.helloworld1999.common.domain.Users;
import cn.helloworld1999.common.mapper.UsersMapperExt;
import cn.helloworld1999.security.dto.RegisterRequest;
import cn.helloworld1999.security.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;

@Service
public class RegisterServiceImpl implements RegisterService {

    @Autowired
    private UsersMapperExt usersMapper;


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Users register(RegisterRequest request) {
        // 验证密码是否匹配
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("密码不匹配");
        }

        // 检查用户名是否已存在
        if (usersMapper.selectByUsername(request.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 创建新用户
        Users user = new Users();
        user.setUsername(request.getUsername());
        // 使用密码加密器加密密码
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setCreated_at(new Date());
        user.setUpdated_at(new Date());

        // 保存用户
        usersMapper.insert(user);

        return user;
    }
}
