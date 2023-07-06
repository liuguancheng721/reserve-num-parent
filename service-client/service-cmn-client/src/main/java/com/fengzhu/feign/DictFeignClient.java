package com.fengzhu.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "service-cmn",path = "/admin/cmn/dict")
@Repository
public interface DictFeignClient {


    @GetMapping("/getName/{dictCode}/{value}")
    String getNameByDictCodeAndValue(@PathVariable(name = "dictCode") String dictCode, @PathVariable(name = "value") String value);

    @GetMapping("/getName/{value}")
    String getNameByValue(@PathVariable(name = "value") String value);
}
