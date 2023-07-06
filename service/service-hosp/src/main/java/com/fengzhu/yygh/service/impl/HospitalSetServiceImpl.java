package com.fengzhu.yygh.service.impl;

import com.atguigu.yygh.model.hosp.HospitalSet;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fengzhu.yygh.mapper.HospitalSetMapper;
import com.fengzhu.yygh.service.HospitalSetService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.management.Query;

@Service
public class HospitalSetServiceImpl extends ServiceImpl<HospitalSetMapper, HospitalSet> implements HospitalSetService {

    @Resource
    private HospitalSetMapper hospitalSetMapper;

    @Override
    public HospitalSet getHospByHoscode(String hoscode) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("hoscode",hoscode);
        HospitalSet hospitalSet = baseMapper.selectOne(queryWrapper);
        return hospitalSet;
    }
}
