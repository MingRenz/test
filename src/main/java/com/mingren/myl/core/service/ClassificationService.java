package com.mingren.myl.core.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mingren.myl.core.entity.Classification;

import java.util.List;

public interface ClassificationService {
    boolean addClassification(Classification classification);

    boolean deleteClassificationById(int id);

    List<Classification> getAllClassification();

    IPage<Classification> page(int current, int size);
}
