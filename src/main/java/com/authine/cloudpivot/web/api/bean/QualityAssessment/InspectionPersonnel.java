package com.authine.cloudpivot.web.api.bean.QualityAssessment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 中铁考核人员维护表
 * @Author Ke LongHai
 * @Date 2021/4/14 11:54
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InspectionPersonnel {

    private String id;


    /**
     * 被打分人的名字
     */
    private String peopleName;

    /**
     * 单位的名字
     */
    private String unitName;

    /**
     * 职务
     */
    private String job;

    /**
     * 联系方式
     */
    private int phone;

    /**
     * 逻辑
     */
    private int logic;
}
