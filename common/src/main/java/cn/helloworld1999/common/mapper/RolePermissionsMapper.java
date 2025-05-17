package cn.helloworld1999.common.mapper;

import cn.helloworld1999.common.domain.RolePermissions;

/**
* @author yixinrui
* @description 针对表【role_permissions】的数据库操作Mapper
* @createDate 2025-05-17 22:33:05
* @Entity cn.helloworld1999.common.domain.RolePermissions
*/
public interface RolePermissionsMapper {

    int deleteByPrimaryKey(Long id);

    int insert(RolePermissions record);

    int insertSelective(RolePermissions record);

    RolePermissions selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RolePermissions record);

    int updateByPrimaryKey(RolePermissions record);

}
