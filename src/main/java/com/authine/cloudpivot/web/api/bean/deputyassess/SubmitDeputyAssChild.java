package com.authine.cloudpivot.web.api.bean.deputyassess;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author Asuvera
 * @Date 2020/7/22 15:10
 * @Version 1.0
 */
@Data
public class SubmitDeputyAssChild {
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
    /**
     *打分人id
     */
    private String userId;
    /**
     *被考核人
     */
    private String assessedPersonId;
    /**
     *部门id
     */
    private String deptId;
    /**
     *年度
     */
    private String annual;
    /**
     *权重
     */
    private int weight;
    private String name ;
}
