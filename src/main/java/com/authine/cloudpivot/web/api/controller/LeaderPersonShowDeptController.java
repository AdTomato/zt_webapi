package com.authine.cloudpivot.web.api.controller;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.service.LeaderPersonShowDeptService;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ext/leaderPersonShowDept")
@Api(value = "二次开发", tags = "二次开发：领导人员显示")
public class LeaderPersonShowDeptController extends BaseController {

    @Autowired
    LeaderPersonShowDeptService leaderPersonShowDeptService;

    @ApiOperation(value = "获取领导人员部门树", httpMethod = "GET")
    @GetMapping("/getTree")
    public ResponseResult<Object> getTree() {
        List<Map<String, Object>> tree = leaderPersonShowDeptService.getLeaderPersonShowDeptTree();
        if (CollectionUtils.isEmpty(tree)) {
            this.getErrResponseResult(null, 404L, "没有获取到显示部门");
        }
        return this.getErrResponseResult(tree, 200L, "获取显示部门成功");
    }

}
