package com.authine.cloudpivot.web.api.parameter;

import com.authine.cloudpivot.web.api.bean.SendLeadershipQualitativeDetails;
import com.authine.cloudpivot.web.api.bean.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @Author:wangyong
 * @Date:2020/3/28 13:10
 * @Description: 领导班子定性测评表开启流程参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeadershipQualitativeSendWf {

    private String id;
    private Date date;
    private List<User> users;
    private List<SendLeadershipQualitativeDetails> sendLeadershipQualitativeDetails;

}

