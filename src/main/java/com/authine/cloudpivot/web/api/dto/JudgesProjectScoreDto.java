package com.authine.cloudpivot.web.api.dto;

import lombok.Data;

import java.time.Instant;

/**
 * @ClassName JudgesProjectScoreDto
 * @author: lfh
 * @Date:2020/7/21 14:16
 * @Description: 总部部门业绩评价表 评委打分数据
 **/
@Data
public class JudgesProjectScoreDto {
    private Instant date;
    private Double weight;
    private String project;
    private Double score;
    private String judge;
}
