package com.mingren.myl.core.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mingren.myl.core.entity.Classification;
import com.mingren.myl.core.service.ClassificationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassificationServiceImpl implements ClassificationService {


    /**
     * 添加一个新的分类
     * @param classification
     * @return
     */
    @Override
    public boolean addClassification(Classification classification) {
        return false;
    }

    @Override
    public boolean deleteClassificationById(int id) {
        return false;
    }

    @Override
    public List<Classification> getAllClassification() {
        return null;
    }

    @Override
    public IPage<Classification> page(int current, int size) {
        return null;
    }
}
