package cn.helloworld1999.common.mapper;

import cn.helloworld1999.common.domain.RolePermissions;
import java.util.List;

/**
 * RolePermissionsMapper扩展接口
 * 包含额外的查询方法
 */
public interface RolePermissionsMapperExt extends RolePermissionsMapper {
    /**
     * 根据角色ID查询角色权限
     * @param roleId 角色ID
     * @return 角色权限列表
     */
    List<RolePermissions> selectByRoleId(Long roleId);
}
