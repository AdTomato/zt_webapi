package com.authine.cloudpivot.web.api.bean.sectionassess;

import com.authine.cloudpivot.web.api.bean.DeptName;
import com.authine.cloudpivot.web.api.bean.User;
import com.authine.cloudpivot.web.api.bean.deputyassess.SubmitDeputyAssChild;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.util.List;

/**
 * The type Submit deputy assess request.
 *
 * @Author Asuvera
 * @Date 2020 /7/22 15:06
 * @Version 1.0
 */
@Data
public class SubmitSectionAssessRequest {
    /**
     * 评价打分表父表id
     */
    private String parentId;
    /**
     * 年度
     */
    private String annual;
    /**
     * 部门
     */
    private DeptName dept;
    /**
     * 被考核人
     */

    private List<User> person;
    /**
     * 打分明细
     */
    @JsonAlias(value = "section_chief_asselement")
    private List<SubmitDeputyAssChild> submitDeputyAssChildren;
    /**
     * 发起表父id
     */
    private String oldParentId;

    private int weight;
}
