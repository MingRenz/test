package com.mingren.myl.core.entity.enums;

import com.mingren.myl.core.exception.UnmessageException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 预约状态
 */

@AllArgsConstructor
public enum ReservationMethod {
    WAIT("等待中", 1),
    SUCCESS("成功", 2),
    CANCEL("取消", 3);

    @Setter
    @Getter
    private String name;

    @Setter
    @Getter
    private Integer type;


    public static ReservationMethod valueOf(Integer type){
        switch(type){
            case 1:
                return WAIT;
            case 2:
                return SUCCESS;
            case 3:
                return CANCEL;
        }
        throw new UnmessageException("无法识别的预约方式。");

    }
}
