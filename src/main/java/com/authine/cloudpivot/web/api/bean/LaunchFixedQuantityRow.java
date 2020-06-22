package com.authine.cloudpivot.web.api.bean;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 领导人员定量考核分数回显
 */
@Data
public class LaunchFixedQuantityRow  {
    private String assessedName;
    private BigDecimal Political_quality;
    private BigDecimal Occupation_ethics;
    private BigDecimal Style_construction;
    private BigDecimal Honest_business;
    private BigDecimal sci_dec_abi;
    private BigDecimal dri_exe_abi;
    private BigDecimal lea_inn_abi;
    private BigDecimal sol_cap_abi;
    private BigDecimal performance;
    private BigDecimal coll_performance;
    private String parentId;
    private BigDecimal num;


}
