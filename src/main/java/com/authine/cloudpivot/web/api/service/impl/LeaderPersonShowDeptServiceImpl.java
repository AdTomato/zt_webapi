package com.authine.cloudpivot.web.api.service.impl;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.authine.cloudpivot.web.api.dto.LeaderPersonShowDeptDto;
import com.authine.cloudpivot.web.api.mapper.LeaderPersonShowDeptMapper;
import com.authine.cloudpivot.web.api.mapper.LeadpersonMapper;
import com.authine.cloudpivot.web.api.service.LeaderPersonShowDeptService;
import com.authine.cloudpivot.web.api.service.MajorTeamAssessmentService;
import com.authine.cloudpivot.web.api.service.OrgDepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.ls.LSInput;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LeaderPersonShowDeptServiceImpl implements LeaderPersonShowDeptService {

    @Resource
    LeaderPersonShowDeptMapper leaderPersonShowDeptMapper;

    @Resource
    LeadpersonMapper leadpersonMapper;

    @Autowired
    OrgDepartmentService orgDepartmentService;

    @Override
    public List<LeaderPersonShowDeptDto> getAllLeaderPersonShowDept() {
        return leaderPersonShowDeptMapper.getAllLeaderPersonShowDept();
    }

    @Override
    public List<Map<String, Object>> getLeaderPersonShowDeptTree() {
        List<Map<String, Object>> result = new ArrayList<>();
        List<LeaderPersonShowDeptDto> allLeaderPersonShowDept = getAllLeaderPersonShowDept();
        for (LeaderPersonShowDeptDto leaderPersonShowDeptDto : allLeaderPersonShowDept) {
            Map<String, Object> data = new HashMap<>();
            data.put("label", leaderPersonShowDeptDto.getDeptName());
            data.put("id", leaderPersonShowDeptDto.getShowDept());
            List<Map<String, Object>> tree = getTree(leaderPersonShowDeptDto.getShowDept());
            if (!CollectionUtils.isEmpty(tree)) {
                data.put("children", tree);
            }
            result.add(data);
        }
        return result;
    }

    private List<Map<String, Object>> getTree(String deptId) {
        List<Map<String, Object>> result = new ArrayList<>();

        // 获取子部门
        List<Map<String, String>> lastLevelDepartment = orgDepartmentService.getLastLevelDepartment(deptId);
        if (!CollectionUtils.isEmpty(lastLevelDepartment)) {
            for (Map<String, String> department : lastLevelDepartment) {
                Map<String, Object> data = new HashMap<>();
                data.put("label", department.get("name"));
                data.put("id", department.get("id"));
                List<Map<String, Object>> children = getTree(department.get("id"));
                if (!CollectionUtils.isEmpty(children)) {
                    data.put("children", children);
                }
                result.add(data);
            }
        }
        // 获取领导人员
        List<Map<String, String>> leadpersonByDeptId = leadpersonMapper.getLeadpersonByDeptId("%" + deptId + "%");
        for (Map<String, String> leadperson : leadpersonByDeptId) {
            Map<String, Object> data = new HashMap<>();
            data.put("label", leadperson.get("lendName"));
            data.put("id", leadperson.get("id"));
            data.put("url", "https://kp.ctce.com.cn:10088/form/detail?sheetCode=LeadPerson&objectId=" + data.get("id") + "&schemaCode=LeadPerson&isWorkFlow=false&return=/application/crec4/application-list/LeadPerson?parentId=ff8080816e251d87016e35340984007f&code=LeadPerson&openMode&pcUrl&queryCode=&iframeAction=detail");
            result.add(data);
        }
        return result;
    }

}
