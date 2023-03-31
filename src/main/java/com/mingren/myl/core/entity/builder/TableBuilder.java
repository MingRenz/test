package com.mingren.myl.core.entity.builder;

import com.mingren.myl.core.entity.DiningTable;

/**
 * 对餐桌进行管理
 */
public class TableBuilder {
    public static TableBuilder create(){
        return new TableBuilder();
    }

    private DiningTable diningTable;

    private TableBuilder(){
        this.diningTable = new DiningTable();
    }

    public TableBuilder pushTableName(String tableName){
        diningTable.setName(tableName);
        return this;
    }

    public TableBuilder pushCapacity(Integer capacity){
        diningTable.setCapacity(capacity);
        return this;
    }

    public TableBuilder pushOpen(Boolean open){
        diningTable.setOpen(open);
        return this;
    }

    public DiningTable finish(){
        diningTable.setUsed(false);
        return diningTable;
    }

}
