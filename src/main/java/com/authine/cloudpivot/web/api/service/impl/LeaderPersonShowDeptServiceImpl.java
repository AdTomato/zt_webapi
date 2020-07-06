package com.authine.cloudpivot.web.api.service.impl;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.authine.cloudpivot.web.api.dto.LeaderPersonShowDeptDto;
import com.authine.cloudpivot.web.api.mapper.LeaderPersonShowDeptMapper;
import com.authine.cloudpivot.web.api.mapper.LeadpersonMapper;
import com.authine.cloudpivot.web.api.service.LeaderPersonShowDeptService;
import com.authine.cloudpivot.web.api.service.OrgDepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            data.put("type", 0);
            List<Map<String, Object>> tree = getTree(leaderPersonShowDeptDto.getShowDept());
            if (!CollectionUtils.isEmpty(tree)) {
                data.put("children", tree);
            }
            result.add(data);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getShowDept() {
        List<Map<String, Object>> result = new ArrayList<>();
        List<String> leaderPersonShowDept = leaderPersonShowDeptMapper.getLeaderPersonShowDept();
        for (String deptId : leaderPersonShowDept) {
            Map<String, Object> data = new HashMap<>();
            Map<String, Object> deptParentId = orgDepartmentService.getDeptParentId(deptId);
            data.put("label", deptParentId.get("deptName"));
            data.put("type", 0);
            data.put("id", deptId);
            Long child = (Long) deptParentId.get("child");
            data.put("have_more", child != 0);
            result.add(data);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getChildAndLeader(String deptId) {
        List<Map<String, Object>> result = new ArrayList<>();
        List<Map<String, Object>> childDeptList = orgDepartmentService.getChildDept(deptId);
        for (Map<String, Object> childDept : childDeptList) {
            Map<String, Object> data = new HashMap<>();
            data.put("label", childDept.get("deptName"));
            data.put("type", 0);
            data.put("id", childDept.get("deptId"));
            Long child = (Long) childDept.get("child");
            data.put("have_more", child != 0);
            result.add(data);
        }
        // 获取领导人员
        List<Map<String, String>> leadpersonByDeptId = leadpersonMapper.getLeadpersonByDeptId("%" + deptId + "%");
        for (Map<String, String> leadperson : leadpersonByDeptId) {
            Map<String, Object> data = new HashMap<>();
            data.put("label", leadperson.get("lendName"));
            data.put("id", leadperson.get("id"));
            data.put("type", 1);
//            data.put("url", "https://kp.ctce.com.cn:10088/form/detail?sheetCode=LeadPerson&objectId=" + data.get("id") + "&schemaCode=LeadPerson&isWorkFlow=false&return=/application/crec4/application-list/LeadPerson?parentId=ff8080816e251d87016e35340984007f&code=LeadPerson&openMode&pcUrl&queryCode=&iframeAction=detail");
            data.put("url", "https://kp.ctce.com.cn:10088/mobile/?corpId=ding6850a95ba42812b9&agentId=312096198#/form/detail?sheetCode=LeadPerson&objectId=" + data.get("id") + "&schemaCode=LeadPerson&isWorkFlow=false&return=/apps/apps-form-list/LeadPerson");
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
                data.put("type", 0);
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
            data.put("type", 1);
//            data.put("url", "https://kp.ctce.com.cn:10088/form/detail?sheetCode=LeadPerson&objectId=" + data.get("id") + "&schemaCode=LeadPerson&isWorkFlow=false&return=/application/crec4/application-list/LeadPerson?parentId=ff8080816e251d87016e35340984007f&code=LeadPerson&openMode&pcUrl&queryCode=&iframeAction=detail");
            data.put("url", "https://kp.ctce.com.cn:10088/mobile/?corpId=ding6850a95ba42812b9&agentId=312096198#/form/detail?sheetCode=LeadPerson&objectId=" + data.get("id") + "&schemaCode=LeadPerson&isWorkFlow=false&return=/apps/apps-form-list/LeadPerson");
            result.add(data);
        }
        return result;
    }

}
