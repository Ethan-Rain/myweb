package cn.helloworld1999.mapper;

import cn.helloworld1999.domain.RolePermissions;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 13278
* @description 针对表【role_permissions】的数据库操作Mapper
* @createDate 2025-04-20 14:40:46
* @Entity cn/helloworld1999/.domain.RolePermissions
*/
@Mapper
public interface RolePermissionsMapper {

    int deleteByPrimaryKey(Long id);

    int insert(RolePermissions record);

    int insertSelective(RolePermissions record);

    RolePermissions selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RolePermissions record);

    int updateByPrimaryKey(RolePermissions record);

}
