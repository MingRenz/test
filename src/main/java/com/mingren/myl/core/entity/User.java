package com.mingren.myl.core.entity;


import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * @Data:   lombok插件
 *          1、@Data可以为类提供读写功能，从而不用写get、set方法。
 * 		    2、他还会为类提供 equals()、hashCode()、toString() 方法。
 * @ToString: lombok插件
 *          1、作用于类，覆盖默认的 toString() 方法，
 *                  输出格式：ClassName(fieldName=fieleValue, fieldName1=fieleValue)
 * @NoArgsConstructor:  lombok插件
 *          1、在类上使用，这个注解可以生成无参构造方法
 * @TableName("user"):
 *          1、在类上使用 用来将指定的数据库表和 JavaBean 进行映射
 */

@Data
@ToString
@TableName("user")
@NoArgsConstructor
public class User implements UserDetails {

    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户名称
     */
    private String username;

    /**
     * 密码
     */
    @JSONField(serialize = false)
    private String password;

    /**
     * 默认电话
     */
    private String telephone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 验证码
     */
    private String code;

    /**
     * 店名
     */
    private String shopName;


    /**
     * 类型 0=管理员 1=厨师 2=服务员
     * EmployeeType
     */
    private Integer type;

    /**
     * 权限
     */
    @TableField(exist = false)
    private List<GrantedAuthority> authorities;


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
