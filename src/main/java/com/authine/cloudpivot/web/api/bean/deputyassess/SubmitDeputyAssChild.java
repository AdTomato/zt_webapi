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
    @JsonAlias(value = "assess_index")
    private String assessIndex;
    @JsonAlias(value = "assess_content")
    private String assessContent;
    private BigDecimal id;
    private String parentId;
    private BigDecimal sortKey;
    private BigDecimal score;
    private String oldParentId;
    private String userId;
    private String assessedPersonId;
    private String deptId;
    private String annual;
}
