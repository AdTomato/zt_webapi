package com.authine.cloudpivot.web.api.bean.deputyassess;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author Asuvera
 * @Date 2020/7/20 11:36
 * 考核要点及内容包装类
 * @Version 1.0
 */
@Data
public class LaunchDeputyAssChild {
    /**
     * 考核指标
     */
    private String assess_index;
    /**
     * 考核要素及标准
     */
    private String assess_content;
    /**
     * id
     */
    private String id;
    /**
     * 主表
     */
    private String parentId;
    /**
     * 排序关键字
     */
    private BigDecimal sortKey;
}
