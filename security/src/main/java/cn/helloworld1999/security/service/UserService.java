package cn.helloworld1999.security.service;

import cn.helloworld1999.domain.Users;
import cn.helloworld1999.mapper.UsersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 用户服务类
 * 负责用户相关的业务逻辑
 */
@Service
public class UserService {

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 根据用户名查询用户
     */
    public Users getUserByUsername(String username) {
        return usersMapper.selectByUsername(username);
    }

    /**
     * 根据ID查询用户
     */
    public Users getUserById(Long id) {
        return usersMapper.selectByPrimaryKey(id);
    }

    /**
     * 注册新用户
     */
    public boolean registerUser(Users user) {
        // 检查用户名是否已存在
        if (usersMapper.selectByUsername(user.getUsername()) != null) {
            return false;
        }

        // 对密码进行加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // 设置默认为启用状态
        user.setEnabled(1);
        
        // 设置创建和更新时间
        Date now = new Date();
        user.setCreated_at(now);
        user.setUpdated_at(now);
        
        // 保存用户到数据库
        return usersMapper.insert(user) > 0;
    }

    /**
     * 更新用户信息
     */
    public boolean updateUser(Users user) {
        // 更新时间
        user.setUpdated_at(new Date());
        
        // 如果密码不为空，则对密码进行加密
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        
        // 更新用户信息
        return usersMapper.updateByPrimaryKeySelective(user) > 0;
    }

    /**
     * 禁用/启用用户
     */
    public boolean toggleUserStatus(Long userId, boolean enable) {
        Users user = usersMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return false;
        }
        
        user.setEnabled(enable ? 1 : 0);
        user.setUpdated_at(new Date());
        
        return usersMapper.updateByPrimaryKeySelective(user) > 0;
    }
}
