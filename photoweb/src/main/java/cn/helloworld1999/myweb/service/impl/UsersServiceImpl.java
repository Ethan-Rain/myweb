package cn.helloworld1999.myweb.service.impl;

import cn.helloworld1999.myweb.entity.Users;
import cn.helloworld1999.myweb.mapper.UsersMapper;
import cn.helloworld1999.myweb.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsersServiceImpl implements UsersService {
    @Autowired
    private UsersMapper usersMapper;

    @Override
    public Users login(String username, String password) {
        Users user = usersMapper.login(username, password);
        if (user != null) {
            return user;
        } else {
            return null;
        }
    }
}
