package com.mingren.myl.core.entity.enums;

import com.mingren.myl.core.exception.UnmessageException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public enum PaymentMethod {
    ALIPAY("支付宝", 1),
    WEIXIN("微信", 2),
    CASH("现金", 3);

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private Integer type;


    public static PaymentMethod valueOf(Integer type){
        switch(type){
            case 1:
                return ALIPAY;
            case 2:
                return WEIXIN;
            case 3:
                return CASH;
        }
        throw new UnmessageException("无法识别的支付方式。");
    }
}
