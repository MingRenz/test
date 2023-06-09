package com.mingren.myl.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mingren.myl.core.entity.Classification;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassificationMapper extends BaseMapper<Classification> {

    /**
     * 检查这个分类是否存在
     * @param name
     * @param shopName
     * @return
     */
    @Select("SELECT COUNT(*) FROM classification WHERE name = #{name} AND shop_name = #{shopName}")
    boolean checkNameIsCreated(@Param("name") String name, @Param("shopName") String shopName);

}