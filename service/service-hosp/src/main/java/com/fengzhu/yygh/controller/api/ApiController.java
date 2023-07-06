package com.fengzhu.yygh.controller.api;

import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.DepartmentQueryVo;
import com.atguigu.yygh.vo.hosp.ScheduleQueryVo;
import com.fengzhu.yygh.service.ScheduleService;
import org.springframework.data.domain.Page;
import com.fengzhu.yygh.exception.YyghException;
import com.fengzhu.yygh.helper.HttpRequestHelper;
import com.fengzhu.yygh.result.Result;
import com.fengzhu.yygh.result.ResultCodeEnum;
import com.fengzhu.yygh.service.DepartmentService;
import com.fengzhu.yygh.service.HospitalService;
import com.fengzhu.yygh.service.HospitalSetService;
import com.fengzhu.yygh.util.MD5;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Api(tags = "医院信息接口")
@RestController
@RequestMapping("/api/hosp")
public class ApiController {

    @Resource
    private HospitalService hospitalService;

    @Resource
    private HospitalSetService hospitalSetService;

    @Resource
    private DepartmentService departmentService;

    @Resource
    private ScheduleService scheduleService;

    // 查询医院信息
    @ApiOperation("查询医院信息")
    @PostMapping("/hospital/show")
    public Result showHosp(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> stringObjectMap = HttpRequestHelper.switchMap(parameterMap);
        // 验证签名
        VerificationSign(stringObjectMap);
        return Result.ok(hospitalService.getByHoscode(stringObjectMap.get("hoscode")));

    }

    // 上传医院接口
    @ApiOperation("上传医院接口")
    @RequestMapping("/saveHospital")
    public Result saveHosp(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> stringObjectMap = HttpRequestHelper.switchMap(parameterMap);
        // 验证签名
        VerificationSign(stringObjectMap);
        // 1、因为图片采用的Base64编码传输，+ 会被空格代替，因此要转换回来
        String logoDataString = (String)stringObjectMap.get("logoData");
        if (!StringUtils.isEmpty(logoDataString)) {
            String logoData = logoDataString.replaceAll(" ", "+");
            // 2、将转换好的图片重新放回map集合中
            stringObjectMap.put("logoData",logoData);
        }
        // 调用service中的方法
        hospitalService.save(stringObjectMap);
        return Result.ok();
    }

    // 上传科室接口
    @ApiOperation("上传科室")
    @PostMapping("/saveDepartment")
    public Result saveDepartment(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> stringObjectMap = HttpRequestHelper.switchMap(parameterMap);
        // 验证签名
        VerificationSign(stringObjectMap);
        departmentService.save(stringObjectMap);
        return Result.ok();
    }

    // 查询科室信息
    @ApiOperation("查询科室信息")
    @PostMapping("department/list")
    public Result departmentList(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> stringObjectMap = HttpRequestHelper.switchMap(parameterMap);
        // 验证签名
        VerificationSign(stringObjectMap);

        // 1、获取页数
        Integer currentPage = Integer.parseInt((String)stringObjectMap.get("page"));
        // 2、获取每页显示的记录数
        Integer pageSize = Integer.parseInt((String)stringObjectMap.get("limit"));
        // 3、科室信息条件封装成对象
        DepartmentQueryVo departmentQueryVo = new DepartmentQueryVo();
        departmentQueryVo.setHoscode((String)stringObjectMap.get("hoscode"));
        departmentQueryVo.setDepcode((String)stringObjectMap.get("depcode"));
        // 4、调用方法查询科室信息
        Page<Department> page = departmentService.selectDepartmentPage(currentPage,pageSize, departmentQueryVo);
        return Result.ok(page);
    }

    // 删除科室
    @ApiOperation("删除科室")
    @PostMapping("/department/remove")
    public Result departmentRemove(HttpServletRequest request) {
        // 将parameterMap转为Department对象
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> stringObjectMap = HttpRequestHelper.switchMap(parameterMap);
        // 签名验证
        VerificationSign(stringObjectMap);
        // 1、获取医院编号
        String hoscode = (String)stringObjectMap.get("hoscode");
        // 2、获取科室编号
        String depcode = (String)stringObjectMap.get("depcode");
        // 3、调用方法删除科室
        departmentService.remove(hoscode,depcode);
        return Result.ok();
    }

    // 上传排班接口
    @ApiOperation("上传排班")
    @PostMapping("/saveSchedule")
    public Result saveSchedule(HttpServletRequest request) {
        // 1、取出request中的参数
        Map<String, String[]> parameterMap = request.getParameterMap();
        // 2、将参数里面的String数组转为对象
        Map<String, Object> stringObjectMap = HttpRequestHelper.switchMap(parameterMap);
        // 3、验证签名
        VerificationSign(stringObjectMap);
        // 4、调用方法上传排班
        scheduleService.save(stringObjectMap);
        return Result.ok();
    }

    // 查询排班接口
    @ApiOperation("查询排班")
    @PostMapping("/schedule/list")
    public Result scheduleList(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> stringObjectMap = HttpRequestHelper.switchMap(parameterMap);
        // 验证签名
        VerificationSign(stringObjectMap);
        // 获取分页的页码和条数
        Integer currentPage = Integer.parseInt((String)stringObjectMap.get("page"));
        Integer pageSize = Integer.parseInt((String)stringObjectMap.get("limit"));
        // 封装分页查询的条件
        ScheduleQueryVo scheduleQueryVo = new ScheduleQueryVo();
        scheduleQueryVo.setHoscode((String)stringObjectMap.get("hoscode"));
//        scheduleQueryVo.setDepcode((String)stringObjectMap.get("depcode"));
        Page<Schedule> page = scheduleService.selectSchedulePage(currentPage,pageSize,scheduleQueryVo);
        return Result.ok(page);
    }


    // 查询排班接口
    @ApiOperation("删除排班")
    @PostMapping("/schedule/remove")
    public Result scheduleRemove(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> stringObjectMap = HttpRequestHelper.switchMap(parameterMap);
        // 验证签名
        VerificationSign(stringObjectMap);
        scheduleService.remove((String)stringObjectMap.get("hoscode"),(String)stringObjectMap.get("hosScheduleId"));
        return Result.ok();
    }

    /**
     * 验证医院接口系统发送的签名
     */
    private void VerificationSign(Map<String, Object> paramMap) {
        // 1、获取参数中的sign
        String sign = (String)paramMap.get("sign");

        // 2、获取参数中hoscode
        String hoscode = (String)paramMap.get("hoscode");

        // 3、根据hoscode查询数据库
        HospitalSet hospitalSet = hospitalSetService.getHospByHoscode(hoscode);

        // 4、根据查询结果拿到数据库中的signkey
        String signKey = hospitalSet.getSignKey();

        // 5、将signKey进行MD5加密
        String signKeyMD5 = MD5.encrypt(signKey);

        // 6、判断加密后的signKeyMD5与sign是否一致
        if (!sign.equals(signKeyMD5)) {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
    }
}
