package com.authine.cloudpivot.web.api.mapper;

import com.authine.cloudpivot.web.api.bean.fourgoodleadgroup.SubmitFourGoodAssessChild;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author zhengshihao
 * @Date: 2021/02/01 15:44
 */
@Component
public interface FourGoodLeadGroupAssessMapper {
    List<SubmitFourGoodAssessChild> countAvgScore(String id);

    void updateAvgScore(List<SubmitFourGoodAssessChild> submitFourGoodAssessChildren, @Param("id") String id);
}
