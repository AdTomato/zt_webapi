package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.engine.api.facade.BizObjectFacade;
import com.authine.cloudpivot.engine.api.model.organization.DepartmentModel;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.web.api.bean.AssessmentResult;

import java.util.List;

/**
 * @Author: wangyong
 * @Date: 2019-12-27 09:37
 * @Description:
 */
public interface CreateAssessmentResultService {

    public List<String> createAssessmentResults(BizObjectFacade objectFacade, String userId, List<AssessmentResult> arList);

    public void createAssessmentResult(BizObjectFacade objectFacade, String userId, AssessmentResult assessmentResult);


    public String isHaveAssessmentResult(AssessmentResult ar);

    public void updateAssessmentResult(List<AssessmentResult> arList);

    void updateOrInsertAssessmentResult(BizObjectFacade bizObjectFacade, String userId, List<AssessmentResult> arList);

    void updateOrInsertAssessmentResultByModel(UserModel user , DepartmentModel department, List<AssessmentResult> arList);

}
