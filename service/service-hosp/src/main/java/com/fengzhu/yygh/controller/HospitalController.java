package com.fengzhu.yygh.controller;

import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import com.fengzhu.yygh.result.Result;
import com.fengzhu.yygh.service.HospitalService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Api(tags = "医院管理接口")
@RestController
@RequestMapping("/admin/hosp/hospital")
public class HospitalController {

    @Resource
    private HospitalService hospitalService;

    @ApiOperation("医院分页列表")
    @GetMapping("/list/{currentPage}/{pageSize}")
    public Result list(@PathVariable int currentPage, @PathVariable int pageSize, HospitalQueryVo hospitalQueryVo) {
        Page<Hospital> pages = hospitalService.selectHospitalPage(currentPage,pageSize,hospitalQueryVo);
        return Result.ok(pages);
    }

    @ApiOperation("更新医院上线状态")
    @GetMapping("/updateStatus/{id}/{status}")
    public Result updateStatus(@PathVariable String id,Integer status) {
        hospitalService.updateStatus(id,status);
        return Result.ok();
    }

    @ApiOperation("医院详情")
    @GetMapping("/show/{id}")
    public Result showHospital(@PathVariable String id) {
        Map<String, Object> hospital = hospitalService.getHospitalById(id);
        return Result.ok(hospital);
    }
}
