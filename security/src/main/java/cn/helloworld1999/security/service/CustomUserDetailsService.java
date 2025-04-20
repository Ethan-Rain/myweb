package cn.helloworld1999.security.service;

import cn.helloworld1999.domain.Users;
import cn.helloworld1999.mapper.UsersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义用户详情服务
 * 实现Spring Security的UserDetailsService接口
 * 用于从数据库加载用户信息
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsersMapper usersMapper;

    // 这里可以注入角色相关的Mapper，以获取用户的角色信息
    // @Autowired
    // private RoleMapper roleMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 从数据库中查询用户
        Users user = usersMapper.selectByUsername(username);
        
        if (user == null) {
            throw new UsernameNotFoundException("用户名不存在: " + username);
        }
        
        // 检查用户是否启用
        boolean enabled = user.getEnabled() != null && user.getEnabled() == 1;
        
        // 这里应该从数据库加载用户的角色/权限
        // 为简化示例，这里手动创建权限列表
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        
        // TODO: 从数据库加载用户角色
        // 目前先添加一个基本角色，后续可以扩展为从数据库动态加载
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        
        // 创建Spring Security的UserDetails对象
        // 参数: 用户名，密码，是否启用，账号是否未过期，凭证是否未过期，账号是否未锁定，权限列表
        return new User(
                user.getUsername(),
                user.getPassword(),  // 这里应该是加密后的密码
                enabled,             // 是否启用
                true,    // 账号是否未过期
                true,    // 凭证是否未过期
                true,    // 账号是否未锁定
                authorities          // 权限列表
        );
    }
}
