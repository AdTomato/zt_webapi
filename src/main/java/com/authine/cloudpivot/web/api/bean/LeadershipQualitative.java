package com.authine.cloudpivot.web.api.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author:wangyong
 * @Date:2020/3/28 11:17
 * @Description: 定性测评表主表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeadershipQualitative extends BaseBean {

    private String unit;
    private Date date;
    private String commentPerson;

}
