package cn.helloworld1999.common.mapper;

import cn.helloworld1999.common.domain.UserRoles;
import java.util.List;

/**
 * UserRolesMapper扩展接口
 * 包含额外的查询方法
 */
public interface UserRolesMapperExt extends UserRolesMapper {
    /**
     * 根据用户ID查询用户角色
     * @param userId 用户ID
     * @return 用户角色列表
     */
    List<UserRoles> selectByUserId(Long userId);
}
