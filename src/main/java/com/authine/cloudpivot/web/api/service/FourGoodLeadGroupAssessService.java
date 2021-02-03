package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.web.api.bean.fourgoodleadgroup.SubmitFourGoodAssessChild;

import java.util.List;

/**
 * @Author baozhayishuo
 * @Date 2021/2/1 15:19
 * @Version 1.0
 */
public interface FourGoodLeadGroupAssessService {
    List<SubmitFourGoodAssessChild> countAvgScore(String id);

    void updateAvgScore(List<SubmitFourGoodAssessChild> submitFourGoodAssessChildren, String id);
}
