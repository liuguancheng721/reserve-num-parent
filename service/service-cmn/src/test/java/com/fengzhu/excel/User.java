package com.fengzhu.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class User {

    @ExcelProperty("用户ID")
    private Integer id;

    @ExcelProperty("用户名称")
    private String username;
}
