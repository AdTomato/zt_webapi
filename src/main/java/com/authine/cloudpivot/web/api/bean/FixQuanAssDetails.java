package com.authine.cloudpivot.web.api.bean;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FixQuanAssDetails {
    private String leadPersonId;
    private String parentId;
    private String oldParentId;
    private String userId;
    private String annual;
    private String companyName;
    private String assessmentProject;
    private BigDecimal score;
}
