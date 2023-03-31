package com.mingren.myl.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mingren.myl.core.entity.User;

public interface UserService {
    boolean registeredUser(User user);

    boolean updatePassword(String oldPassword, String newPassword);

    User getUserById(Integer id);

    User getUserByName(String userName);

    User getUserByTelephone(String telephone);

    boolean setUserCodeForTelephone(String telephone, String code);

    IPage<User> getPageByName(Integer current, Integer size, String name);

    User checkTelephoneAndCode(String telephone, String code);

    boolean deleteUser(Integer id);

    boolean updateEmployeePassword(Integer id, String password);
}
