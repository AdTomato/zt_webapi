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
    private String assess_index;
    private String assess_content;
    private String id;
    private String parentId;
    private BigDecimal sortKey;
}
