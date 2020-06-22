package com.authine.cloudpivot.web.api.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: wangyong
 * @time: 2020/5/7 11:26
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewDemocraticEvaluation {

    private Integer gSatisfiedPoll;
    private Integer gBasicSatisfiedPoll;
    private Integer gNoSatisfiedPoll;
    private Integer gNoUnderstandPoll;

    private Integer rSatisfiedPoll;
    private Integer rBasicSatisfiedPoll;
    private Integer rNoSatisfiedPoll;
    private Integer rNoUnderstandPoll;

    private Integer bSatisfiedPoll;
    private Integer bBasicSatisfiedPoll;
    private Integer bNoSatisfiedPoll;
    private Integer bNoUnderstandPoll;

    private Integer iSatisfiedPoll;
    private Integer iBasicSatisfiedPoll;
    private Integer iNoSatisfiedPoll;
    private Integer iNoUnderstandPoll;

    private String prominentProblem;
    private String concreteContent;

    private String commentSuggestion;
}
