package cn.helloworld1999.common.mapper;

import cn.helloworld1999.common.domain.Users;

/**
 * UsersMapper扩展接口
 * 包含额外的查询方法
 */
public interface UsersMapperExt extends UsersMapper {
    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息
     */
    Users selectByUsername(String username);
}
