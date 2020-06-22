package com.authine.cloudpivot.web.api.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author:wangyong
 * @Date:2020/3/27 13:42
 * @Description: 发起新选拔干部民主评议表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendEvaluating extends BaseBean{

    private String unit;
    private Date date;
    private String commentPerson;
    private Double votePeoples;

}
