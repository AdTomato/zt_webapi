package com.authine.cloudpivot.web.api.bean.fourgoodleadgroup;

import com.authine.cloudpivot.web.api.bean.DeptName;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Author zhengshihao
 * @Date: 2021/01/28 17:24
 */
@Data
public class SubmitFourGoodAssessRequest {

    private String id;

    private Date date;
    /**
     * 被考核部门
     */
    private List<DeptName> dept;

    private List<SubmitFourGoodAssessChild> submitFourGoodAssessChildren;
}
