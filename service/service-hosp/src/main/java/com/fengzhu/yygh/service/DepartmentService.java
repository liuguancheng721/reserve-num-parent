package com.fengzhu.yygh.service;

import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.vo.hosp.DepartmentQueryVo;
import com.atguigu.yygh.vo.hosp.DepartmentVo;
import org.springframework.data.domain.Page;


import java.util.List;
import java.util.Map;

public interface DepartmentService {
    void save(Map<String, Object> stringObjectMap);

    Page<Department> selectDepartmentPage(Integer currentPage, Integer pageSize, DepartmentQueryVo departmentQueryVo);

    void remove(String hoscode, String depcode);

    List<DepartmentVo> findDepartmentTree(String hoscode);

    String getDepartmentName(String hoscode, String depcode);
}
