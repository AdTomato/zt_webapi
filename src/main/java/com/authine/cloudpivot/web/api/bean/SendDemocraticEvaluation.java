package com.authine.cloudpivot.web.api.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author:wangyong
 * @Date:2020/3/27 1:06
 * @Description: 发起工作民主评议表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendDemocraticEvaluation extends BaseBean {

    /**
     * 单位
     */
    private String unit;

    /**
     * 日期
     */
    private Date date;

    /**
     * 评议人员
     */
    private String commentPerson;

    /**
     * 满意票数
     */
    private Integer gSatisfiedPoll = 0;

    /**
     * 基本满意票数
     */
    private Integer gBasicSatisfiedPoll = 0;

    /**
     * 不满意票数
     */
    private Integer gNoSatisfiedPoll = 0;

    /**
     * 不了解票数
     */
    private Integer gNoUnderstandPoll = 0;

    /**
     * 满意票数
     */
    private Integer rSatisfiedPoll = 0;

    /**
     * 基本满意票数
     */
    private Integer rBasicSatisfiedPoll = 0;

    /**
     * 不满意票数
     */
    private Integer rNoSatisfiedPoll = 0;

    /**
     * 不了解票数
     */
    private Integer rNoUnderstandPoll = 0;

    /**
     * 满意票数
     */
    private Integer bSatisfiedPoll = 0;

    /**
     * 基本满意票数
     */
    private Integer bBasicSatisfiedPoll = 0;

    /**
     * 不满意票数
     */
    private Integer bNoSatisfiedPoll = 0;

    /**
     * 不了解票数
     */
    private Integer bNoUnderstandPoll = 0;

    /**
     * 满意票数
     */
    private Integer iSatisfiedPoll = 0;

    /**
     * 基本满意票数
     */
    private Integer iBasicSatisfiedPoll = 0;

    /**
     * 不满意票数
     */
    private Integer iNoSatisfiedPoll = 0;

    /**
     * 不了解票数
     */
    private Integer iNoUnderstandPoll = 0;

    /**
     * 执行《干部任用条例》规定的资格、条件和程序不严格
     */
    private Integer noStrictPoll = 0;

    /**
     * 任人唯亲
     */
    private Integer appointPeoplePoll = 0;

    /**
     * 领导干部用人上个人说了算
     */
    private Integer individualPoll = 0;

    /**
     * 跑官要官，说情打招呼
     */
    private Integer runOfficePoll = 0;

    /**
     * 买官卖官
     */
    private Integer buySellOfficePoll = 0;

    /**
     * 拉票
     */
    private Integer canvassingPoll = 0;

    /**
     * 不存在突出问题
     */
    private Integer noOutstandingProblemsPoll = 0;

    /**
     * 其他问题
     */
    private String otherProblems = "";

    /**
     * 意见和建议
     */
    private String commentSuggestion = "";

    /**
     * 投票人数
     */
    private Integer votePeoples = 0;

}
