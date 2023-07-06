package com.fengzhu.yygh.repository;

import com.atguigu.yygh.model.hosp.Department;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends MongoRepository<Department,String> {
    /**
     * Spring Data提供的MongoDB的规范，按照规范命名，MongoDB会自动帮你实现这个方法
     * @param hoscode 医院编号
     * @param depcode 科室编号
     * @return 科室对象
     */
    Department getDepartmentByHoscodeAndDepcode(String hoscode, String depcode);
}
