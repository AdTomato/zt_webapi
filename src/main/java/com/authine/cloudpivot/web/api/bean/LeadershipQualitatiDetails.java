package com.authine.cloudpivot.web.api.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wangyong
 * @time 2020/6/4 21:41
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeadershipQualitatiDetails {

    private String id;

    private String parentId;

    private Double sortKey;

    private String pId;

    private String evaluationItems;

    private Integer goodPoint;

    private Integer preferablyPoint;

    private Integer ordinaryPoint;

    private Integer poolPoint;

}
