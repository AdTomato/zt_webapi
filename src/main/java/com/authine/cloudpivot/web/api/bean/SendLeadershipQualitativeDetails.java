package com.authine.cloudpivot.web.api.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author:wangyong
 * @Date:2020/3/28 11:14
 * @Description: 发起定性测评表子表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendLeadershipQualitativeDetails {

    private String id;
    private String parentId;
    private Double sortKey;
    private String evaluationItems;
    private Double goodPoint;
    private Double preferablyPoint;
    private Double ordinaryPoint;
    private Double poolPoint;

}
