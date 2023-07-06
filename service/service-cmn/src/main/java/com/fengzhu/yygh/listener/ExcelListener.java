package com.fengzhu.yygh.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.atguigu.yygh.model.cmn.Dict;
import com.atguigu.yygh.vo.cmn.DictEeVo;
import com.fengzhu.yygh.mapper.DictMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class ExcelListener extends AnalysisEventListener<DictEeVo> {

    @Resource
    private DictMapper dictMapper;

    @Override
    public void invoke(DictEeVo dictEeVo, AnalysisContext analysisContext) {
        // 创建表对应的实体类对象
        Dict dict = new Dict();
        // 将excel表的对象中的数据复制到实体类对象中
        BeanUtils.copyProperties(dictEeVo,dict);
        // 往数据库中插入数据
        dictMapper.insert(dict);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
