package com.fengzhu.listcopy;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

public class ListCopyToList {
    public static void main(String[] args) {
        List<UserExcel> userExcelList = new ArrayList<>();
        List<User> userList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            User user = new User();
            user.setUid(i);
            user.setUsername("user" + i);
            user.setPassword("5201314" + i);
            userList.add(user);
        }

        for (User user: userList) {
            UserExcel userExcel = new UserExcel();
            // 将一个java对象的属性值赋值到另一个对象中，source对象有的属性，target对象没有，
            // 不会影响复制，他只会复制属性名和类型匹配的属性值
            BeanUtils.copyProperties(user,userExcel);
            userExcelList.add(userExcel);
        }

        System.out.println(userExcelList);
    }
}
