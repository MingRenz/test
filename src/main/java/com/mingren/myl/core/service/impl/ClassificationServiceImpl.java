package com.mingren.myl.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mingren.myl.core.controller.UserController;
import com.mingren.myl.core.entity.Classification;
import com.mingren.myl.core.exception.UnmessageException;
import com.mingren.myl.core.mapper.ClassificationMapper;
import com.mingren.myl.core.mapper.FoodClassMapper;
import com.mingren.myl.core.service.ClassificationService;
import com.mingren.myl.core.util.SecurityUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ClassificationServiceImpl implements ClassificationService {

    @Resource
    ClassificationMapper classificationMapper;

    @Resource
    FoodClassMapper foodClassMapper;

    @Resource
    UserController userController;

    /**
     * 添加一个新的分类
     *      先检查这个分类是否已经存在，如果不存在则插入
     * @param classification
     * @return
     */
    @Override
    public boolean addClassification(Classification classification) {
        String name = classification.getName();
        if (classificationMapper.checkNameIsCreated(name, SecurityUtil.getShopName())) {
            throw new UnmessageException("名称已经创建！");
        }
        return classificationMapper.insert(classification) == 1;
    }

    /**
     * 删除分类
     *      当分类下面没有菜品时可以进行删除
     * @param id
     * @return
     */
    @Override
    public boolean deleteClassificationById(int id) {
        if (!foodClassMapper.selectFoodClassByClassId(id).isEmpty()) {
            throw new UnmessageException("分类下有菜品！");
        }
        return classificationMapper.deleteById(id) == 1;
    }

    /**
     * 获取全部分类
     * @return
     */
    @Override
    public List<Classification> getAllClassification() {
        QueryWrapper<Classification> wrapper = new QueryWrapper<>();
        wrapper.eq("shop_name", userController.getShopName());
        wrapper.orderByDesc("id");
        return classificationMapper.selectList(wrapper);
    }

    /**
     * 进行分页操作
     * @param current
     * @param size
     * @return
     */
    @Override
    public IPage<Classification> page(int current, int size) {
        IPage<Classification> page = new Page<>(current, size);
        QueryWrapper<Classification> wrapper = new QueryWrapper<>();
        wrapper.eq("shop_name", userController.getShopName()).orderByDesc("id");
        return classificationMapper.selectPage(page,
                wrapper);
    }
}
