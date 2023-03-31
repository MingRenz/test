package com.mingren.myl.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mingren.myl.core.controller.UserController;
import com.mingren.myl.core.entity.DiningTable;
import com.mingren.myl.core.entity.Result;
import com.mingren.myl.core.entity.builder.TableBuilder;
import com.mingren.myl.core.exception.UnmessageException;
import com.mingren.myl.core.mapper.DiningTableMapper;
import com.mingren.myl.core.mapper.OrderMapper;
import com.mingren.myl.core.service.ReservationService;
import com.mingren.myl.core.service.TableService;
import com.mingren.myl.core.websocket.KitchenWebsocket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class TableServiceImpl implements TableService {

    @Resource
    private DiningTableMapper diningTableMapper;

    @Resource
    private UserController userController;

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private ReservationService reservationService;


    /**
     * 通过QueryWrapper类：条件查询构造器，
     * 查询到所有，与shop_name当前店名相等时，并且开放的table
     * 并以list形式返回列表
     * @return： 返回当前店可以使用的所有桌子
     */
    @Override
    public List<DiningTable> listForShow() {
        QueryWrapper<DiningTable> wrapper=new QueryWrapper<>();
        wrapper.eq("open",true);
        wrapper.eq("shop_name",userController.getShopName());
        return diningTableMapper.selectList(wrapper);
    }

    /**
     * 获取每张餐桌的预约信息以及每张餐桌的订单？
     * @return
     */
    @Override
    public List<DiningTable> listInfo() {
        List<DiningTable> tables=listForShow();
        for (DiningTable table : tables) {
            table.setOrder(orderMapper.selectById(table.getOrderId()));
            table.setReservationInfoList(reservationService.getReservationByTableId(table.getId()));
        }
        return tables;
    }

    /**
     * 获取所有没有在用餐的餐桌
     * @return
     */
    @Override
    public List<DiningTable> listForUsed() {
        QueryWrapper<DiningTable> wrapper = new QueryWrapper<>();
        wrapper.eq("used", true);
        wrapper.eq("shop_name", userController.getShopName());
        return diningTableMapper.selectList(wrapper);
    }


    /**
     * 获取所有餐桌
     * @return
     */
    @Override
    public List<DiningTable> list() {
        QueryWrapper<DiningTable> wrapper = new QueryWrapper<>();
        wrapper.eq("shop_name", userController.getShopName());
        return diningTableMapper.selectList(wrapper);
    }

    /**
     * 创建一个新餐桌
     * @param tableName：餐桌的名字
     * @param capacity：餐桌的容量
     * @param open：餐桌是否开放
     * @return： 返回插入的结果
     */
    @Override
    public Boolean createTable(String tableName, Integer capacity, Boolean open) {
        DiningTable table = TableBuilder.create().pushTableName(tableName)
                .pushCapacity(capacity).pushOpen(open).finish();
        table.setShopName(userController.getShopName());
        return diningTableMapper.insert(table) == 1;
    }

    /**
     * 根据id获取餐桌
     * @param id
     * @return
     */
    @Override
    public DiningTable getTableOfThrow(Integer id) {
        DiningTable table = diningTableMapper.selectById(id);
        if(table == null){
            throw new UnmessageException("餐桌不存在");
        }
        return table;
    }


    /**
     * 根据id修改餐桌的配置
     * @param id
     * @param tableName
     * @param capacity
     * @param open
     * @return
     */
    @Override
    public Boolean updateTable(Integer id, String tableName, Integer capacity, Boolean open) {
        DiningTable table = getTableOfThrow(id);
        table.setName(tableName);
        table.setCapacity(capacity);
        table.setOpen(open);
        return diningTableMapper.updateById(table) == 1;
    }

    /**
     * 删除餐桌
     * @param id
     * @return
     */
    @Override
    public Boolean deleteTable(Integer id) {
        DiningTable table = getTableOfThrow(id);
        if(table.getUsed()){
            throw new UnmessageException("餐桌正在被使用！");
        }
        return diningTableMapper.deleteById(id) == 1;
    }


    /**
     * 使得餐桌从未使用变为使用
     * @param tableId
     * @param orderId
     * @return
     */
    @Override
    public boolean tableUsed(Integer tableId, Integer orderId) {
        DiningTable table = getTableOfThrow(tableId);
        log.info("tableId:{} orderId:{} used:{}", tableId, orderId, table.getUsed());
        if(!table.getOpen()){
            throw new UnmessageException("餐桌未开放！");
        }
        if(table.getUsed()){
            throw new UnmessageException("餐桌正被使用。");
        }
        table.setOrderId(orderId);
        table.setUsed(true);
        return diningTableMapper.updateById(table) == 1;
    }

    /**
     * 使得餐桌从使用状态变为未使用（散桌）
     *      但此方法已经过时，下面有新方法
     * @Deprecated： 注解：代表该方法以及过时
     * @param tableId
     * @param orderId
     * @return
     */
    @Override
    @Deprecated
    public boolean tableUnused(Integer tableId, Integer orderId) {
        DiningTable table = getTableOfThrow(tableId);

        log.info("tableId:{} orderId:{} used:{}", tableId, orderId, table.getUsed());
        if(!table.getUsed()){
            throw new UnmessageException("餐桌未被使用。");
        }
        if(!table.getOrderId().equals(orderId)){
            throw new UnmessageException("订单号未对应。");
        }
        table.setOrderId(0);
        table.setUsed(false);
        return diningTableMapper.updateById(table) == 1;
    }

    @Override
    public boolean tableUnused(Integer tableId) {
        DiningTable table = getTableOfThrow(tableId);

        if(!table.getUsed()){
            throw new UnmessageException("餐桌未被使用。");
        }

        table.setOrderId(0);
        table.setUsed(false);

        KitchenWebsocket.sendMessage(userController.getShopName(), Result.flush());

        return diningTableMapper.updateById(table) == 1;
    }


}
