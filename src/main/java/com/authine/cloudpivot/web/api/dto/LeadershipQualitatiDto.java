package com.authine.cloudpivot.web.api.dto;

import com.authine.cloudpivot.web.api.bean.LeadershipQualitati;
import com.authine.cloudpivot.web.api.bean.LeadershipQualitatiDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author wangyong
 * @time 2020/6/4 21:44
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeadershipQualitatiDto extends LeadershipQualitati {

    private List<LeadershipQualitatiDetails> leadershipQualitatiDetails;

}
