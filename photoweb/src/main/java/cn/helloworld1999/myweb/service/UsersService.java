package cn.helloworld1999.myweb.service;

import cn.helloworld1999.myweb.entity.Users;

public interface UsersService {
    Users login(String username, String password);
}
