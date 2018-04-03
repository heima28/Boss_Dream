package com.itheima.bos.service.jobs;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.itheima.bos.dao.take_delivery.PromotionRepository;
import com.itheima.bos.domain.take_delivery.Promotion;

@Component
public class PromotionJob {

    @Autowired
    private PromotionRepository promotionRepository;

    public void checkPromotionState() {

        // 查询失效时间小于当前时间的所有宣传任务
        List<Promotion> list = promotionRepository.findPromotions(new Date());

        System.out.println(list);

        // 修改宣传任务对象的状态
        if (list.size() > 0) {
            for (Promotion promotion : list) {
                promotion.setStatus("2");
            }
            promotionRepository.save(list);
        }
    }
}
