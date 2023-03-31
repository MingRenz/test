package com.mingren.myl.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mingren.myl.core.controller.UserController;
import com.mingren.myl.core.entity.DiningTable;
import com.mingren.myl.core.entity.ReservationInfo;
import com.mingren.myl.core.entity.builder.ReservationBuilder;
import com.mingren.myl.core.entity.enums.ReservationMethod;
import com.mingren.myl.core.exception.UnmessageException;
import com.mingren.myl.core.mapper.DiningTableMapper;
import com.mingren.myl.core.mapper.ReservationInfoMapper;
import com.mingren.myl.core.service.ReservationService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {

    @Resource
    private ReservationInfoMapper reservationInfoMapper;

    @Resource
    private DiningTableMapper diningTableMapper;

    @Resource
    private UserController userController;


    /**
     * 分页操作
     * @param current
     * @param size
     * @return
     */
    @Override
    public IPage<ReservationInfo> getPage(Integer current, Integer size) {
        IPage<ReservationInfo> page = new Page<>(current, size);
        QueryWrapper<ReservationInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("shop_name", userController.getShopName());
        wrapper.orderByDesc("id");
        return reservationInfoMapper.selectPage(page, wrapper);
    }

    /**
     * 根据id查询是否有预约，如果有则返回该预约
     * @param id
     * @return
     */
    @Override
    public ReservationInfo getInfoOrThrow(Integer id) {
        ReservationInfo info = reservationInfoMapper.selectById(id);
        if (info == null) {
            throw new UnmessageException("没有这个预约id");
        }
        return info;
    }

    /**
     * 使得这个预约状态从未完成变为已经完成
     * @param id
     * @return
     */
    @Override
    public Boolean successInfo(Integer id) {
        ReservationInfo info = getInfoOrThrow(id);
        if (info.getReservationMethod() == ReservationMethod.CANCEL) {
            throw new UnmessageException("预约已取消!");
        }
        if (info.getReservationMethod() == ReservationMethod.SUCCESS) {
            throw new UnmessageException("预约已完成!");
        }
        info.setReservationMethod(ReservationMethod.SUCCESS);
        return reservationInfoMapper.updateById(info) == 1;
    }

    /**
     * 取消预约
     * @param id
     * @return
     */
    @Override
    public Boolean cancelInfo(Integer id) {
        ReservationInfo info = getInfoOrThrow(id);
        if (info.getReservationMethod() == ReservationMethod.CANCEL) {
            throw new UnmessageException("预约已取消!");
        }
        if (info.getReservationMethod() == ReservationMethod.SUCCESS) {
            throw new UnmessageException("预约已完成!");
        }
        info.setReservationMethod(ReservationMethod.CANCEL);
        return reservationInfoMapper.updateById(info) == 1;
    }

    /**
     * 删除预约id
     * @param id
     * @return
     */
    @Override
    public Boolean deleteInfo(Integer id) {
        return reservationInfoMapper.deleteById(id) == 1;
    }


    /**
     * 创建预约
     * @param clientName
     * @param clientTel
     * @param tableId
     * @param numberOfPeople
     * @param startTime
     * @param endTime
     * @return
     */
    @Override
    public Boolean createInfo(String clientName, String clientTel, Integer tableId, Integer numberOfPeople, LocalDateTime startTime, LocalDateTime endTime) {

        if(startTime == null || endTime == null){
            throw new UnmessageException("预约时间不能为空。");
        }


        ReservationInfo info = ReservationBuilder.create()
                .pushClientName(clientName).pushClientTel(clientTel)
                .pushStartDate(startTime).pushEndDate(endTime).pushNumberOfPeople(numberOfPeople)
                .pushTableId(tableId).finish();
        info.setShopName(userController.getShopName());

        List<ReservationInfo> infoList = reservationInfoMapper.getInfoByTableIdForReservation(tableId);


        for (ReservationInfo temp : infoList) {

            if(temp.getStartDate().isEqual(info.getStartDate())){
                throw new UnmessageException("时间段已预定");
            }

            if(temp.getEndDate().isEqual(info.getEndDate())){
                throw new UnmessageException("时间段已预定");
            }

            //检查对应tableId在时间段上有没有预约的订单
            if (temp.getEndDate().isAfter(info.getEndDate())
                    && temp.getStartDate().isBefore(info.getStartDate())) {
                throw new UnmessageException("时间段已预定");
            }

            if (temp.getEndDate().isAfter(info.getEndDate())
                    && temp.getStartDate().isBefore(info.getEndDate())) {
                throw new UnmessageException("时间段已预定");
            }

            if (temp.getStartDate().isAfter(info.getStartDate())
                    && temp.getStartDate().isBefore(info.getEndDate())) {
                throw new UnmessageException("时间段已预定");
            }
        }

        return reservationInfoMapper.insert(info) == 1;
    }

    /**
     * 根据预约电话查询预约
     * @param tel
     * @return
     */
    @Override
    public List<ReservationInfo> searchByTel(String tel) {
        QueryWrapper<ReservationInfo> wrapper = new QueryWrapper<>();
        wrapper.like("client_tel", tel);
        return reservationInfoMapper.selectList(wrapper);
    }

    /**
     * 没看懂
     * @param number
     * @param startTime
     * @param endTime
     * @return
     */
    @Override
    public List<DiningTable> getRecommend(Integer number, LocalDateTime startTime, LocalDateTime endTime) {
        List<DiningTable> recommendTables = new ArrayList<>();
        List<DiningTable> tables = diningTableMapper.getRecommendTableByNumberOfPeople(number, userController.getShopName());
        for (DiningTable table : tables) {
            List<ReservationInfo> infoList = reservationInfoMapper.getInfoByTableIdForReservation(table.getId());
            boolean canAdd = true;

            for (ReservationInfo temp : infoList) {
                //检查对应tableId在时间段上有没有预约的订单
                if(temp.getStartDate().isEqual(startTime)){
                    canAdd = false;
                }else if(temp.getEndDate().isEqual(endTime)){
                    canAdd = false;
                } else if (temp.getEndDate().isAfter(endTime)
                        && temp.getStartDate().isBefore(startTime)) {
                    canAdd = false;
                } else if (temp.getEndDate().isAfter(endTime)
                        && temp.getStartDate().isBefore(endTime)) {
                    canAdd = false;
                } else if (temp.getStartDate().isAfter(startTime)
                        && temp.getStartDate().isBefore(endTime)) {
                    canAdd = false;
                }
            }

            if (canAdd) {
                recommendTables.add(table);
            }
        }
        return recommendTables;
    }



    /**
     * 查询对应餐桌下未完成预约的情况
     * @param tableId
     * @return
     */
    @Override
    public List<ReservationInfo> getReservationByTableId(Integer tableId) {
        List<ReservationInfo> list=reservationInfoMapper.getInfoByTableIdForReservation(tableId);
        return list;
    }

    /**
     * 根据餐桌id检查该餐桌是否有预约
     * @param tableId
     * @param time
     * @return
     */
    @Override
    public Boolean checkHaveReservation(Integer tableId, LocalDateTime time) {
        QueryWrapper<ReservationInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("table_id", tableId).ge("start_date", time)
                .le("end_date", time);
        return reservationInfoMapper.selectCount(wrapper) > 0;

    }
}
