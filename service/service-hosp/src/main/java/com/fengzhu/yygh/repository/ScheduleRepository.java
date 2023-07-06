package com.fengzhu.yygh.repository;

import com.atguigu.yygh.model.hosp.Schedule;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ScheduleRepository extends MongoRepository<Schedule,String> {
    /**
     * Spring Data提供了对mongodb的规范，只要方法名称按照一定的规范编写，那么mongodb会自动帮你实现这个方法
     * @param hoscode 医院编号
     * @param hosScheduleId 排班id
     * @return 排班对象
     */
    Schedule getScheduleByHoscodeAndHosScheduleId(String hoscode, String hosScheduleId);


    List<Schedule> getScheduleByHoscodeAndDepcodeAndWorkDate(String hoscode, String depcode, Date toDate);
}
