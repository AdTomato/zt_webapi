package com.authine.cloudpivot.web.api.service;


import java.util.List;
import java.util.Map;

public interface OrgDepartmentService {

    List<Map<String, String>> getLastLevelDepartment(String parentId);

}
