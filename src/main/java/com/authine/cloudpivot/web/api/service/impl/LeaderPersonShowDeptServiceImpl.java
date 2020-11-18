package com.authine.cloudpivot.web.api.service.impl;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.authine.cloudpivot.web.api.bean.leadership.LeadShipTree;
import com.authine.cloudpivot.web.api.dto.LeaderPersonShowDeptDto;
import com.authine.cloudpivot.web.api.mapper.LeaderPersonShowDeptMapper;
import com.authine.cloudpivot.web.api.mapper.LeadpersonMapper;
import com.authine.cloudpivot.web.api.service.LeaderPersonShowDeptService;
import com.authine.cloudpivot.web.api.service.OrgDepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

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

    @Override
    public List<Map<String, String>> getAllLeaderPerson() {
        return leaderPersonShowDeptMapper.getAllLeaderPerson();
    }

    @Override
    public LeadShipTree getLeaderPerson() {
        List<Map<String, String>> allLeaderPerson = getAllLeaderPerson();
        Map<String, List<Map<String, String>>> departmentMap = new ConcurrentHashMap<>();
        allLeaderPerson.forEach(leaderPerson -> {
            String departmentStrList = leaderPerson.get("department");
            if (!StringUtils.isBlank(departmentStrList)) {
                JSONArray departmentArray = JSON.parseArray(departmentStrList);
                departmentArray.forEach(obj -> {
                    JSONObject object = (JSONObject) obj;
                    if (departmentMap.keySet().contains(((JSONObject) obj).get("id"))) {
                        // 包含
                        departmentMap.get(object.get("id")).add(leaderPerson);
                    } else {
                        // 不包含
                        List<Map<String, String>> arr = new ArrayList<>();
                        arr.add(leaderPerson);
                        departmentMap.put((String) object.get("id"), arr);
                    }
                });
            }
        });

        AtomicReference<LeadShipTree> root = new AtomicReference<>();

        Map<String, LeadShipTree> idToTree = new ConcurrentHashMap<>();
        departmentMap.keySet().forEach(departmentId -> {
            if (idToTree.containsKey(departmentId)) {
                // 之前轮询已经查找过的部门id
                if (idToTree.get(departmentId).getLeadShipData() == null) {
                    idToTree.get(departmentId).setLeadShipData(new ArrayList<>());
                }
                idToTree.get(departmentId).getLeadShipData().addAll(departmentMap.get(departmentId));
            } else {
                // 之前没有查找的部门id
                Map<String, String> departmentInfo = orgDepartmentService.getDepartmentInfo(departmentId);
                if (departmentInfo == null) {
                    return;
                }
                // 判断父id是否为空，如果为空代表代表该人员是根部门的领导
                String parentId = departmentInfo.containsKey("parentId") ? departmentInfo.get("parentId") : "";

                if (StringUtils.isBlank(parentId)) {
                    // 为空，该人员为根部门的领导
                    if (root.get() == null) {
                        root.set(new LeadShipTree());
                        LeadShipTree tree = root.get();
                        tree.setId(departmentInfo.get("id"));
                        tree.setName(departmentInfo.get("name"));
                        tree.setLeadShipData(new ArrayList<>());
                        tree.getLeadShipData().addAll(departmentMap.get(departmentId));
                        idToTree.put(root.get().getId(), root.get());
                    } else {
                        root.get().getLeadShipData().addAll(departmentMap.get(departmentId));
                    }
                } else {
                    // 不为空，代表上层还有部门，需要查询上层的部门
                    LeadShipTree tree = new LeadShipTree();
                    tree.setId(departmentInfo.get("id"));
                    tree.setName(departmentInfo.get("name"));
                    tree.setLeadShipData(new ArrayList<>());
                    tree.getLeadShipData().addAll(departmentMap.get(departmentId));
                    tree.setChild(new ArrayList<>());
                    idToTree.put(tree.getId(), tree);
                    // 判断父id是否被查询过了，如果查询过了，直接插入
                    if (idToTree.containsKey(parentId)) {
                        idToTree.get(parentId).getChild().add(tree);
                    } else {
                        while (true) {
                            // 循环查询
                            departmentInfo = orgDepartmentService.getDepartmentInfo(departmentInfo.get("parentId"));
                            if (departmentInfo == null) {
                                return;
                            }

                            String parentId2 = departmentInfo.containsKey("parentId") ? departmentInfo.get("parentId") : "";

                            // 判断父id是否是空，如果为空，则代表当前查询出来的部门是根部门，上一个查询结果为根部门子节点
                            if (StringUtils.isBlank(parentId2)) {
                                if (root.get() == null) {
                                    root.set(new LeadShipTree());
                                    root.get().setId(departmentInfo.get("id"));
                                    root.get().setName(departmentInfo.get("name"));
                                    root.get().setChild(new ArrayList<>());
                                    root.get().setLeadShipData(new ArrayList<>());
                                }
                                root.get().getChild().add(tree);
                                idToTree.put(root.get().getId(), root.get());
                                break;
                            } else {
                                // 如果父id不为空，则证明上面还有父部门
                                LeadShipTree tree2 = new LeadShipTree();
                                tree2.setId(departmentInfo.get("id"));
                                tree2.setName(departmentInfo.get("name"));
                                tree2.setChild(new ArrayList<>());
                                tree2.getChild().add(tree);
                                idToTree.put(tree2.getId(), tree2);
                                // 判断父是否已经查询过了
                                if (idToTree.containsKey(parentId2)) {
                                    // 已经查询过了
                                    idToTree.get(parentId2).getChild().add(tree2);
                                    break;
                                } else {
                                    // 没有查询过，查询父部门
                                    tree = tree2;

                                }
                            }
                        }
                    }
                }
            }
        });
        return root.get();
    }

    @Override
    public List<String> getLeaderPersonShowDept() {
        return leaderPersonShowDeptMapper.getLeaderPersonShowDept();
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
