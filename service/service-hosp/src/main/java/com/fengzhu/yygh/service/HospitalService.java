package com.fengzhu.yygh.service;

import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.data.domain.Page;
import java.util.Map;

public interface HospitalService {
    void save(Map<String, Object> stringObjectMap);

    Object getByHoscode(Object hoscode);

    Page<Hospital> selectHospitalPage(int currentPage, int pageSize, HospitalQueryVo hospitalQueryVo);

    void updateStatus(String id, Integer status);

    Map<String, Object> getHospitalById(String id);

    String getHosptialNameByHoscode(String hoscode);

}
