package com.authine.cloudpivot.web.api.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author:wangyong
 * @Date:2020/3/27 13:50
 * @Description: 发起新选拔干部民主评议表中的评测干部
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendEvaluatingCadreList {

    private String id;
    private String parentId;
    private Double sortKey;
    private String userName;
    private String gender;
    private Date dateOfBirth;
    private String rawDuty;
    private String cashDuty;
    private Date promotionDate;
    private Double satisfiedPoll;
    private Double basicSatisfiedPoll;
    private Double noSatisfiedPoll;
    private Double noUnderstandPoll;
    private Double existencePoll;
    private Double noExistencePoll;
    private Double iNoUnderstandPoll;


}
