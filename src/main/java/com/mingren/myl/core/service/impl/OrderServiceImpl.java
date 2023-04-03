package com.mingren.myl.core.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mingren.myl.core.entity.DiningTable;
import com.mingren.myl.core.entity.Order;
import com.mingren.myl.core.entity.enums.PaymentMethod;
import com.mingren.myl.core.service.OrderService;
import com.mingren.myl.core.service.TableService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private TableService tableService;

    /**
     * 获取所有正在使用的订单
     * @return
     */
    @Override
    public List<Order> getUsedOrder() {
        List<DiningTable> tables = tableService.listForUsed();\
        List<Order> orders = new ArrayList<>();

        for(DiningTable table : tables){
            Order order = getOrderOrThrow(table.getOrderId());
            order.setFoods(orderFoodMapper.selectOrderFoodListByOrderId(order.getId()));
            order.setDiningTable(table);
            orders.add(order);
        }
        return orders;

    }

    @Override
    public List<Order> getNotSuccessOrderWithFoods() {
        return null;
    }

    @Override
    public List<Order> getNotPayOrder() {
        return null;
    }

    @Override
    public IPage<Order> pageOrder(Integer current, Integer size, LocalDateTime startTime, LocalDateTime endTime, String clientName, Integer tableId) {
        return null;
    }

    @Override
    public boolean createOrder(Integer tableId, Integer numberOfPeople, String name, String remarks) {
        return false;
    }

    @Override
    public boolean createOrderWithFoods(Integer tableId, Integer numberOfPeople, String name, String remarks, String foods) {
        return false;
    }

    @Override
    public boolean updateOrder(Integer id, Integer tableId, Integer numberOfPeople, String name, String remarks) {
        return false;
    }

    @Override
    public Order getOrderById(Integer id) {
        return null;
    }

    @Override
    public boolean cancelOrder(Integer id) {
        return false;
    }

    @Override
    public boolean successOrder(Integer orderId, BigDecimal price, PaymentMethod paymentMethod) {
        return false;
    }

    @Override
    public boolean addFoodForOrder(Integer orderId, Integer foodId) {
        return false;
    }

    @Override
    public boolean deleteFoodForOrder(Integer orderFoodId) {
        return false;
    }

    @Override
    public boolean modifyFoodAmountForOrder(Integer orderFoodId, Integer amount) {
        return false;
    }

    @Override
    public boolean completeFoodForOrder(Integer orderFoodId) {
        return false;
    }

    @Override
    public boolean returnFoodForOrder(Integer orderFoodId, String msg) {
        return false;
    }

    @Override
    public Order getOrderOrThrow(Integer id) {
        return null;
    }

    @Override
    public BigDecimal getActualPrice(Integer orderId) {
        return null;
    }

    @Override
    public void urgeOrder(Integer orderId) {

    }
}
