package com.authine.cloudpivot.web.api.dto;

import com.authine.cloudpivot.web.api.bean.JudgesWeight;
import lombok.Data;

import java.time.Instant;
import java.util.List;

/**
 * @ClassName JudgesDeptWeightDto
 * @author: lfh
 * @Date:2020/7/24 9:37
 * @Description:
 **/
@Data
public class JudgesDeptWeightDto {
    private String bid;
    private String dept;
    private Instant date;
    private List<JudgesWeight> judgesWeightList;
}
