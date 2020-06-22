package com.authine.cloudpivot.web.api.bean;

import lombok.Data;

import java.util.List;

@Data
public class LeadFixQuanCountInfo {
    private String id;
    private String leadPersonId;
    private String oldParentId;
    private List<AssessmentDetail> adList;
    private String annual;
    private String companyName;
}
