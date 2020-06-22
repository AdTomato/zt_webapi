package com.authine.cloudpivot.web.api.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author:wangyong
 * @Date:2020/3/28 11:18
 * @Description: 定性测评表子表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeadershipQualitativeDetails {

    private String id;
    private String parentId;
    private Double sortKey;
    private String evaluationItems;
    private Integer goodPoint;
    private Integer preferablyPoint;
    private Integer ordinaryPoint;
    private Integer poolPoint;
    private String pId;

}
