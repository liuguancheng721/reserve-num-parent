package com.fengzhu.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

import java.util.Map;

public class ExcelListener extends AnalysisEventListener<User> {

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        System.out.println("表头信息->" + headMap);
    }

    /**
     * 一行一行读取，从第二行读取
     */
    @Override
    public void invoke(User user, AnalysisContext analysisContext) {
        System.out.println(user);
    }

    /**
     * 读取之后执行
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
