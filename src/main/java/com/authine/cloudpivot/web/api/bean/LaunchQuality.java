package com.authine.cloudpivot.web.api.bean;

import lombok.Data;

import java.util.List;

@Data
public class LaunchQuality extends LaunchAssessment {
    private List<LaunLeadQuaCountRow> assessedPeople;
    private List<LeaderQualityChild> leaderQualityChildren;
    private String companyName;
    private String judgeToString;


}
