package com.fengzhu.yygh.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.enums.DictEnum;
import com.atguigu.yygh.model.cmn.Dict;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import com.fengzhu.feign.DictFeignClient;
import com.fengzhu.yygh.repository.HospitalRepository;
import com.fengzhu.yygh.service.HospitalService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class HospitalServiceImpl implements HospitalService {

    /**
     * 注入mongoRepository对象
     */
    @Autowired
    private HospitalRepository hospitalRepository;

    /**
     * 注入远程调用服务对象
     */
    @Resource
    private DictFeignClient dictFeignClient;

    @Override
    public void save(Map<String, Object> stringObjectMap) {
        // 把参数map集合转换成对象Hospital
        String mapString = JSONObject.toJSONString(stringObjectMap);
        Hospital hospital = JSONObject.parseObject(mapString, Hospital.class);

        // 判断是否存在数据
        String hoscode = hospital.getHoscode();
        Hospital hospitalExist = hospitalRepository.getHospitalByHoscode(hoscode);

        // 存在，更新
        if (hospitalExist != null) {
            hospital.setId(hospitalExist.getId());
            hospital.setStatus(hospitalExist.getStatus());
            hospital.setCreateTime(hospitalExist.getCreateTime());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        } else {
            // 不存在，添加
            hospital.setStatus(0);
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }

    }

    @Override
    public Object getByHoscode(Object hoscode) {
        return hospitalRepository.getByHoscode(hoscode);
    }

    @Override
    public void updateStatus(String id, Integer status) {
        Hospital hospital = hospitalRepository.findById(id).get();
        hospital.setStatus(status);
        hospital.setUpdateTime(new Date());
        hospitalRepository.save(hospital);
    }

    @Override
    public Map<String, Object> getHospitalById(String id) {
        Map<String, Object> result = new HashMap<>();
        Hospital hospital = this.packHospital(hospitalRepository.findById(id).get());
        result.put("hospital", hospital);
        //单独处理更直观
        result.put("bookingRule", hospital.getBookingRule());
        //不需要重复返回
        hospital.setBookingRule(null);
        return result;
    }

    @Override
    public String getHosptialNameByHoscode(String hoscode) {
        Hospital hospital = hospitalRepository.getHospitalByHoscode(hoscode);
        if (hospital != null) {
            return hospital.getHosname();
        }
        return "";
    }

    @Override
    public Page<Hospital> selectHospitalPage(int currentPage, int pageSize, HospitalQueryVo hospitalQueryVo) {
        // 1、排序
        Sort sort = Sort.by(Sort.Direction.DESC,"createTime");
        // 2、创建分页对象
        Pageable pageable = PageRequest.of(currentPage - 1,pageSize,sort);
        // 3、创建匹配实例
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreCase(true)
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        // 4、封装条件对象
        Hospital hospital = new Hospital();
        BeanUtils.copyProperties(hospitalQueryVo,hospital);
        // 5、创建查询条件对象
        Example<Hospital> example = Example.of(hospital,matcher);
        // 6、调用分页方法
        Page<Hospital> pages = hospitalRepository.findAll(example, pageable);

        // 7、调用远程dict服务中的方法，去获取医院等级名称和完整地址
        List<Hospital> hospitalList = pages.getContent();
        hospitalList.stream().forEach(item -> {
            this.packHospital(item);
        });
        return pages;
    }

    private Hospital packHospital(Hospital hospital) {
        String hospitalName;
        if (DictEnum.HOSTYPE.getDictCode() == null) {
            hospitalName = dictFeignClient.getNameByValue(hospital.getHostype());
        } else {
            hospitalName = dictFeignClient.getNameByDictCodeAndValue(DictEnum.HOSTYPE.getDictCode(), hospital.getHostype());
        }
        String provinceName = dictFeignClient.getNameByValue(hospital.getProvinceCode());
        String cityName = dictFeignClient.getNameByValue(hospital.getCityCode());
        String districtName = dictFeignClient.getNameByValue(hospital.getDistrictCode());
        hospital.getParam().put("hospitalType",hospitalName);
        hospital.getParam().put("fullAddress",provinceName + cityName + districtName + hospital.getAddress());
        return hospital;
    }
}
