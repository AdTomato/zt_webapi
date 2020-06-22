package com.authine.cloudpivot.web.api.bean;

import lombok.Data;

import java.util.List;

@Data
public class LaunchFixedQuantity extends  LaunchAssessment {
    private List<LeadPerson> assessedPeople;
    private String CompanyName;
    private String parentId;

}
