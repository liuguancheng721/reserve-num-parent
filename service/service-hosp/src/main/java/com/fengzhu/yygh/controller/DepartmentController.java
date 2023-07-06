package com.fengzhu.yygh.controller;

import com.atguigu.yygh.vo.hosp.DepartmentVo;
import com.fengzhu.yygh.result.Result;
import com.fengzhu.yygh.service.DepartmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Api(tags = "医院科室")
@RequestMapping("/admin/hosp/department")
public class DepartmentController {

    @Resource
    private DepartmentService departmentService;

    @ApiOperation("查询医院所有科室列表")
    @GetMapping("/getDepList/{hoscode}")
    public Result getDepList(@PathVariable String hoscode) {
        List<DepartmentVo> departmentTree = departmentService.findDepartmentTree(hoscode);
        return Result.ok(departmentTree);
    }
}
