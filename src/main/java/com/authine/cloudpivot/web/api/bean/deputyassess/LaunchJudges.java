package com.authine.cloudpivot.web.api.bean.deputyassess;

import com.authine.cloudpivot.web.api.bean.User;
import lombok.Data;

import java.util.List;

/**
 * @Author Asuvera
 * @Date 2020/7/20 13:46
 * @Version 1.0
 */
@Data
public class LaunchJudges {
    private String id;
    private List<User> judges;
    private int weight;
    private String roleName;
    private String mutual;
    private String table;
}
