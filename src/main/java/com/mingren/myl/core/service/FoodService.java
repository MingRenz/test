package com.mingren.myl.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mingren.myl.core.entity.Food;

import java.util.List;

public interface FoodService {

    List<Food> getFoodsByClassId(Integer cid);

    Food getFoodById(Integer foodID);

    boolean addFood(Food food);

    boolean deleteFoodById(Integer foodId);

    boolean updateFoodById(Food food);

    IPage<Food> getPageBySearch(Integer current,
                                Integer size,
                                String name,
                                Integer classificationId);

    boolean deleteClassForFood(Integer foodId, Integer classId);

    boolean addClassForFood(Integer foodId, Integer classId);

    Food getFoodOrThrow(Integer id);
}
