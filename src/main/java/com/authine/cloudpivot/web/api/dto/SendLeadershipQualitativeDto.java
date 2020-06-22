package com.authine.cloudpivot.web.api.dto;

import com.authine.cloudpivot.web.api.bean.SendLeadershipQualitative;
import com.authine.cloudpivot.web.api.bean.SendLeadershipQualitativeDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author:wangyong
 * @Date:2020/3/28 11:21
 * @Description: 发起定性测评表dto
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendLeadershipQualitativeDto extends SendLeadershipQualitative {

    private List<SendLeadershipQualitativeDetails> qualitativeDetails;

}
