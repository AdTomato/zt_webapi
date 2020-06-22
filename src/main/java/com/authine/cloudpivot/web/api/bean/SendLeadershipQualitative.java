package com.authine.cloudpivot.web.api.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author:wangyong
 * @Date:2020/3/28 11:13
 * @Description: 发起定性测评表主表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendLeadershipQualitative extends BaseBean{

    private String unit;
    private Date date;
    private String commentPerson;
    private Double votePeoples;

}
