package cn.helloworld1999.mapper;

import cn.helloworld1999.domain.Permissions;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 13278
* @description 针对表【permissions】的数据库操作Mapper
* @createDate 2025-04-20 14:40:46
* @Entity cn/helloworld1999/.domain.Permissions
*/
@Mapper
public interface PermissionsMapper {

    int deleteByPrimaryKey(Long id);

    int insert(Permissions record);

    int insertSelective(Permissions record);

    Permissions selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Permissions record);

    int updateByPrimaryKey(Permissions record);

}
