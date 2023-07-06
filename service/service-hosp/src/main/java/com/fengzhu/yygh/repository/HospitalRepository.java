package com.fengzhu.yygh.repository;

import com.atguigu.yygh.model.hosp.Hospital;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HospitalRepository extends MongoRepository<Hospital,String> {
    /**
     * Spring Data提供了对mongodb的规范，只要方法名称按照一定的规范编写，那么mongodb会自动帮你实现这个方法
     * 详情可百度查查有哪些规范
     */
    // 判断是否存在数据
    Hospital getHospitalByHoscode(String hospcode);

    Object getByHoscode(Object hoscode);

}
