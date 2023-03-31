package com.mingren.myl.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mingren.myl.core.entity.ReservationInfo;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationInfoMapper extends BaseMapper<ReservationInfo> {

    /**
     * 查询对应餐桌下未完成预约的情况
     * @param tableId
     * @return
     */
    @Select("SELECT * FROM reservation_info WHERE table_id = #{tableId} AND status = 1")
    List<ReservationInfo> getInfoByTableIdForReservation(
            Integer tableId
    );

}
