package com.authine.cloudpivot.web.api.controller;


import com.authine.cloudpivot.engine.enums.ErrCode;
import com.authine.cloudpivot.web.api.bean.LeadPerson;
import com.authine.cloudpivot.web.api.bean.leadership.LeadPersonDO;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.service.LeadpersonService;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


/**
 * <p>
 * 领导人员 前端控制器
 * </p>
 *
 * @author zsh
 * @since 2019-11-26
 */
@RestController
@RequestMapping("/ext/leadperson")
public class LeadpersonController extends BaseController {
    private Logger log = LoggerFactory.getLogger(LeadpersonController.class);

    @Autowired(required = true)
    private LeadpersonService leadpersonService;

    /**
     * 领导人员展示界面,用部门id去查部门下面领导人员
     *
     * @param deptId
     * @return
     */
    @RequestMapping("/leadlist")
    public ResponseResult<List<LeadPerson>> users(String deptId) {
        List<LeadPerson> personList = new ArrayList<LeadPerson>();

        if (deptId == null || "".equals(deptId)) {
            return getErrResponseResult(personList, ErrCode.SYS_PARAMETER_EMPTY.getErrCode(), "error");

        }
        personList = leadpersonService.getBydeptId(deptId);
        for (int i = 0; i < personList.size(); i++) {
            int j = i + 1;
            personList.get(i).setNum(j);
        }
        return getOkResponseResult(personList, "success");
    }

    /**
     * 查看领导人员信息,自动更新领导人员年龄及年龄段
     *
     * @param
     * @return
     */
    @Scheduled(cron = "0 0 12 * * ?")
    @PostMapping("/updateLeaderAge")
    public ResponseResult<String> updateLeaderAge()  {
        log.info("执行更新领导人员年龄任务时间: " + LocalDateTime.now());
        List<LeadPersonDO> leadPersonDOList = leadpersonService.selectAllLeadPerson();
        for (LeadPersonDO leadPersonDO : leadPersonDOList) {
            if (leadPersonDO.getDateOfBirth()!=null) {


                SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd yyyy hh:mm:ss z", Locale.ENGLISH);
                //Date date = format.parse(birthDate);
                Calendar cal = Calendar.getInstance();
                if (cal.before(leadPersonDO.getDateOfBirth())) {
                    throw new IllegalArgumentException(
                            "The birthDay is before Now.It's unbelievable!");
                }
                int yearNow = cal.get(Calendar.YEAR);
                int monthNow = cal.get(Calendar.MONTH) + 1;
                int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
                cal.setTime(leadPersonDO.getDateOfBirth());
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
                if (age <= 20) {
                    ageBracket = "20及以下";
                } else if (age <= 30) {
                    ageBracket = "21-30";
                } else if (age <= 40) {
                    ageBracket = "31-40";
                } else if (age <= 50) {
                    ageBracket = "41-50";
                } else if (age <= 60) {
                    ageBracket = "51-60";
                } else if (age <= 70) {
                    ageBracket = "61-70";
                } else {
                    ageBracket = "71及以上";
                }
                leadPersonDO.setAge((double) age);
                leadPersonDO.setAgeBracket(ageBracket);
            }else{
                leadPersonDO.setAge(0D);
                leadPersonDO.setAgeBracket("20及以下");
            }
        }

        leadpersonService.updateLeaderAge(leadPersonDOList);
        return this.getOkResponseResult("更新全部领导人员年龄及区间成功", "success");
    }
}
