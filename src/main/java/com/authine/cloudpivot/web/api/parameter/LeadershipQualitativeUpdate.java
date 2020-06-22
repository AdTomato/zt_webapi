package com.authine.cloudpivot.web.api.parameter;

import com.authine.cloudpivot.web.api.bean.LeadershipQualitative;
import com.authine.cloudpivot.web.api.bean.LeadershipQualitativeDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author:wangyong
 * @Date:2020/3/28 13:56
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeadershipQualitativeUpdate extends LeadershipQualitative {

    private List<LeadershipQualitativeDetails> leadershipQualitativeDetails;

}
