package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.engine.api.facade.BizObjectFacade;
import com.authine.cloudpivot.engine.api.model.organization.DepartmentModel;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.engine.api.model.runtime.BizObjectModel;
import com.authine.cloudpivot.web.api.bean.AssessmentResult;
import com.authine.cloudpivot.web.api.mapper.AssessmentResultMapper;
import com.authine.cloudpivot.web.api.service.CreateAssessmentResultService;
import com.authine.cloudpivot.web.api.utils.DataSetUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * @Author: wangyong
 * @Date: 2019-12-27 09:39
 * @Description:
 */
@Service
@Slf4j
public class CreateAssessmentResultServiceImpl implements CreateAssessmentResultService {

    @Resource
    AssessmentResultMapper assessmentResultMapper;

    @Override
    public List<String> createAssessmentResults(BizObjectFacade objectFacade, String userId, List<AssessmentResult> arList) {

        List<BizObjectModel> bizObjects = new ArrayList<>();

        for (AssessmentResult ar :
                arList) {
            bizObjects.add(getBizObjectModel(ar));
        }
        return objectFacade.addBizObjects(userId, bizObjects, "id");
    }

    @Override
    public void createAssessmentResult(BizObjectFacade objectFacade, String userId, AssessmentResult assessmentResult) {
        String id = isHaveAssessmentResult(assessmentResult);
        if (StringUtils.isEmpty(id)) {
            // 创建一条
            objectFacade.saveBizObjectModel(userId, getBizObjectModel(assessmentResult), "id");
        } else {
            // 更新
            updateAssessmentResult(Arrays.asList(assessmentResult));
        }
    }

    @Override
    public String isHaveAssessmentResult(AssessmentResult ar) {
        return assessmentResultMapper.isHaveAssessmentResult(ar);
    }

    @Override
    public void updateAssessmentResult(List<AssessmentResult> arList) {
        assessmentResultMapper.updateAssessmentResult(arList);
    }

    @Override
    public void updateOrInsertAssessmentResult(BizObjectFacade bizObjectFacade, String userId, List<AssessmentResult> arList) {
        String arId = null;
        List<AssessmentResult> uarList = new ArrayList<>();
        List<AssessmentResult> carList = new ArrayList<>();
        for (AssessmentResult ar : arList) {
            arId = isHaveAssessmentResult(ar);
            if (null != arId) {
                ar.setId(arId);
                uarList.add(ar);
            } else {
                carList.add(ar);
            }
        }
        if (0 != carList.size()) {
            log.info("新增考核结果");
            createAssessmentResults(bizObjectFacade, userId, carList);
        }

        if (0 != uarList.size()) {
            log.info("更新考核结果");
            updateAssessmentResult(uarList);
        }
    }

    private BizObjectModel getBizObjectModel(AssessmentResult assessmentResult) {
        BizObjectModel model = new BizObjectModel();
        model.setSchemaCode("AssessmentResult");
        model.setSequenceStatus("COMPLETED");
        Map data = new HashMap();
        data.put("leadershipPerson", assessmentResult.getLeadershipPerson());
        data.put("assess_content", assessmentResult.getAssessContent());
        data.put("assess_time", assessmentResult.getAssessTime());
        data.put("assess_result", assessmentResult.getAssessResult());
        data.put("p_id", assessmentResult.getPId());
        model.put(data);
        return model;
    }

    @Override
    public void updateOrInsertAssessmentResultByModel(UserModel user , DepartmentModel department, List<AssessmentResult> arList) {

        String arId = null;
        List<AssessmentResult> uarList = new ArrayList<>();
        List<AssessmentResult> carList = new ArrayList<>();
        for (AssessmentResult ar : arList) {
            arId = isHaveAssessmentResult(ar);
            if (null != arId) {
                ar.setId(arId);
                uarList.add(ar);
            } else {
                carList.add(ar);
            }
        }
        if (0 != carList.size()) {
            log.info("新增考核结果");
            //createAssessmentResults(bizObjectFacade, userId, carList);
            for (AssessmentResult car:
                    carList) {
                String s = DataSetUtils.setBaseData(car, user, department, "考核结果", "COMPLETED");
            }
            assessmentResultMapper.insertAssessmentResult(carList);

        }

        if (0 != uarList.size()) {
            log.info("更新考核结果");
            updateAssessmentResult(uarList);

        }


    }
}
