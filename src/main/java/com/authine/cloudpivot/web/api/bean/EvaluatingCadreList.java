package com.authine.cloudpivot.web.api.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author:wangyong
 * @Date:2020/3/27 14:26
 * @Description: 新选拔干部民主评议表中的评测干部
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluatingCadreList {

    private String id;
    private String parentId;
    private Double sortKey;
    private String userName;
    private String gender;
    private Date dateOfBirth;
    private String rawDuty;
    private String cashDuty;
    private Date promotionDate;
    private Integer satisfiedPoll;
    private Integer basicSatisfiedPoll;
    private Integer noSatisfiedPoll;
    private Integer noUnderstandPoll;
    private Integer existencePoll;
    private Integer noExistencePoll;
    private Integer iNoUnderstandPoll;
    private String pId;


}
