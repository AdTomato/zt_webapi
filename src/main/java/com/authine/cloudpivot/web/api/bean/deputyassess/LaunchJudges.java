package com.authine.cloudpivot.web.api.bean.deputyassess;

import com.authine.cloudpivot.web.api.bean.User;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author Asuvera
 * @Date 2020/7/20 13:46
 * @Version 1.0
 */
@Data
public class LaunchJudges {
    /**
     * id
     */
    private String id;
    /**
     * 评委
     */
    private List<User> judges;
    /**
     * 权重
     */
    private BigDecimal weight;
    /**
     * 职位
     */
    private String roleName;
    /**
     * 是否参加互评
     */
    private String mutual;
    /**
     * 使用哪种表格
     */
    private String table;
}
