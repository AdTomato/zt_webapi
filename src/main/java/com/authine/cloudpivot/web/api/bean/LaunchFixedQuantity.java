package com.authine.cloudpivot.web.api.bean;

import lombok.Data;

import java.util.List;

@Data
public class LaunchFixedQuantity extends  LaunchAssessment {
    /**
     * 被评分人
     */
    private List<LeadPerson> assessedPeople;
    /**
     * 公司名称
     */
    private String CompanyName;
    /**
     * 主表id
     */
    private String parentId;

}
