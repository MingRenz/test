package com.mingren.myl.core.controller;


import com.mingren.myl.core.entity.Result;
import com.mingren.myl.core.service.UserService;
import com.mingren.myl.core.util.ConstantVal;
import com.mingren.myl.core.util.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;

import com.google.code.kaptcha.impl.DefaultKaptcha;

@Controller
@Slf4j
@Api(tags = "登录界面(路由)")
public class IndexController {

    @Resource
    UserService userService;

    @Resource
    DefaultKaptcha defaultKaptcha;

    @Resource
    AuthenticationManager authenticationManager;

    /**
     * 进行验证码的请求，在网页哪里会产生一个随机id进行路径不同使得每次请求该路径都进行图片刷新
     * 并且把验证码存到session里面使得进行登录验证可以对比
     * 类似于/captcha.jpg?id=随机数
     * @param request
     * @param response
     * @throws IOException
     */
    @GetMapping("/captcha.jpg")
    @ResponseBody
    public void applyCheckCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        /*
        这行代码设置了HTTP响应头"Cache-Control"的值为"no-store，no-cache"，
        并将内容类型设置为"image/jpeg"。
        这表示服务器不希望浏览器缓存此图片，并且每次需要时都会请求该图片。
         */
        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setContentType("image/jpeg");

        //生成文字验证码
        String text = defaultKaptcha.createText();
        //生成图片验证码
        BufferedImage image = defaultKaptcha.createImage(text);
        //把文字验证码  保存到session
        request.getSession().setAttribute(ConstantVal.CHECK_CODE, text);
        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(image, "jpg", out);
    }

    /*
        下面进行手机号验证码登录功能，先不与实现


        @GetMapping(value = "/sms-code")
        @ResponseBody
        public Result getSmsCode(@RequestParam("telephone") String telephone){
            String code = String
                    .valueOf(new Random().nextInt(899999) + 100000);
            if(userService.setUserCodeForTelephone(telephone, code)){
                return Result.success("发送成功。");
            }
            return Result.error("发送失败。");
        }


        @PostMapping(value="/login-by-sms")
        @ResponseBody
        public Result loginBySms(HttpServletRequest request,
                                 @RequestParam("telephone") String telephone,
                                 @RequestParam("code") String code){
            try{
                User user = userService.checkTelephoneAndCode(telephone, code);
                //手机验证码登陆
                Authentication authenticate = new SmsAuthentication(user);
                SecurityContextHolder.getContext().setAuthentication(authenticate);
                HttpSession session = request.getSession();
                session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext()); // 这个非常重要，否则验证后将无法登陆
            }catch(UnmessageExcpetion e){
                return Result.error(e.getMessage());
            } catch (Exception e){
                log.error(e.getMessage());
                return Result.error("登陆失败!");
            }
            return Result.success("登陆成功！");
        }

    */

    /**
     * 进行表单登录验证
     * @param request：请求
     * @param username：账号
     * @param password：密码
     * @param kaptcha：验证码
     * @return：返回结果
     * UsernamePasswordAuthenticationToken：
     *      是Spring Security框架中的一个类，用于表示基于用户名和密码的身份验证请求
     *
     * authenticationManager.authenticate(usernamePasswordAuthenticationToken)：
     *      Spring Security 框架中的一个方法，用于对给定的身份验证凭据进行身份验证。
     *      它接受一个继承自AbstractAuthenticationToken类的身份验证凭据对象(例如 UsernamePasswordAuthenticationToken)，
     *      并返回一个表示经过身份验证的用户详细信息(UserDetails)的 Authentication 对象。
     *
     *      ps：如果身份验证失败，authenticate() 方法将抛出一个身份验证异常(AuthenticationException)
     *
     * SecurityContextHolder.getContext().setAuthentication(authenticate);：
     *      通常在身份验证成功后，将使用 该方法来将已认证的 Authentication 对象存储在安全上下文中。
     *      这样，在后续的请求中，可以通过调用
     *              SecurityContextHolder.getContext().getAuthentication()方法
     *      获取当前用户的认证对象，并基于此进行进一步的授权和访问控制
     *
     */

    @PostMapping("/login")
    @ResponseBody
    public Result login(HttpServletRequest request,
                        @RequestParam("username") String username,
                        @RequestParam("password") String password,
                        @RequestParam("kaptcha") String kaptcha){

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken=
                new UsernamePasswordAuthenticationToken(username,password);

        try {
            //获取存储在session里面的文字验证码
            String s = request.getSession().getAttribute(ConstantVal.CHECK_CODE).toString();
            if (s==null || !s.equals(kaptcha) || kaptcha.trim().isEmpty()){
                return Result.error("验证码错误！");
            }

            //进行安全验证
            Authentication authenticate = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

            SecurityContextHolder.getContext().setAuthentication(authenticate);
            HttpSession session = request.getSession();
            // 这个非常重要，否则验证后将无法登陆
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        }catch (BadCredentialsException e){
            return Result.error("密码错误!");
        }catch (Exception e){
            e.printStackTrace();
            return Result.error("登陆失败!");
        }
        log.info("登录成功{}",username);
        return Result.success("登陆成功！");
    }

    @GetMapping("/")
    public String index(){
        String userName= SecurityUtil.getUserName();


        if(userName.trim().isEmpty() || userName.equals("anonymousUser")){
            return "login";
        }

        return "frontManager";
    }

    @GetMapping("/front")
    @ApiOperation("前台管理")
    public String front(){
        String userName= SecurityUtil.getUserName();

        if(userName == null || userName.trim().isEmpty() || userName.equals("anonymousUser")){
            return "login";
        }

        return "frontManager";
    }

    @GetMapping("/userManager")
    @ApiOperation("用户管理")
    public String userManager(){
        String userName= SecurityUtil.getUserName();
        if(userName == null || userName.trim().isEmpty() || userName.equals("anonymousUser")){
            return "login";
        }

        return "userManager";
    }

    @GetMapping("/food")
    @ApiOperation("菜品管理")
    public String food(){
        String userName= SecurityUtil.getUserName();

        if(userName == null || userName.trim().isEmpty() || userName.equals("anonymousUser")){
            return "login";
        }

        return "foodManager";
    }

    @GetMapping("/reservation")
    @ApiOperation("预约管理")
    public String reservation(){
        String userName= SecurityUtil.getUserName();


        if(userName == null || userName.trim().isEmpty() || userName.equals("anonymousUser")){
            return "login";
        }
        return "reservationManager";
    }

    @GetMapping("/order")
    @ApiOperation("订单管理")
    public String order(){
        String userName= SecurityUtil.getUserName();


        if(userName == null || userName.trim().isEmpty() || userName.equals("anonymousUser")){
            return "login";
        }

        return "orderManager";
    }

    @GetMapping("/statistics")
    @ApiOperation("统计管理")
    public String statistics(){
        String userName= SecurityUtil.getUserName();
        if(userName == null || userName.trim().isEmpty() || userName.equals("anonymousUser")){
            return "login";
        }
        return "statisticsManager";
    }


    @GetMapping("/myinfo")
    @ApiOperation("我的信息")
    public String myinfo(){
        String userName= SecurityUtil.getUserName();


        if(userName == null || userName.trim().isEmpty() || userName.equals("anonymousUser")){
            return "login";
        }
        return "myinfo";
    }

    @GetMapping("/mykitchen")
    @ApiOperation("后厨管理")
    public String kitchen(){
        String userName= SecurityUtil.getUserName();

        if(userName == null || userName.trim().isEmpty() || userName.equals("anonymousUser")){
            return "login";
        }
        return "kitchenManager";
    }




}
