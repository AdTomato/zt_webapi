package com.authine.cloudpivot.web.api.parameter;

import com.authine.cloudpivot.web.api.bean.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @Author:wangyong
 * @Date:2020/3/27 0:16
 * @Description: 民主评议表controller层接口参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DemocraticEvaluationSendWf {

    private String id;
    private Date date;
    private List<User> users;

}
