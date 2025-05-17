package cn.helloworld1999.common.mapper;

import cn.helloworld1999.common.domain.Permissions;

/**
* @author yixinrui
* @description 针对表【permissions】的数据库操作Mapper
* @createDate 2025-05-17 22:33:05
* @Entity cn.helloworld1999.common.domain.Permissions
*/
public interface PermissionsMapper {

    int deleteByPrimaryKey(Long id);

    int insert(Permissions record);

    int insertSelective(Permissions record);

    Permissions selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Permissions record);

    int updateByPrimaryKey(Permissions record);

}
