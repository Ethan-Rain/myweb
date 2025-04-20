package cn.helloworld1999.mapper;

import cn.helloworld1999.domain.Users;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 13278
* @description 针对表【users】的数据库操作Mapper
* @createDate 2025-04-20 14:40:46
* @Entity cn/helloworld1999/.domain.Users
*/
@Mapper
public interface UsersMapper {

    int deleteByPrimaryKey(Long id);

    int insert(Users record);

    int insertSelective(Users record);

    Users selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Users record);

    int updateByPrimaryKey(Users record);
    
    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户对象
     */
    Users selectByUsername(String username);
}
