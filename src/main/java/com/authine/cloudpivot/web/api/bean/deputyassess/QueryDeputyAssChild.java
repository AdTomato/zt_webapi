package com.authine.cloudpivot.web.api.bean.deputyassess;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @Author Asuvera
 * @Date 2020/7/28 10:55
 * @Version 1.0
 */
@Data
public class QueryDeputyAssChild {
    private String name ;
    private String position;
    private Map<String, BigDecimal> assessmentResult;
}
