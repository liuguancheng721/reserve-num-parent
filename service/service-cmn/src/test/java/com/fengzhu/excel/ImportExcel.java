package com.fengzhu.excel;

import com.alibaba.excel.EasyExcel;

public class ImportExcel {
    public static void main(String[] args) {
        String fileName = "C:\\server\\user.xlsx";

        // 调用方法实现读取操作
        EasyExcel.read(fileName,User.class,new ExcelListener()).sheet().doRead();
    }
}
