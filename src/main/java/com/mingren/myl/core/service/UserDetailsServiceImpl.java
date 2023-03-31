package com.mingren.myl.core.service;

import com.mingren.myl.core.entity.User;
import com.mingren.myl.core.mapper.UserMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Resource
    UserMapper userMapper;


    /**
     *
     * @param s：传入的名字，账号
     * @return  如果找到该账户则返回user
     * @throws UsernameNotFoundException    当名字不存在是抛出不存在异常
     */
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userMapper.selectUserByName(s);
        if (user==null){
            throw new UsernameNotFoundException("账号不存在");
        }
        //用于返回一个空的不可变列表（List）实例。也就是说，它返回一个长度为 0 的、不能被修改的 List 对象。
        // 这个方法可以方便地创建一个不需要包含任何元素的列表，并且由于返回的列表是不可修改的，因此可以避免程序中出现意外修改空列表的情况。
        user.setAuthorities(Collections.emptyList());
        return user;
    }
}
