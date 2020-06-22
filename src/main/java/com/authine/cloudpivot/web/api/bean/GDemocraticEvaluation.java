package com.authine.cloudpivot.web.api.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author:wangyong
 * @Date:2020/3/27 1:08
 * @Description: 工作民主评议表(改)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GDemocraticEvaluation extends BaseBean{

    private String unit;
    private Date date;
    private String commentPerson;
    private int gSatisfiedPoll;
    private int gBasicSatisfiedPoll;
    private int gNoSatisfiedPoll;
    private int gNoUnderstandPoll;
    private int rSatisfiedPoll;
    private int rBasicSatisfiedPoll;
    private int rNoSatisfiedPoll;
    private int rNoUnderstandPoll;
    private int bSatisfiedPoll;
    private int bBasicSatisfiedPoll;
    private int bNoSatisfiedPoll;
    private int bNoUnderstandPoll;
    private int iSatisfiedPoll;
    private int iBasicSatisfiedPoll;
    private int iNoSatisfiedPoll;
    private int iNoUnderstandPoll;

    private String prominentProblem;
    private String concreteContent;
    private String commentSuggestion;

}
