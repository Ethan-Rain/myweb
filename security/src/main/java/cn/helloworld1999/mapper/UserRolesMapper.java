package cn.helloworld1999.mapper;

import cn.helloworld1999.domain.UserRoles;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 13278
* @description 针对表【user_roles】的数据库操作Mapper
* @createDate 2025-04-20 14:40:46
* @Entity cn/helloworld1999/.domain.UserRoles
*/
@Mapper
public interface UserRolesMapper {

    int deleteByPrimaryKey(Long id);

    int insert(UserRoles record);

    int insertSelective(UserRoles record);

    UserRoles selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserRoles record);

    int updateByPrimaryKey(UserRoles record);

}
