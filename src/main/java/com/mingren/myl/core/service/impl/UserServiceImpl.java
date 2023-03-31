package com.mingren.myl.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mingren.myl.core.controller.UserController;
import com.mingren.myl.core.entity.User;
import com.mingren.myl.core.entity.enums.EmployeeType;
import com.mingren.myl.core.exception.UnmessageException;
import com.mingren.myl.core.mapper.UserMapper;
import com.mingren.myl.core.service.UserService;
import com.mingren.myl.core.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Resource
    UserMapper userMapper;
    @Resource
    PasswordEncoder encoder;

    @Resource
    UserController userController;


    /**
     * 根据手机号？检查用户是否存在
     * @param telephone
     * @param code: 验证码
     * @return: 返回的用户
     */
    @Override
    public User checkTelephoneAndCode(String telephone, String code) {

        User user=userMapper.selectUserByTelephone(telephone);
        if (user==null){
            throw new UnmessageException("没有查询到对应的用户");
        }
        if (code.equals(user.getCode())){
            return user;
        }

        throw new UnmessageException("登录失败,验证码错误");
    }

    /**
     * 根据手机号查询用户
     * @param telephone
     * @return 返回的用户
     */
    @Override
    public User getUserByTelephone(String telephone) {
        User user=userMapper.selectUserByTelephone(telephone);
        if (user==null){
            throw new UnmessageException("没有查询到对应的用户");
        }

        return user;
    }

    /**
     *  根据手机号查到的用户，设置验证码，在进行更新表里面的验证码
     * @param telephone
     * @param code
     * @return
     */
    @Override
    public boolean setUserCodeForTelephone(String telephone, String code) {
        User user = userMapper.selectUserByTelephone(telephone);
        if (user==null){
            throw new UnmessageException("没有查询到对应的用户");
        }
        user.setCode(code);
        /*源代码，我这里尝试去除，看看 是否还能达到想要的效果
            try {
                zhenziSmsClient.send(telephone, "您的验证码:" + code);

            }catch(Exception e){
                throw new UnmessageException(e.getMessage());
            }
         */
        log.info("code={}",code);

        return userMapper.updateById(user)==1;
    }

    /**
     * 账号注册，如果可以进行账号注册，则进行加密处理
     * PasswordEncoder encoder
     *      encoder.encode(CharSequence var1)
     *      进行密码加密处理
     *
     * @param user
     * @return
     */
    @Override
    public boolean registeredUser(User user) {
        if (userMapper.selectUserByName(user.getUsername())==null){
            throw new UnmessageException("用户名已存在!");
        }
        if (userMapper.selectUserByTelephone(user.getTelephone())==null){
            throw new UnmessageException("手机号已经被注册!");
        }
        //进行密码加密处理
        user.setUsername(encoder.encode(user.getPassword()));
        return userMapper.insert(user)==1;
    }

    /**
     * 更新密码,先获取当前登录对象，根据老密码是否正确修改密码
     * PasswordEncoder encoder
     *      encoder.matches(CharSequence var1, String var2)
     *      校验原始密码和已加密的密码是否匹配，如果匹配则返回true，否则返回false
     * @param oldPassword
     * @param newPassword
     * @return
     */
    @Override
    public boolean updatePassword(String oldPassword, String newPassword) {
        User user=userMapper.selectUserByName(SecurityUtil.getUserName());
        /*源代码
        Preconditions.checkNotNull(user, "用户不存在!"); 需要引入依赖
        判断是否为空
         */
        if (user==null){
            throw new UnmessageException("用户不存在");
        }
        if (encoder.matches(oldPassword,user.getPassword())){
            user.setPassword(encoder.encode(newPassword));
            return userMapper.updateById(user)==1;
        }

        throw new UnmessageException("旧密码错误");
    }

    /**
     * 根据id获取用户
     * @param id
     * @return
     */
    @Override
    public User getUserById(Integer id) {
        return userMapper.selectById(id);
    }

    /**
     * 根据username获取用户
     * @param userName
     * @return
     */
    @Override
    public User getUserByName(String userName) {
        return userMapper.selectUserByName(userName);
    }


    /**
     * 通过 mybatisplus 进行分页用户管理
     *      QueryWrapper是 MyBatis-Plus 框架中的一个查询条件构造器，
     *      用于方便生成SQL查询语句的条件部分。它提供了一系列的链式方法来添加各种查询条件，
     *      例如等于、不等于、大于、小于、in、like、between等等。
     * 通过QueryWrapper对象，可以非常方便地构建复杂的查询条件。
     * @param current： 当前也
     * @param size：页大小
     * @param name：传的name
     * @return 返回分页结果
     */
    @Override
    public IPage<User> getPageByName(Integer current, Integer size, String name) {
        IPage<User> page = new Page<>(current,size);
        QueryWrapper<User> wrapper=new QueryWrapper<>();
        if (name!=null&&!name.trim().equals("")){
            wrapper.like("username",name);
        }
        wrapper.eq("shop_name",userController.getShopName());
        wrapper.orderByDesc("id");

        page=userMapper.selectPage(page,wrapper);
        return page;
    }


    /**
     * 根据id 删除员工
     * @param id
     * @return
     */

    @Override
    public boolean deleteUser(Integer id) {
        User user = userMapper.selectById(id);
        if (user==null){
            throw new UnmessageException("该员工不存在!");
        }
        if (user.getType().equals(EmployeeType.manager.getType())){
            throw new UnmessageException("不能删除店长!");
        }

        return userMapper.deleteById(id)==1;
    }

    /**
     * 根据id修改员工密码
     * @param id
     * @param password
     * @return
     */
    @Override
    public boolean updateEmployeePassword(Integer id, String password) {
        User user = userMapper.selectById(id);
        if (user==null){
            throw new UnmessageException("该员工不存在!");
        }
        if (user.getType().equals(EmployeeType.manager.getType())){
            throw new UnmessageException("不能修改店长密码!");
        }
        user.setPassword(encoder.encode(password));
        return userMapper.updateById(user)==1;
    }
}
