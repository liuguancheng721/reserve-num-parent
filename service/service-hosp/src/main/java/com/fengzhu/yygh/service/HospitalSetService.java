package com.fengzhu.yygh.service;

import com.atguigu.yygh.model.hosp.HospitalSet;
import com.baomidou.mybatisplus.extension.service.IService;

public interface HospitalSetService extends IService<HospitalSet> {
    HospitalSet getHospByHoscode(String hoscode);
}
