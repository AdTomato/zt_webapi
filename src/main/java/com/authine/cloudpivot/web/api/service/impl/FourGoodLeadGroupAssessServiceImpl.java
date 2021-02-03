package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.web.api.bean.fourgoodleadgroup.SubmitFourGoodAssessChild;
import com.authine.cloudpivot.web.api.mapper.FourGoodLeadGroupAssessMapper;
import com.authine.cloudpivot.web.api.service.FourGoodLeadGroupAssessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author zhengshihao
 * @Date: 2021/02/01 15:43
 */
@Service
public class FourGoodLeadGroupAssessServiceImpl implements FourGoodLeadGroupAssessService {
    @Autowired
    FourGoodLeadGroupAssessMapper mapper;

    @Override
    public List<SubmitFourGoodAssessChild> countAvgScore(String id) {
        return mapper.countAvgScore(id);
    }

    @Override
    public void updateAvgScore(List<SubmitFourGoodAssessChild> submitFourGoodAssessChildren, String id) {
        mapper.updateAvgScore(submitFourGoodAssessChildren,id);
    }
}
