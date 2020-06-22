package com.authine.cloudpivot.web.api.controller;

import com.alibaba.fastjson.JSON;
import com.authine.cloudpivot.engine.api.facade.WorkflowInstanceFacade;
import com.authine.cloudpivot.engine.api.model.organization.DepartmentModel;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.engine.enums.ErrCode;
import com.authine.cloudpivot.web.api.bean.*;
import com.authine.cloudpivot.web.api.constants.Constants;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.parameter.LeadershipQualitativeSendWf;
import com.authine.cloudpivot.web.api.parameter.LeadershipQualitativeUpdate;
import com.authine.cloudpivot.web.api.service.LeadershipQualitativeService;
import com.authine.cloudpivot.web.api.utils.DataSetUtils;
import com.authine.cloudpivot.web.api.utils.RedisUtils;
import com.authine.cloudpivot.web.api.utils.UserUtils;
import com.authine.cloudpivot.web.api.utils.WorkflowUtils;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author wangyong
 * @Date:2020/3/28 11:32
 * @Description: 领导班子定性测评表控制层
 */
@RestController
@Slf4j
@RequestMapping("/ext/leadershipQualitative")
public class LeadershipQualitativeController extends BaseController {

    @Autowired
    LeadershipQualitativeService leadershipQualitativeService;

    @Autowired
    RedisUtils redisUtils;

    /**
     * @param leadershipQualitativeSendWf:
     * @Author: wangyong
     * @Date: 2020/3/28 13:48
     * @return: com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.Object>
     * @Description: 开启领导班子定性测评表考核流程
     */
    @PostMapping("/sendWf")
    public ResponseResult<Object> sendWf(@RequestBody LeadershipQualitativeSendWf leadershipQualitativeSendWf) {

        log.info("进入领导班子定性测评表开启流程接口");
        String id = leadershipQualitativeSendWf.getId();
        log.info("领导班子定性测评表开启流程id:" + id);
        List<User> users = leadershipQualitativeSendWf.getUsers();
        log.info("参与考评的评委为：" + users);
        Date date = leadershipQualitativeSendWf.getDate();
        String userId = UserUtils.getUserId(getUserId());
        UserModel userModel = this.getOrganizationFacade().getUser(userId);
        DepartmentModel departmentModel = this.getOrganizationFacade().getDepartment(userModel.getDepartmentId());
        if (StringUtil.isEmpty(id) || users == null || users.size() == 0) {
            return this.getErrResponseResult(null, 404L, "必填参数不能为空");
        }

        List<SendLeadershipQualitativeDetails> sendLeadershipQualitativeDetails = leadershipQualitativeService.getSendLeadershipQualitativeDetails(id);

        if (sendLeadershipQualitativeDetails == null || sendLeadershipQualitativeDetails.size() == 0) {
            return this.getErrResponseResult(null, 404L, "子表数据不能为空");
        }

        List<String> ids = new ArrayList<>();
        List<LeadershipQualitative> leadershipQualitatives = new ArrayList<>();
        List<LeadershipQualitativeDetails> qualitativeDetails = new ArrayList<>();
        int num = 0;
        for (User user : users) {
            LeadershipQualitative leadershipQualitative = new LeadershipQualitative();
            DataSetUtils.setBaseData(leadershipQualitative, userModel, departmentModel, "领导班子定性测评表", Constants.PROCESSING_STATUS);
            leadershipQualitative.setCommentPerson(JSON.toJSONString(Arrays.asList(user)));
            leadershipQualitative.setDate(date);
            leadershipQualitative.setUnit(id);
            ids.add(leadershipQualitative.getId());
            qualitativeDetails.addAll(setDetails(sendLeadershipQualitativeDetails, leadershipQualitative.getId()));
            leadershipQualitatives.add(leadershipQualitative);
            num++;
            if (num == 5) {
                startWf(leadershipQualitatives, qualitativeDetails, ids, this.getWorkflowInstanceFacade(), userModel, departmentModel);
                num = 0;
                leadershipQualitatives.clear();
                qualitativeDetails.clear();
                ids.clear();
            }
        }
        if (num != 0) {
            startWf(leadershipQualitatives, qualitativeDetails, ids, this.getWorkflowInstanceFacade(), userModel, departmentModel);
        }
        log.info("流程开启完毕");
        return this.getErrResponseResult(null, ErrCode.OK.getErrCode(), ErrCode.OK.getErrMsg());
    }

    /**
     * 更新领导班子定性测评表
     *
     * @param leadershipQualitativeUpdate
     * @return 更新结果
     * @author wangyong
     */
    @PutMapping("/updateLeadershipQualitative")
    public ResponseResult<Object> updateLeadershipQualitative(@RequestBody LeadershipQualitativeUpdate leadershipQualitativeUpdate) {
        String unit = leadershipQualitativeUpdate.getUnit();
        String userId = this.getUserId();
        synchronized (LeadershipQualitativeController.class) {
            if (redisUtils.hasKey(userId + "-" + unit)) {
                log.info("重复提交数据：" + leadershipQualitativeUpdate);
                return this.getErrResponseResult(null, 444L, "禁止重复提交");
            } else {
                redisUtils.set(userId + "-" + unit, 1, 30);
            }
        }
        log.info("当前提交数据：" + leadershipQualitativeUpdate);
        List<LeadershipQualitativeDetails> leadershipQualitativeDetails = leadershipQualitativeUpdate.getLeadershipQualitativeDetails();
        if (StringUtil.isEmpty(unit) || leadershipQualitativeDetails == null || leadershipQualitativeDetails.size() == 0) {
            return this.getErrResponseResult(null, 404L, "参数不能为空");
        }
        log.info("开始更新领导班子定性测评表");
        leadershipQualitativeService.updateQualitative(unit, leadershipQualitativeDetails);
        return this.getErrResponseResult(null, ErrCode.OK.getErrCode(), ErrCode.OK.getErrMsg());
    }

    @PutMapping("/updateLeadershipQualitative2")
    public ResponseResult<Object> updateLeadershipQualitative2(String id) {

        leadershipQualitativeService.updateQualitative2(id);

        return this.getErrResponseResult(null, ErrCode.OK.getErrCode(), ErrCode.OK.getErrMsg());
    }

    private void startWf(List<LeadershipQualitative> leadershipQualitatives, List<LeadershipQualitativeDetails> qualitativeDetails, List<String> ids, WorkflowInstanceFacade workflowInstanceFacade, UserModel userModel, DepartmentModel departmentModel) {
        leadershipQualitativeService.insertLeadershipQualitativeData(leadershipQualitatives, qualitativeDetails);
//        log.info("新增领导班子定性测评表主表内容");
//        leadershipQualitativeService.insertLeadershipQualitative(leadershipQualitatives);
//        log.info("新增领导班子定性测评表子表内容");
//        leadershipQualitativeService.insertLeadershipQualitativeDetails(qualitativeDetails);
        log.info("开启流程");
        WorkflowUtils.startWorkflow(workflowInstanceFacade, ids, departmentModel.getId(), userModel.getId(), Constants.LEADERSHIP_QUALITATI_WF, true);
    }

    private List<LeadershipQualitativeDetails> setDetails(List<SendLeadershipQualitativeDetails> sendLeadershipQualitativeDetails, String parentId) {
        List<LeadershipQualitativeDetails> result = new ArrayList<>();
        for (SendLeadershipQualitativeDetails qualitativeDetail : sendLeadershipQualitativeDetails) {
            LeadershipQualitativeDetails l = new LeadershipQualitativeDetails();
            l.setId(UUID.randomUUID().toString().replace("-", ""));
            l.setEvaluationItems(qualitativeDetail.getEvaluationItems());
            l.setParentId(parentId);
            l.setSortKey(qualitativeDetail.getSortKey());
            l.setPId(qualitativeDetail.getId());
            l.setGoodPoint(1);
            l.setOrdinaryPoint(0);
            l.setPreferablyPoint(0);
            l.setPoolPoint(0);
            result.add(l);
        }
        return result;
    }


}
