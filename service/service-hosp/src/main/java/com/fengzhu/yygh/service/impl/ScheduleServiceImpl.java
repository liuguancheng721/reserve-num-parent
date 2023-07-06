package com.fengzhu.yygh.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.BookingScheduleRuleVo;
import com.atguigu.yygh.vo.hosp.ScheduleQueryVo;
import com.fengzhu.yygh.repository.ScheduleRepository;
import com.fengzhu.yygh.service.DepartmentService;
import com.fengzhu.yygh.service.HospitalService;
import com.fengzhu.yygh.service.ScheduleService;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Resource
    private ScheduleRepository scheduleRepository;

    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private HospitalService hospitalService;

    @Resource
    private DepartmentService departmentService;

    @Override
    public void save(Map<String, Object> stringObjectMap) {
        // stringObjectMap转Schedule对象
        String stringMap = JSONObject.toJSONString(stringObjectMap);
        Schedule schedule = JSONObject.parseObject(stringMap, Schedule.class);
        // 获取 hosScheduleId 和 hoscode
        String hosScheduleId = schedule.getHosScheduleId();
        String hoscode = schedule.getHoscode();
        Schedule scheduleExist = scheduleRepository.getScheduleByHoscodeAndHosScheduleId(hoscode,hosScheduleId);
        // 判断是否存在
        if (scheduleExist != null) {
            // 存在，更新
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(0);
            scheduleRepository.save(schedule);
        } else {
            // 不存在，添加
            schedule.setCreateTime(new Date());
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(0);
            scheduleRepository.save(schedule);
        }
    }

    @Override
    public Page<Schedule> selectSchedulePage(Integer currentPage, Integer pageSize, ScheduleQueryVo scheduleQueryVo) {
        // 指定排序字段
        Sort sort = Sort.by(Sort.Direction.DESC,"createTime");
        // 创建分页对象
        Pageable pageable = PageRequest.of(currentPage,pageSize,sort);
        // 创建匹配对象
        ExampleMatcher matcher = ExampleMatcher.matching() // 构建对象
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                // 忽略大小写
                .withIgnoreCase(true);
        // 创建schedule对象，将scheduleQueryVo复制到department
        Schedule schedule = new Schedule();
        BeanUtils.copyProperties(schedule,scheduleQueryVo);
        schedule.setIsDeleted(0);
        // 创建MongoDB的查询条件封装对象
        Example<Schedule> example = Example.of(schedule,matcher);
        // 调用mongoDB的findAll方法
        Page<Schedule> pages = scheduleRepository.findAll(example, pageable);
        return pages;
    }

    @Override
    public void remove(String hoscode, String hosScheduleId) {
        // 根据医院编号和排班id查询排班
        Schedule schedule = scheduleRepository.getScheduleByHoscodeAndHosScheduleId(hoscode, hosScheduleId);
        if (schedule != null) {
            scheduleRepository.deleteById(schedule.getId());
        }
    }

    @Override
    public Map<String, Object> getScheduleRule(Integer currentPage, Integer pageSize, String hoscode, String depcode) {
        // 1、根据医院编号 和 科室编号查询
        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode);
        // 2、根据工作日workDate进行分组
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria), // 匹配条件
                Aggregation.group("workDate") // 分子总代吗
                .first("workDate").as("workDate")
                // 3、统计号源数量
                .count().as("docCount")
                .sum("reservedNumber").as("reservedNumber")
                .sum("availableNumber").as("availableNumber"),
                // 排序
                Aggregation.sort(Sort.Direction.DESC,"workDate"),
                // 实现分页
                Aggregation.skip((currentPage - 1) * pageSize),
                Aggregation.limit(pageSize)
        );

        // 调用方法，最终执行
        AggregationResults<BookingScheduleRuleVo> bookingScheduleRuleVos = mongoTemplate.aggregate(aggregation, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> mappedResults = bookingScheduleRuleVos.getMappedResults();

        // 分组查询总记录数
        Aggregation totalAggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate")
        );
        AggregationResults<BookingScheduleRuleVo> bookingScheduleRuleVos1 = mongoTemplate.aggregate(totalAggregation, Schedule.class, BookingScheduleRuleVo.class);
        int total = bookingScheduleRuleVos1.getMappedResults().size();

        // 遍历集合，将日期转为对应星期
        for (BookingScheduleRuleVo b: mappedResults) {
            Date workDate = b.getWorkDate();
            String dayOfWeek = this.getDayOfWeek(new DateTime(workDate));
            b.setDayOfWeek(dayOfWeek);
        }

        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("bookingScheduleRuleList",mappedResults);
        resultMap.put("total",total);
        String hospitalName = hospitalService.getHosptialNameByHoscode(hoscode);
        Map<String,Object> baseMap = new HashMap<>();
        baseMap.put("hosname",hospitalName);
        resultMap.put("baseMap",baseMap);
        return resultMap;
    }

    @Override
    public List<Schedule> getDetailSchedule(String hoscode, String depcode, String workDate) {
        List<Schedule> scheduleList = scheduleRepository.getScheduleByHoscodeAndDepcodeAndWorkDate(hoscode,depcode,new DateTime(workDate).toDate());
        scheduleList.stream().forEach(item -> {
            this.packageSchedule(item);
        });
        return scheduleList;
    }

    // 封装排班列表详情其他值 医院名称、科室名称、日期对应星期
    private void packageSchedule(Schedule schedule) {
        // 获取医院名称
        String hospitalName = hospitalService.getHosptialNameByHoscode(schedule.getHoscode());
        // 获取科室名称
        String depcode = schedule.getDepcode();
        String departmentName = departmentService.getDepartmentName(schedule.getHoscode(),depcode);
        String weekName = this.getDayOfWeek(new DateTime(schedule.getWorkDate()));
        schedule.getParam().put("hosname",hospitalName);
        schedule.getParam().put("depname",departmentName);
        schedule.getParam().put("dayOfWeek",weekName);
    }

    public String getDayOfWeek(DateTime dateTime) {
        String dayOfWeek = "";
        switch (dateTime.getDayOfWeek()) {
            case DateTimeConstants.SUNDAY:
                dayOfWeek = "周日";
                break;
            case DateTimeConstants.MONDAY:
                dayOfWeek = "周一";
                break;
            case DateTimeConstants.TUESDAY:
                dayOfWeek = "周二";
                break;
            case DateTimeConstants.WEDNESDAY:
                dayOfWeek = "周三";
                break;
            case DateTimeConstants.THURSDAY:
                dayOfWeek = "周四";
                break;
            case DateTimeConstants.FRIDAY:
                dayOfWeek = "周五";
                break;
            case DateTimeConstants.SATURDAY:
                dayOfWeek = "周六";
            default:
                break;
        }
        return dayOfWeek;
    }
}
