package com.fengzhu.yygh.controller;

import com.atguigu.yygh.model.cmn.Dict;
import com.fengzhu.yygh.result.Result;
import com.fengzhu.yygh.service.DictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api(tags = "数据字典")
@RestController
@RequestMapping("/admin/cmn/dict")
public class DictController {

    @Resource
    private DictService dictService;

    @ApiOperation("根据数据id查询子数据列表")
    @GetMapping("/findChildData")
    public Result findChildData(Long id) {
        List<Dict> dictList = dictService.findChildData(id);
        return Result.ok(dictList);
    }

    @ApiOperation("导出数据字典")
    @GetMapping("/exportData")
    public void exportData(HttpServletResponse response) {
        dictService.exportData(response);
    }

    @ApiOperation("导入数据字典")
    @PostMapping("/importData")
    public Result importData(MultipartFile file) {
        dictService.importData(file);
        return Result.ok();
    }

    @ApiOperation("获取数据字典名称")
    @GetMapping("/getName/{dictCode}/{value}")
    public String getNameByDictCodeAndValue(@PathVariable String dictCode,@PathVariable String value) {
        return dictService.getNameByDictCodeAndValue(dictCode,value);
    }


    @ApiOperation("获取数据字典名称")
    @GetMapping("/getName/{value}")
    public String getNameByValue(@PathVariable String value) {
        return dictService.getNameByDictCodeAndValue("",value);
    }

    @ApiOperation("根据dictCode获取下级节点")
    @GetMapping("/findByDictCode/{dictCode}")
    public Result findByDictCode(@PathVariable String dictCode) {
        List<Dict> dictList = dictService.findProvinceByDictCode(dictCode);
        return Result.ok(dictList);
    }

}

