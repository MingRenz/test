package com.mingren.myl.core.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mingren.myl.core.controller.UserController;
import com.mingren.myl.core.entity.Food;
import com.mingren.myl.core.exception.UnmessageException;
import com.mingren.myl.core.mapper.ClassificationMapper;
import com.mingren.myl.core.mapper.FoodClassMapper;
import com.mingren.myl.core.mapper.FoodMapper;
import com.mingren.myl.core.service.FoodService;
import com.mingren.myl.core.util.SecurityUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@Service
public class FoodServiceImpl implements FoodService {

    @Resource
    private FoodMapper foodMapper;

    @Resource
    private UserController userController;

    @Resource
    ClassificationMapper classificationMapper;

    @Resource
    FoodClassMapper foodClassMapper;


    /**
     * 根据菜品id获取所有属于该菜品的food
     * @param cid
     * @return
     */
    @Override
    public List<Food> getFoodsByClassId(Integer cid) {
        List<Food> foods = foodMapper.selectFoodsByCid(cid);
        return foods;
    }

    /**
     * 根据food的id获取单个food
     * @param foodID
     * @return
     */
    @Override
    public Food getFoodById(Integer foodID) {
        Food food = foodMapper.selectOneById(foodID);
        if(food == null){
            throw new UnmessageException("没有这个菜品");
        }
        return food;
    }

    /**
     * 添加一个菜
     * @param food
     * @return
     */
    @Override
    public boolean addFood(Food food) {
        if(foodMapper.checkFoodNameCreated(food.getFoodName(), SecurityUtil.getShopName())){
            throw new UnmessageException("菜品名称已经存在！");
        }
        if(food.getDiscount().equals(BigDecimal.ZERO)){
            food.setDiscount(BigDecimal.ONE);
        }
        if(food.getPrice() == null){
            food.setPrice(BigDecimal.ZERO);
        }
        food.setShopName(userController.getShopName());
        return foodMapper.insert(food) == 1;
    }

    /**
     * 删除一个菜
     *      同时删除映射表中的关系
     * @param foodId
     * @return
     */
    @Override
    public boolean deleteFoodById(Integer foodId) {
        if(foodMapper.deleteById(foodId) == 1){
            //删除分类对应关系
            foodClassMapper.deleteFoodClassByFoodId(foodId);
            return true;
        }
        return false;
    }

    /**
     * 更新food信息
     * @param food
     * @return
     */
    @Override
    public boolean updateFoodById(Food food) {
        return foodMapper.updateById(food) == 1;
    }

    /**
     * 检查菜品和分类是否存在
     * @param foodId
     * @param classId
     */
    private void checkClassAndFoodCreated(Integer foodId, Integer classId){
        if(foodMapper.selectById(foodId) == null){
            throw new UnmessageException("菜品id，不存在");
        }
        if(classificationMapper.selectById(classId) == null){
            throw new UnmessageException("分类id，不存在");
        }
    }

    /**
     * 添加一个菜品分类
     * @param foodId
     * @param classId
     * @return
     */
    @Override
    public boolean addClassForFood(Integer foodId, Integer classId) {
        //检查菜品和分类是否存在
        checkClassAndFoodCreated(foodId, classId);
        if(foodClassMapper.checkFoodClassCreated(foodId, classId)){
            throw new UnmessageException("对应关系已经存在！");
        }
        return foodClassMapper.insertFoodClass(foodId, classId);
    }

    /**
     *  删除菜品分类
     * @param foodId
     * @param classId
     * @return
     */
    @Override
    public boolean deleteClassForFood(Integer foodId, Integer classId) {
        checkClassAndFoodCreated(foodId, classId);
        return foodClassMapper.deleteOneById(foodId, classId);
    }


    /**
     * 进行分页操作
     * @param current
     * @param size
     * @param name
     * @param classificationId
     * @return
     */
    @Override
    public IPage<Food> getPageBySearch(Integer current, Integer size, String name, Integer classificationId) {
        IPage<Food> foodPage = new Page<>(current, size);
        foodPage = foodMapper.pageByNameAndClassId(foodPage, name, classificationId,
                userController.getShopName());
        return foodPage;
    }


    /**
     * 根据菜品id获取菜品
     * @param id
     * @return
     */
    @Override
    public Food getFoodOrThrow(Integer id) {
        Food food = foodMapper.selectById(id);
        if(food == null){
            throw new UnmessageException("菜品id, 不存在");
        }
        return food;
    }
}
