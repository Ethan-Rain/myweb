package cn.helloworld1999.mapper;


import cn.helloworld1999.domain.Roles;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 13278
* @description 针对表【roles】的数据库操作Mapper
* @createDate 2025-04-20 14:40:46
* @Entity cn/helloworld1999/.domain.Roles
*/
@Mapper
public interface RolesMapper {

    int deleteByPrimaryKey(Long id);

    int insert(Roles record);

    int insertSelective(Roles record);

    Roles selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Roles record);

    int updateByPrimaryKey(Roles record);

}
