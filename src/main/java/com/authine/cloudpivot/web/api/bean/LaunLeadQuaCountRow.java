package com.authine.cloudpivot.web.api.bean;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 发起领导人员定性考核的子表的每一行对应的实体类
 */
@Data
public class LaunLeadQuaCountRow {
    //rowid
    private  String id;
    //主表id
    private  String parentId;

    //关联表单的ID
    private  String leadershipName;
    //职务
    private  String leadershipDuty;
    /**
     * 从发起考核表复制到评分表后rowId
     */
    private String newRowId;
    /**
     * 从发起考核表复制到评分表后的parentId
     */
    private String newParentId;

    private BigDecimal excellentPoll;
    private BigDecimal competentPoll;
    private BigDecimal basicCompetentPoll;
    private BigDecimal notCompetentPoll;

    private BigDecimal sortKey;
}
