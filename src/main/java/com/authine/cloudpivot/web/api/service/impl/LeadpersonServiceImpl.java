package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.web.api.bean.LeadPerson;
import com.authine.cloudpivot.web.api.mapper.LeadpersonMapper;
import com.authine.cloudpivot.web.api.service.LeadpersonService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * <p>
 * 领导人员 服务实现类
 * </p>
 *
 * @author zsh
 * @since 2019-11-26
 */
@Service
public class LeadpersonServiceImpl implements LeadpersonService {
    @Resource
    private LeadpersonMapper mapper;

    @Override
    public List<LeadPerson> getBydeptId(String deptId) {
        return mapper.getByDeptId(deptId);
    }

    @Override
    public Map updateLeaderAge(Date birthDate, String id) {
        Calendar cal = Calendar.getInstance();
        if (cal.before(birthDate)) {
            throw new IllegalArgumentException(
                    "The birthDay is before Now.It's unbelievable!");
        }
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH) + 1;
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
        cal.setTime(birthDate);
        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH) + 1;
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
        int age = yearNow - yearBirth;
        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                // monthNow==monthBirth
                if (dayOfMonthNow < dayOfMonthBirth) {
                    age--;
                }
            } else {
                // monthNow>monthBirth
                age--;
            }
        }
        String ageBracket = null;
        if (age<=20){
            ageBracket = "20及以下";
        }else if (age<=30){
            ageBracket = "21-30";
        }else if (age<=40){
            ageBracket = "31-40";
        }else if (age<=50){
            ageBracket = "41-50";
        }else if (age<=60){
            ageBracket = "51-60";
        }else if (age<=70){
            ageBracket = "61-70";
        }else{
            ageBracket = "71及以上";
        }
        mapper.updateLeaderAge(age,id,ageBracket);
        Map map = new HashMap();
        map.put("age",age);
        map.put("ageBracket",ageBracket);
        return map;
    }
}
