package com.itheima.bos.service.base.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itheima.bos.dao.base.AreaRepository;
import com.itheima.bos.domain.base.Area;
import com.itheima.bos.service.base.AreaService;

import net.sf.json.JSONArray;

/**
 * ClassName:AreaServiceImpl <br/>
 * Function: <br/>
 * Date: 2018年3月15日 上午11:31:39 <br/>
 */
@Service
@Transactional
public class AreaServiceImpl implements AreaService {

    @Autowired
    private AreaRepository areaRepository;

    // 批量保存
    @Override
    public void save(List<Area> list) {
        areaRepository.save(list);
    }

    // 分页查询
    @Override
    public Page<Area> findAll(Pageable pageable) {
        return areaRepository.findAll(pageable);
    }

    @Override
    public List<Area> findByQ(String q) {

        q = "%" + q.toUpperCase() + "%";
        return areaRepository.findQ(q);
    }

    @Override
    public List<Object[]> exportCharts() {

        return areaRepository.exportCharts();
    }


    @Override
    public List<LinkedHashMap<String, Object>> exportfigure() {
        List<Object[]> list = areaRepository.exportfigure();
        List<LinkedHashMap<String, Object>> list2 =
                new ArrayList<LinkedHashMap<String, Object>>();
        for (Object[] objects : list) {

            LinkedHashMap<String, Object> map =
                    new LinkedHashMap<String, Object>();
            map.put("name", objects[0] + "");
            Integer[] integer =
                    new Integer[] {Integer.parseInt(objects[1] + "")};
            map.put("data", integer);
            list2.add(map);

        }
        return list2;
    }

}
