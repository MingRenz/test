package com.mingren.myl.core.controller;

import com.mingren.myl.core.entity.Order;
import com.mingren.myl.core.entity.Result;
import com.mingren.myl.core.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Api(tags = "订单管理")
@RequestMapping("/order")
public class OrderController {

    @Resource
    private OrderService orderService;

    @ApiOperation("获取所有正在使用的订单")
    @GetMapping("/used-order")
    public Result getUsedOrder(){
        List<Order> list = orderService.getUsedOrder();
        return Result.success("成功。", list);
    }

}
