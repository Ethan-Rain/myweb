package cn.helloworld1999.common.mapper;

import cn.helloworld1999.common.domain.Roles;

/**
* @author yixinrui
* @description 针对表【roles】的数据库操作Mapper
* @createDate 2025-05-17 22:33:05
* @Entity cn.helloworld1999.common.domain.Roles
*/
public interface RolesMapper {

    int deleteByPrimaryKey(Long id);

    int insert(Roles record);

    int insertSelective(Roles record);

    Roles selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Roles record);

    int updateByPrimaryKey(Roles record);

}
