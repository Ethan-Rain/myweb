package cn.helloworld1999.security.service;

import cn.helloworld1999.common.domain.Users;
import cn.helloworld1999.security.dto.RegisterRequest;

public interface RegisterService {
    Users register(RegisterRequest request);
}
