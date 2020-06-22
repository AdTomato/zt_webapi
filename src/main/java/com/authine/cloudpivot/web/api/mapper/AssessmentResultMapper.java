package com.authine.cloudpivot.web.api.mapper;

import com.authine.cloudpivot.web.api.bean.AssessmentResult;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author: wangyong
 * @Date: 2019-12-30 12:51
 * @Description: 考核结果mapper
 */
@Mapper
public interface AssessmentResultMapper {

    public String isHaveAssessmentResult(AssessmentResult ar);

    public void updateAssessmentResult(List<AssessmentResult> arList);

    void insertAssessmentResult(List<AssessmentResult> car);
}
