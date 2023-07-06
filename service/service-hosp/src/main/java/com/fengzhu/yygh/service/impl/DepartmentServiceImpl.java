package com.fengzhu.yygh.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.vo.hosp.DepartmentQueryVo;
import com.atguigu.yygh.vo.hosp.DepartmentVo;
import com.fengzhu.yygh.repository.DepartmentRepository;
import com.fengzhu.yygh.service.DepartmentService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Resource
    private DepartmentRepository departmentRepository;

    @Override
    public void save(Map<String, Object> stringObjectMap) {
        // 将stringObjectMap转为Department对象
        String mapString = JSONObject.toJSONString(stringObjectMap);
        Department department = JSONObject.parseObject(mapString, Department.class);

        // 获取department中的hoscode 和 depcode(根据医院编号和科室编号查询）
        Department departmentExist = departmentRepository.getDepartmentByHoscodeAndDepcode(department.getHoscode(),department.getDepcode());

        // 判断departmentExist是否存在
        if (departmentExist != null) {
            // 存在，更新
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            departmentRepository.save(department);
        } else {
            // 不存在，添加
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            departmentRepository.save(department);
        }
    }

    @Override
    public Page<Department> selectDepartmentPage(Integer currentPage, Integer pageSize, DepartmentQueryVo departmentQueryVo) {
        // 根据创建时间排序
        Sort sort = Sort.by(Sort.Direction.DESC,"createTime");
        // 创建分页对象
        Pageable pageable = PageRequest.of(currentPage,pageSize,sort);
        // 创建匹配器（固定写法，知道这个是干嘛的就行）
        ExampleMatcher matcher = ExampleMatcher.matching() // 构建对象
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                 // 忽略大小写
                .withIgnoreCase(true);
        // 创建department对象
        Department department = new Department();
        BeanUtils.copyProperties(departmentQueryVo,department);
        department.setIsDeleted(0);
        Example<Department> departmentQueryVoExample = Example.of(department, matcher);
        Page<Department> pages = departmentRepository.findAll(departmentQueryVoExample,pageable);
        return pages;
    }

    @Override
    public void remove(String hoscode, String depcode) {
        // 根据医院编号和科室编号查询科室
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if (department != null) {
            departmentRepository.deleteById(department.getId());
        }
    }

    @Override
    public List<DepartmentVo> findDepartmentTree(String hoscode) {
        Department department = new Department();
        department.setHoscode(hoscode);
        Example<Department> departmentExample = Example.of(department);
        List<Department> allDepartment = departmentRepository.findAll(departmentExample);
        // 根据bigcode分组，并且获取每个大科室下面的子级科室
        Map<String, List<Department>> collectionDepartment = allDepartment.stream().collect(Collectors.groupingBy(Department::getBigcode));
        // 用于最终数据封装集合
        List<DepartmentVo> resultList = new ArrayList<>();
        for(Map.Entry<String,List<Department>> entry: collectionDepartment.entrySet()) {
            // 大科室编号
            String bigCode = entry.getKey();
            // 大科室编号对应的全局数据
            List<Department> departmentList = entry.getValue();
            DepartmentVo departmentVo = new DepartmentVo();
            departmentVo.setDepcode(bigCode);
            departmentVo.setDepname(departmentList.get(0).getBigname());
            // 封装小科室
            List<DepartmentVo> children = new ArrayList<>();
            for (Department d :departmentList) {
                DepartmentVo departmentVo1 = new DepartmentVo();
                departmentVo1.setDepname(d.getDepname());
                departmentVo1.setDepcode(d.getDepcode());
                children.add(departmentVo1);
            }
            // 设置子节点
            departmentVo.setChildren(children);
            resultList.add(departmentVo);
        }
        return resultList;
    }

    @Override
    public String getDepartmentName(String hoscode, String depcode) {
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode,depcode);
        if (department != null) {
            return department.getDepname();
        }
        return "";
    }
}
