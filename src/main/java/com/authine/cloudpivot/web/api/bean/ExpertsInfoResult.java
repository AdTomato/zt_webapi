package com.authine.cloudpivot.web.api.bean;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

/**
 * @Author:lfh
 * @Date:2020/3/2 15:04
 * @Description:
 */
@Data
public class ExpertsInfoResult {
    /**
     * id
     */
    private String id;

    /**
     * 姓名
     */
    @JsonAlias("user_name")
    private String userName;

    /**
     *单位
     */
    private String unit;

    /**
     *专家等级
     */
    private String expert_level;


}
