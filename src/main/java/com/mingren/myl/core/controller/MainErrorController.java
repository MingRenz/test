package com.mingren.myl.core.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;


/**
 * 控制页面访问出错后的页面
 */

@Controller
public class MainErrorController implements ErrorController {


    @RequestMapping("/error")
    public String handleError(HttpServletRequest request){
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if(statusCode == 404){
            return "/404";
        }else if(statusCode == 403){
            return "/login";
        }else{
            return "/500";
        }

    }

    @Override
    public String getErrorPath() {
        return null;
    }
}
