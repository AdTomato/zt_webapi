package com.authine.cloudpivot.web.api.dto;

import lombok.Data;

import java.util.List;

/**
 * 领导人员树型结构
 *
 * @author wangyong
 * @time 2020-06-30-05-52
 */
@Data
public class LeaderPersonTreeDto {

    private String label;
    private String id;
    private String url;
    private List<LeaderPersonTreeDto> children;

}
