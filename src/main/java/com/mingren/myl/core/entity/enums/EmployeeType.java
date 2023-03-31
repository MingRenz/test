package com.mingren.myl.core.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public enum EmployeeType {

    manager("店长",0),
    chef("厨师",1),
    waiter("服务员",2)
    ;

    @Setter
    @Getter
    private String name;

    @Setter
    @Getter
    private Integer type;

}
