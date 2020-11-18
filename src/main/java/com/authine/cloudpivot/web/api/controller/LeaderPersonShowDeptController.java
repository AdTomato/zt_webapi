package com.authine.cloudpivot.web.api.controller;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.authine.cloudpivot.engine.enums.ErrCode;
import com.authine.cloudpivot.web.api.bean.leadership.LeadShipTree;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.service.LeaderPersonShowDeptService;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
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

    @ApiOperation(value = "获取所有需要显示的领导人员部门", httpMethod = "GET")
    @GetMapping("/getShowDept")
    public ResponseResult<Object> getShowDept() {

        List<Map<String, Object>> showDept = leaderPersonShowDeptService.getShowDept();
        if (CollectionUtils.isEmpty(showDept)) {
            return this.getErrResponseResult(null, 404L, "没有获取到显示部门");
        }
        return this.getErrResponseResult(showDept, 200L, "获取显示部门成功");
    }

    @ApiOperation(value = "根据部门id获取子部门以及领导人员", httpMethod = "GET")
    @GetMapping("/getChildAndLeader")
    public ResponseResult<Object> getChildAndLeader(@RequestParam String deptId) {
        List<Map<String, Object>> childAndLeader = leaderPersonShowDeptService.getChildAndLeader(deptId);
        if (CollectionUtils.isEmpty(childAndLeader)) {
            return this.getErrResponseResult(null, 404L, "没有获取数据");
        }
        return this.getErrResponseResult(childAndLeader, 200L, "获取部门数据成功");
    }

    @GetMapping("/getShowLeadShip")
    public ResponseResult<Object> getShowLeadShip() {
        LeadShipTree leaderPerson = leaderPersonShowDeptService.getLeaderPerson();
        List<String> leaderPersonShowDept = leaderPersonShowDeptService.getLeaderPersonShowDept();
        List<LeadShipTree> newChild = new ArrayList<>();
        List<LeadShipTree> oldChild = leaderPerson.getChild();
        List<Integer> countRecord = new ArrayList<>();
        for (int i = 0; i < leaderPersonShowDept.size(); i++) {
            String deptId = leaderPersonShowDept.get(i);
            for (int j = 0; j < oldChild.size(); j++) {
                if (oldChild.get(j).getId().equals(deptId)) {
                    newChild.add(oldChild.get(j));
                    countRecord.add(j);
                }
            }
        }

        for (int i = 0; i < oldChild.size(); i++) {
            if (!countRecord.contains(i)) {
                newChild.add(oldChild.get(i));
            }
        }

        leaderPerson.setChild(newChild);

        return this.getErrResponseResult(leaderPerson, ErrCode.OK.getErrCode(), ErrCode.OK.getErrMsg());
    }

}
