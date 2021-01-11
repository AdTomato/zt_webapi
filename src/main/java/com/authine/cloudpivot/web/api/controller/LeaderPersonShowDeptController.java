package com.authine.cloudpivot.web.api.controller;

import cn.hutool.core.util.NumberUtil;
import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.dubbo.common.utils.StringUtils;
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

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/ext/leaderPersonShowDept")
@Api(value = "二次开发", tags = "二次开发：领导人员显示")
public class LeaderPersonShowDeptController extends BaseController {

    @Autowired
    LeaderPersonShowDeptService leaderPersonShowDeptService;

    private static int compare(Map<String, Object> o1, Map<String, Object> o2) {
        String num1, num2;
        if (o1.containsKey("num")) {
            num1 = String.valueOf(o1.get("num"));
        } else {
            num1 = "999";
        }
        if (o2.containsKey("num")) {
            num2 = String.valueOf(o2.get("num"));
        } else {
            num2 = "999";
        }
        return NumberUtil.sub(num1, num2).intValue();
    }

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

        change(leaderPerson);

        return this.getErrResponseResult(leaderPerson, ErrCode.OK.getErrCode(), ErrCode.OK.getErrMsg());
    }

    /**
     * 排序
     *
     * @param tree 领导人员树
     */
    private void change(LeadShipTree tree) {
        List<Map<String, Object>> leadShipData = tree.getLeadShipData();
        if (leadShipData != null && !leadShipData.isEmpty()) {
            leadShipData.sort((LeaderPersonShowDeptController::compare));
        }
        List<LeadShipTree> child = tree.getChild();

        if ("子分公司".equals(tree.getName())) {
            List<String> companyList = Arrays.asList("一公司", "二公司", "三公司", "四公司", "五公司", "七分公司", "八分公司"
                    , "建筑公司", "电气化公司", "城轨分公司", "物资公司", "钢结构建筑公司", "市政工程公司", "路桥公司"
                    , "上海工程公司", "机电公司", "南京分公司", "工程建设分公司", "房地产公司", "投资运营公司"
                    , "试验检测与测量分公司", "设计研究院", "工程材料公司", "安徽中铁健康服务有限公司");
            List<LeadShipTree> child2 = new ArrayList<>(child.size());
            Set<Integer> indexSet = new HashSet<Integer>(child.size());
            for (int i = 0; i < companyList.size(); i++) {
                String companyName = companyList.get(i);
                for (int j = 0; j < child.size(); j++) {
                    if (child.get(j).getName().equals(companyName)) {
                        child2.add(child.get(j));
                        indexSet.add(j);
                        break;
                    }
                }
            }
            for (int i = 0; i < child.size(); i++) {
                if (!indexSet.contains(i)) {
                    child2.add(child.get(i));
                }
            }
            tree.setChild(child2);
        }
        child = tree.getChild();
        if (!child.isEmpty()) {
            for (int i = 0; i < child.size(); i++) {
                change(child.get(i));
            }
        }
    }

}
