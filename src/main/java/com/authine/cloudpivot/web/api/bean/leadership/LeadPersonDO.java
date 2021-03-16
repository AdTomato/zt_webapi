package com.authine.cloudpivot.web.api.bean.leadership;

import java.io.Serializable;

import lombok.Data;

import java.util.Date;

/**
 * @Description
 * @Author Hunter
 * @Date 2021-03-16
 */

@Data
public class LeadPersonDO implements Serializable {

    private static final long serialVersionUID = 8569951863043564240L;

    private String id;


    /**
     * 出生年月
     */
    private Date dateOfBirth;


    /**
     * 年龄
     */
    private Double age;

    /**
     * 年龄段
     */
    private String ageBracket;


}
