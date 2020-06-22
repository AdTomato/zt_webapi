package com.authine.cloudpivot.web.api.bean;

import lombok.Data;

/**
 * 发起领导人员定性考核的子表的每一行对应的实体类
 */
@Data
public class LeaderQualityChild {
    //rowid
    private  String id;
    //主表id
    private  String parentId;

    //关联表单的ID
    private  String leadershipName;
    //职务
    private  String leadershipDuty;
    /**
     * 发起考核表的rowId
     */
    private String oldRowId;
    /**
     * 发起考核表parentId
     */
    private String oldParentId;

    private String excellentPoll;
    private String competentPoll;
    private String basicCompetentPoll;
    private String notCompetentPoll;
    private String userId;
}
