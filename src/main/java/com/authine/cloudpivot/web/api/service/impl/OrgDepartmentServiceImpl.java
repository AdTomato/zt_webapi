package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.web.api.mapper.OrgDepartmentMapper;
import com.authine.cloudpivot.web.api.service.OrgDepartmentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class OrgDepartmentServiceImpl implements OrgDepartmentService {

    @Resource
    OrgDepartmentMapper orgDepartmentMapper;

    @Override
    public List<Map<String, String>> getLastLevelDepartment(String parentId) {
        return orgDepartmentMapper.getLastLevelDepartment(parentId);
    }

    @Override
    public Map<String, Object> getDeptParentId(String deptId) {
        return orgDepartmentMapper.getDeptParentId(deptId);
    }

    @Override
    public List<Map<String, Object>>  getChildDept(String deptId) {
        return orgDepartmentMapper.getChildDept(deptId);
    }
}
