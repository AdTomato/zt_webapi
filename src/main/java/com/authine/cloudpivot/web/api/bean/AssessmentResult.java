package com.authine.cloudpivot.web.api.bean;

import lombok.Data;

import java.util.Date;

/**
 * @Author: wangyong
 * @Date: 2019-12-26 14:11
 * @Description: 领导人员考核结果
 */
@Data
public class AssessmentResult extends BaseBean {

    /**
     * 领导人员id
     */
    private String leadershipPerson;

    /**
     * 考核主体
     */
    private String assessContent;

    /**
     * 考核时间
     */
    private Date assessTime;

    /**
     * 考核结果
     */
    private String assessResult;

    /**
     * 发起流程表单的id
     */
    private String pId;
    private String time;

}
