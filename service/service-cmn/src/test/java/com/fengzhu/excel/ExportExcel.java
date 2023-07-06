package com.fengzhu.excel;

import com.alibaba.excel.EasyExcel;

import java.util.ArrayList;
import java.util.List;

public class ExportExcel {
    public static void main(String[] args) {

        String fileName = "C:\\server\\user.xlsx";

        // 调用方法实现写操作
        List<User> userList = new ArrayList();

        for(int i = 0; i < 10; i++) {
            User user = new User();
            user.setId(i);
            user.setUsername("user" + i);
            userList.add(user);
        }
        // 调用方法实现导出操作
        EasyExcel.write(fileName,User.class).sheet("用户信息").doWrite(userList);
    }
}
