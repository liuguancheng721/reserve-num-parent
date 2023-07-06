package com.fengzhu.yygh.service.impl;

import com.alibaba.excel.EasyExcel;
import com.atguigu.yygh.model.cmn.Dict;
import com.atguigu.yygh.vo.cmn.DictEeVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fengzhu.yygh.listener.ExcelListener;
import com.fengzhu.yygh.mapper.DictMapper;
import com.fengzhu.yygh.service.DictService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    @Autowired
    private ExcelListener listener;

    /**
     * @Cacheable 将方法返回结果进行缓存，下次请求时，如果缓存存在，则直接读取缓存中的数据
     */
    @Cacheable(value = "dict",keyGenerator = "keyGenerator")
    @Override
    public List<Dict> findChildData(Long id) {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper();
        queryWrapper.eq("parent_id",id);
        List<Dict> dictList = baseMapper.selectList(queryWrapper);
        for (Dict dict:dictList) {
            Long dictId = dict.getId();
            boolean hasChild = isHasChild(dictId);
            dict.setHasChildren(hasChild);
        }
        return dictList;
    }

    // 判断id下面是否有子节点
    private boolean isHasChild(Long id) {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id",id);
        Integer count = baseMapper.selectCount(queryWrapper);
        return count > 0;
    }

    /**
     * 数据字典导出为excel表
     */
    @Override
    public void exportData(HttpServletResponse response) {
        try {
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode("数据字典", "UTF-8");
            response.setHeader("Content-disposition", "attachment;filename="+ fileName + ".xlsx");

            List<Dict> dictList = baseMapper.selectList(null);
            List<DictEeVo> dictEeVoList = new ArrayList<>();
            for (Dict dict: dictList) {
                DictEeVo dictEeVo = new DictEeVo();
                BeanUtils.copyProperties(dict,dictEeVo);
                dictEeVoList.add(dictEeVo);
            }

            // 调用方法实现导出操作
            EasyExcel.write(response.getOutputStream(),DictEeVo.class).sheet("dict").doWrite(dictEeVoList);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 导入
     * @CacheEvict 清空指定的缓存空间缓存，一般用在更新或者删除方法上，allEntries表示是否清空所有缓存
     */
    @CacheEvict(value = "dict",allEntries = true)
    @Override
    public void importData(MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(),DictEeVo.class,listener).sheet().doRead();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getNameByDictCodeAndValue(String dictCode, String value) {
        // 传过来的dictCode为空，则执行下列语句
        if (StringUtils.isEmpty(dictCode)) {
            Dict dict = baseMapper.selectOne(new QueryWrapper<Dict>().eq("value", value));
            if (dict != null) {
                return dict.getName();
            }
        } else {
            // 获取父数据
            Dict dict = baseMapper.selectOne(new QueryWrapper<Dict>().eq("dict_code",dictCode));
            if (dict != null) {
                // 获取父的id
                Long parentId = dict.getId();
                Dict dictChild = baseMapper.selectOne(new QueryWrapper<Dict>().eq("parent_id", parentId).eq("value",value));
                if (dictChild != null) {
                    return dictChild.getName();
                }
            }
        }
        return null;
    }

    @Override
    public List<Dict> findProvinceByDictCode(String dictCode) {
        // 根据dictCode查询省份大类
        Dict dict = baseMapper.selectOne(new QueryWrapper<Dict>().eq("dict_code", dictCode));
        // 根据省份的id查询所有省份
        List<Dict> provinceChildData = this.findChildData(dict.getId());
        return provinceChildData;
    }
}
