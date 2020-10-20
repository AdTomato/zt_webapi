package com.authine.cloudpivot.web.api.mapper;

import java.util.List;
import java.util.Map;

public interface OrgDepartmentMapper {

    List<Map<String, String>> getLastLevelDepartment(String parentId);

    Map<String, Object> getDeptParentId(String deptId);

    List<Map<String, Object>>  getChildDept(String deptId);

    Map<String, String> getDepartmentInfo(String deptId);

}
