package com.authine.cloudpivot.web.api.mapper;

import com.authine.cloudpivot.web.api.bean.LeaderPersonShowDept;
import com.authine.cloudpivot.web.api.dto.LeaderPersonShowDeptDto;

import java.util.List;
import java.util.Map;

/**
 * 领导人员显示mapper
 *
 * @author wangyong
 * @time 2020-06-30-10-03
 */
public interface LeaderPersonShowDeptMapper {

    List<LeaderPersonShowDeptDto> getAllLeaderPersonShowDept();

}
