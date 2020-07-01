package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.web.api.bean.MajorTeamAssessment;
import com.authine.cloudpivot.web.api.dto.LeaderPersonShowDeptDto;
import org.omg.CORBA.OBJ_ADAPTER;

import java.util.List;
import java.util.Map;

/**
 * 领导人员部门显示
 *
 * @author wangyong
 * @time 2020-06-30-11-24
 */
public interface LeaderPersonShowDeptService {

    List<LeaderPersonShowDeptDto> getAllLeaderPersonShowDept();

    List<Map<String, Object>> getLeaderPersonShowDeptTree();

    List<Map<String, Object>> getShowDept();

    List<Map<String, Object>> getChildAndLeader(String deptId);

}
