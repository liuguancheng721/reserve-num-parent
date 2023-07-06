package com.fengzhu.yygh.controller;

import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.vo.hosp.HospitalSetQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fengzhu.yygh.exception.YyghException;
import com.fengzhu.yygh.result.Result;
import com.fengzhu.yygh.service.HospitalSetService;
import com.fengzhu.yygh.util.MD5;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.*;

@Api(tags = "医院设置管理")
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
@Slf4j
//@CrossOrigin
public class HospitalSetController {

    @Resource
    private HospitalSetService hospitalSetService;

    @ApiOperation(value = "获取所有医院设置")
    @GetMapping("/findAll")
    public Result findAllHospitalSet() {
        List<HospitalSet> hospitalSets = hospitalSetService.list();
//        try {
//            int res = 1 / 0;
//        } catch (Exception e) {
//            throw new YyghException("被除数不能为0",500);
//        }
        return Result.ok(hospitalSets);
    }

    /**
     * 动态占位符传参方式
     */
    @ApiOperation(value = "逻辑删除医院设置",notes = "根据医院ID")
    @DeleteMapping("/{id}")
    public Result removeHospitalSet(@PathVariable Long id) {
        boolean flag = hospitalSetService.removeById(id);
        if (flag) {
            return Result.ok();
        }
        return Result.fail();
    }

    // 分页及条件查询医院设置
    @ApiOperation("分页及条件查询医院设置")
    @PostMapping("/pageGetHospitalSet/{currentPage}/{pageSize}")
    public Result pageGetHospitalSet( @PathVariable Integer currentPage, @PathVariable Integer pageSize,@RequestBody(required = false) HospitalSetQueryVo hospitalSetQueryVo) {
        System.out.println("currentPage = " + currentPage + "pageSize = " + pageSize + "searchObj = " + hospitalSetQueryVo);
        // 创建分页对象
        Page<HospitalSet> page = new Page<>(currentPage,pageSize);
        // 创建查询条件构造器对象
        QueryWrapper<HospitalSet> queryWrapper = new QueryWrapper();
        if (hospitalSetQueryVo.getHosname() != null && hospitalSetQueryVo.getHosname() != "") {
            queryWrapper.like("hosname",hospitalSetQueryVo.getHosname());
        }
        if (!StringUtils.isEmpty(hospitalSetQueryVo.getHoscode())) {
            queryWrapper.eq("hoscode",hospitalSetQueryVo.getHoscode());
        }
        // 调用分页查询方法
        Page<HospitalSet> hospitalSetPage = hospitalSetService.page(page, queryWrapper);
        return Result.ok(hospitalSetPage);
    }

    // 新增医院设置
    @ApiOperation("新增医院设置")
    @PostMapping("/insertHospitalSet")
    public Result insertHospitalSet(@RequestBody HospitalSet hospitalSet) {
        // 设置签名密钥
        Random random = new Random();
        // MD5
        hospitalSet.setSignKey(MD5.encrypt(String.valueOf(System.currentTimeMillis()) + random.nextInt(1000)));
        hospitalSetService.save(hospitalSet);
        return Result.ok();
    }

    // 更新医院设置
    @ApiOperation("更新医院设置")
    @PostMapping("/updateHospitalSet")
    public Result updateHospitalSet(@RequestBody HospitalSet hospitalSet) {
        boolean flag = hospitalSetService.updateById(hospitalSet);
        if (flag) {
            return Result.ok();
        }
        return Result.fail();
    }

    // 根据id获取医院设置
    @ApiOperation("根据ID获取医院设置")
    @GetMapping("/findHospitalSetById")
    public Result findHospitalSetById(Long id) {
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        return Result.ok(hospitalSet);
    }

    // 批量删除医院设置
    @ApiOperation("根据IDs批量删除医院设置")
    @DeleteMapping("/deleteManyHospitalSetByIds")
    public Result deleteManyHospitalSetByIds(@RequestBody List<Long> ids) {
        log.info("ids = {}",ids);
        boolean flag = hospitalSetService.removeByIds(ids);
        if (flag) {
            return Result.ok();
        }
        return Result.fail();
    }

    // 医院设置锁定和解锁
    @ApiOperation("锁定和解锁医院设置")
    @PutMapping("/lockHospitalSet/{id}/{status}")
    public Result lockHospitalSet(@PathVariable Long id,@PathVariable Integer status) {
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        hospitalSet.setStatus(status);
        boolean flag = hospitalSetService.updateById(hospitalSet);
        if (flag) {
            return Result.ok();
        }
        return Result.fail();
    }

    // 发送签名密钥
    @ApiOperation("发送签名密钥")
    @PostMapping("/sendKey")
    public Result sendKey(Long id) {
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        String signKey = hospitalSet.getSignKey();
        String hoscode = hospitalSet.getHoscode();

        // TODO 发送短信
        return Result.ok();

    }
}
