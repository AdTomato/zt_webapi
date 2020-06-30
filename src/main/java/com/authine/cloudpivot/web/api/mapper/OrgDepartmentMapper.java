package com.authine.cloudpivot.web.api.mapper;

import java.util.List;
import java.util.Map;

public interface OrgDepartmentMapper {

    List<Map<String, String>> getLastLevelDepartment(String parentId);

}
