package com.authine.cloudpivot.web.api.bean;

import lombok.Data;

import java.util.List;

@Data
public class LeadFixQuanCountInfo {
    /**
     * 主表id
     */
    private String id;
    /**
     * 被考核人id,关联表单关联领导人员
     */
    private String leadPersonId;
    /**
     * 发起表id
     */
    private String oldParentId;
    /**
     * 子表数据
     */
    private List<AssessmentDetail> adList;
    /**
     * 年度
     */
    private String annual;
    /**
     * 公司名称
     */
    private String companyName;
}
