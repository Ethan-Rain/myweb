package cn.helloworld1999.common.mapper;

import cn.helloworld1999.common.domain.Users;

/**
* @author yixinrui
* @description 针对表【users】的数据库操作Mapper
* @createDate 2025-05-17 22:33:05
* @Entity cn.helloworld1999.common.domain.Users
*/
public interface UsersMapper {

    int deleteByPrimaryKey(Long id);

    int insert(Users record);

    int insertSelective(Users record);

    Users selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Users record);

    int updateByPrimaryKey(Users record);

    Users selectByUsername(String username);
}
