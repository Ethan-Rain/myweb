package cn.helloworld1999.common.mapper;

import cn.helloworld1999.common.domain.UserRoles;

/**
* @author yixinrui
* @description 针对表【user_roles】的数据库操作Mapper
* @createDate 2025-05-17 22:33:05
* @Entity cn.helloworld1999.common.domain.UserRoles
*/
public interface UserRolesMapper {

    int deleteByPrimaryKey(Long id);

    int insert(UserRoles record);

    int insertSelective(UserRoles record);

    UserRoles selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserRoles record);

    int updateByPrimaryKey(UserRoles record);

}
