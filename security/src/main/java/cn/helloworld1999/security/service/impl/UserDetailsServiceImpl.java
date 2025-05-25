package cn.helloworld1999.security.service.impl;

import cn.helloworld1999.common.domain.Users;
import cn.helloworld1999.common.domain.UserRoles;
import cn.helloworld1999.common.domain.RolePermissions;
import cn.helloworld1999.common.domain.Permissions;
import cn.helloworld1999.common.mapper.UsersMapperExt;
import cn.helloworld1999.common.mapper.UserRolesMapperExt;
import cn.helloworld1999.common.mapper.RolePermissionsMapperExt;
import cn.helloworld1999.common.mapper.PermissionsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户详情服务实现类
 * 用于从数据库加载用户信息和权限信息
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsersMapperExt usersMapper;

    @Autowired
    private UserRolesMapperExt userRolesMapper;

    @Autowired
    private RolePermissionsMapperExt rolePermissionsMapper;

    @Autowired
    private PermissionsMapper permissionsMapper;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. 根据用户名查询用户信息
        Users user = usersMapper.selectByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        // 2. 查询用户的角色
        List<UserRoles> userRoles = userRolesMapper.selectByUserId(user.getId());
        
        // 3. 查询角色对应的权限
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (userRoles != null && !userRoles.isEmpty()) {
            for (UserRoles userRole : userRoles) {
                // 获取角色的权限
                List<RolePermissions> rolePermissions = rolePermissionsMapper.selectByRoleId(userRole.getRole_id());
                if (rolePermissions != null) {
                    for (RolePermissions rolePermission : rolePermissions) {
                        Permissions permission = permissionsMapper.selectByPrimaryKey(rolePermission.getPermission_id());
                        if (permission != null) {
                            authorities.add(new SimpleGrantedAuthority(permission.getPermission_name()));
                        }
                    }
                }
            }
        }

        // 4. 返回用户详情
        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            authorities
        );
    }
}
