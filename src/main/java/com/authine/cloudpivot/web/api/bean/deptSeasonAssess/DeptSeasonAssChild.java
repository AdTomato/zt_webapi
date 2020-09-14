package com.authine.cloudpivot.web.api.bean.deptSeasonAssess;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author Asuvera
 * @Date 2020/9/8 11:35
 * @Version 1.0
 */
@Data
public class DeptSeasonAssChild {
    private String id;
    /**
     *考核项目
     */
    @JsonAlias(value = "assess_index")
    private String assessIndex;
    /**
     *考核内容
     */
    @JsonAlias(value = "assess_content")
    private String assessContent;
    /**
     * 考核标准
     */
    @JsonAlias(value = "score_criterion")
    private String scoreCriterion;

    /**
     *评价表主表id
     */
    private String parentId;
    private BigDecimal sortKey;
    /**
     *分数
     */
    private BigDecimal score;
    /**
     *发起表主表id
     */
    private String oldParentId;
}
