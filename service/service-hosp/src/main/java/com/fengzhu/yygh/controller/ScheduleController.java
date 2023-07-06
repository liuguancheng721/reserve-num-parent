package com.fengzhu.yygh.controller;

import com.atguigu.yygh.model.hosp.Schedule;
import com.fengzhu.yygh.result.Result;
import com.fengzhu.yygh.service.ScheduleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Api(tags = "排班接口")
@RestController
@RequestMapping("/admin/hosp/schedule")
public class ScheduleController {

    @Resource
    private ScheduleService scheduleService;

    @ApiOperation("查询排班规则数据")
    @GetMapping("/getScheduleRule/{currentPage}/{pageSize}/{hoscode}/{depcode}")
    public Result getScheduleRule(@PathVariable Integer currentPage,
                                  @PathVariable Integer pageSize,
                                  @PathVariable String hoscode,
                                  @PathVariable String depcode) {
        Map<String,Object> map =  scheduleService.getScheduleRule(currentPage,pageSize,hoscode,depcode);
        return Result.ok(map);
    }

    @ApiOperation("根据排班日期获取排班详情列表")
    @GetMapping("/getDetailSchedule/{hoscode}/{depcode}/{workDate}")
    public List<Schedule> getDetailSchedule(@PathVariable String hoscode,
                                            @PathVariable String depcode,
                                            @PathVariable String workDate) {
        return scheduleService.getDetailSchedule(hoscode,depcode,workDate);
    }
}
