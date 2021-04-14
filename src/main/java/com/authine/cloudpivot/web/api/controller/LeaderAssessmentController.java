package com.authine.cloudpivot.web.api.controller;

import com.alibaba.fastjson.JSON;
import com.authine.cloudpivot.engine.api.facade.BizObjectFacade;
import com.authine.cloudpivot.engine.api.facade.OrganizationFacade;
import com.authine.cloudpivot.engine.api.facade.WorkflowInstanceFacade;
import com.authine.cloudpivot.engine.api.model.organization.DepartmentModel;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.engine.api.model.runtime.BizObjectModel;
import com.authine.cloudpivot.engine.enums.ErrCode;
import com.authine.cloudpivot.web.api.bean.*;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.service.CreateAssessmentResultService;
import com.authine.cloudpivot.web.api.service.impl.ILeaderAssessProxyService;
import com.authine.cloudpivot.web.api.service.ILeaderAssessService;
import com.authine.cloudpivot.web.api.utils.BatchListUtil;
import com.authine.cloudpivot.web.api.utils.CreateEvaluationTableUtils;
import com.authine.cloudpivot.web.api.utils.DataSetUtils;
import com.authine.cloudpivot.web.api.utils.RedisUtils;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;

@Api()
@RestController(value = "领导人员定性定量考核")
@RequestMapping("/ext/leadAssess")
@Slf4j
public class LeaderAssessmentController extends BaseController {
    @Autowired
    private ILeaderAssessService iLeaderAssessServie;
    @Autowired
    private ILeaderAssessProxyService iLeaderAssessProxyService;
    @Autowired
    private CreateAssessmentResultService createAssessmentResultService;

    @Autowired
    RedisUtils redisUtils;

    /**
     * 发起领导人员定量考核考核流程
     *
     * @param
     * @return
     */
    @ApiOperation(value = "发起领导人员定量考核")
    @RequestMapping("/createLeadAssessWorkflow")
    public ResponseResult<String> createAssessmentWorkflow(@RequestBody LaunchFixedQuantity launchFixedQuantity) {
        log.info("开始执行创建流程方法");
        //被考核人列表
        List<LeadPerson> assessedPeople = launchFixedQuantity.getAssessedPeople();
        long count = assessedPeople.stream().distinct().count();
        boolean isRepeat = count < assessedPeople.size();
        if (isRepeat){
            return this.getErrResponseResult("error", ErrCode.UNKNOW_ERROR.getErrCode(), "失败");
        }
        //评委列表
        List<User> judge = launchFixedQuantity.getJudge();
        //年度
        String annual = launchFixedQuantity.getAnnual();
        //公司名称
        String companyName = launchFixedQuantity.getCompanyName();
        // 创建数据的引擎类
        BizObjectFacade bizObjectFacade = super.getBizObjectFacade();

        // 有关组织机构的引擎类
        OrganizationFacade organizationFacade = super.getOrganizationFacade();

        // 创建流程的引擎类
        WorkflowInstanceFacade workflowInstanceFacade = super.getWorkflowInstanceFacade();

        // 当前用户id
        String userId = super.getUserId();
        if (userId == null) {
            userId = "2c9280a26706a73a016706a93ccf002b";
        }
        UserModel user = organizationFacade.getUser(userId);
        for (LeadPerson assessedPerson : assessedPeople) {
            BizObjectModel model = new BizObjectModel();
            model.setSchemaCode("leadfixquanass");
            Map<String, Object> data = new HashMap<>();
            // 被考核人
            data.put("assessedPerson", assessedPerson.getId());
            data.put("annual", launchFixedQuantity.getAnnual());
            // 评委
            data.put("judge", JSON.toJSONString(judge));
            data.put("companyName", launchFixedQuantity.getCompanyName());
            data.put("oldParentId", launchFixedQuantity.getParentId());
            // 将数据写入到model中
            model.put(data);

            log.info("存入数据库中的数据：" + data);

            // 创建机关部门打分表,返回领导人员打分表的id值
            String objectId = bizObjectFacade.saveBizObject(userId, model, false);
            List<LeaderEvaluationTable> leaderEvaluationTables = CreateEvaluationTableUtils.getLeaderEvaluationTable(objectId);
            iLeaderAssessServie.insertEvaluationTable(leaderEvaluationTables);
            workflowInstanceFacade.startWorkflowInstance(user.getDepartmentId(), user.getId(), "leadfixquan_wf", objectId, true);

        }
        return this.getOkResponseResult("success", "成功");
    }


    /**
     * 汇总定量考核明细得出结果，将结果回写到定量考核评价表中
     *
     * @param
     * @return
     */
    @ApiOperation(value = "计算定量考核结果")
    @RequestMapping("/countAssessmentResult")
    public ResponseResult<String> calculationAssessmentResult(@RequestBody LeadFixQuanCountInfo leadFixQuanCountInfo) {

        // 有关组织机构的引擎类
        OrganizationFacade organizationFacade = super.getOrganizationFacade();
        String userId = getUserId();
        UserModel user = organizationFacade.getUser(userId);
        synchronized (LeaderAssessmentController.class) {
            if (redisUtils.hasKey(userId + "-" + leadFixQuanCountInfo.getId())) {
                log.info("重复提交数据：" + leadFixQuanCountInfo);
                log.info("用户" + user.getName() + userId);
                return this.getErrResponseResult(null, 444L, "禁止重复提交");
            } else {
                redisUtils.set(userId + "-" + leadFixQuanCountInfo.getId(), 1, 30);
            }
        }
        log.info("当前提交定量数据：" + leadFixQuanCountInfo);
        //iLeaderAssessServie.insertFixQuanAssDetails(leadFixQuanCountInfo, user);
        log.info("用户" + user.getName() + userId);
        DepartmentModel department = organizationFacade.getDepartment(user.getDepartmentId());
        log.info("开始执行计算定量考核结果方法");
        String result = iLeaderAssessProxyService.proxyupdateFixQuanResult(user, department, leadFixQuanCountInfo);
        if ("成功".equals(result)) {
            return getOkResponseResult("success", "成功");
        } else {
            return getErrResponseResult("error", ErrCode.UNKNOW_ERROR.getErrCode(), result);
        }
    }

    /**
     * 发起领导人员定性考核考核流程
     *
     * @param
     * @return
     */
    @ApiOperation(value = "发起领导人员定性考核")
    @RequestMapping("/createLeadQualityWorkflow")
    public ResponseResult<String> createQualityAssessmentWorkflow(@RequestBody LaunchQuality launchQuality) {
        log.info("开始执行创建流程方法");
        //被考核每一行信息
        List<LaunLeadQuaCountRow> assessedPeople = launchQuality.getAssessedPeople();
        for (int i = 0; i < assessedPeople.size() - 1; i++) {
            LaunLeadQuaCountRow countRow = assessedPeople.get(i);
            for (int j = i + 1; j < assessedPeople.size(); j++) {
                if (countRow.getLeadershipName().equals(assessedPeople.get(j).getLeadershipName())) {
                    return this.getErrResponseResult("人员重复", ErrCode.UNKNOW_ERROR.getErrCode(), "失败");
                }
            }
        }
        //评委列表
        List<User> judge = launchQuality.getJudge();
        //年度
        String annual = launchQuality.getAnnual();
        //公司名称
        String companyName = launchQuality.getCompanyName();
        // 创建数据的引擎类
        BizObjectFacade bizObjectFacade = super.getBizObjectFacade();

        // 有关组织机构的引擎类
        OrganizationFacade organizationFacade = super.getOrganizationFacade();

        // 创建流程的引擎类
        WorkflowInstanceFacade workflowInstanceFacade = super.getWorkflowInstanceFacade();

        // 当前用户id
        String userId = super.getUserId();
        if (userId == null) {
            userId = "ff8080816e3e92fb016e3e9792f702de";
        }
        UserModel user = organizationFacade.getUser(userId);
        DepartmentModel department = organizationFacade.getDepartment(user.getDepartmentId());

        BigDecimal sk = new BigDecimal("10");
        String objectId = DataSetUtils.setBaseData(launchQuality, user, department, "领导人员定性考核", "PROCESSING");
        for (LaunLeadQuaCountRow assessedPerson : assessedPeople) {

            assessedPerson.setNewRowId(UUID.randomUUID().toString().replaceAll("-", ""));
            assessedPerson.setNewParentId(objectId);
            assessedPerson.setSortKey(sk);
            sk = sk.add(new BigDecimal("10"));
        }
        String judgeString = JSON.toJSONString(judge);
        launchQuality.setJudgeToString(judgeString);

        List<LaunchQuality> qualities = new ArrayList<>();
        qualities.add(launchQuality);
        if (assessedPeople.size() > 5) {
            List<List<LaunLeadQuaCountRow>> lists = BatchListUtil.averageAssign(assessedPeople, 5);
            for (int i = 0; i < lists.size(); i++) {
                if (i == 0) {
                    iLeaderAssessServie.insertLeadqualityAndChildTable(qualities, lists.get(i));

                } else {
                    iLeaderAssessServie.insertLeadqualityChildTable(lists.get(i));
                }
            }

            workflowInstanceFacade.startWorkflowInstance(user.getDepartmentId(), user.getId(), "leadquality_wf", objectId, true);


        } else {
            iLeaderAssessServie.insertLeadqualityAndChildTable(qualities, assessedPeople);
            workflowInstanceFacade.startWorkflowInstance(user.getDepartmentId(), user.getId(), "leadquality_wf", objectId, true);

        }
        return this.getOkResponseResult("success", "成功");

    }


    /**
     * 领导人员定性考核存储结果
     *
     * @param
     * @return
     */
    @ApiOperation(value = "领导人员定性考核存储票数接口")
    @RequestMapping("/saveLeadQuality")
    public ResponseResult saveLeadQuality(@RequestBody LaunchQuality launchQuality) {
        String userId = getUserId();
        String oldParentId = launchQuality.getLeaderQualityChildren().get(0).getOldParentId();
        OrganizationFacade organizationFacade = super.getOrganizationFacade();
        UserModel user = organizationFacade.getUser(userId);
        synchronized (LeaderAssessmentController.class) {
            if (redisUtils.hasKey(userId + "-" + oldParentId)) {
                log.info("重复提交数据：" + launchQuality);
                log.info("用户" + user.getName() + userId);
                return this.getErrResponseResult(null, 444L, "禁止重复提交");
            } else {
                redisUtils.set(userId + "-" + oldParentId, 1, 30);
            }
        }
        log.info("当前提交定性数据：" + launchQuality);
        log.info("用户" + user.getName() + userId);
        log.info("开始执行创建流程方法");
        BizObjectFacade bizObjectFacade = super.getBizObjectFacade();
        // 有关组织机构的引擎类
        DepartmentModel department = organizationFacade.getDepartment(user.getDepartmentId());
        iLeaderAssessServie.insertleaderQualityChildren(launchQuality, userId);
        //String responseResult = iLeaderAssessProxyService.proxycountLeadQuality(launchQuality, user, department);
        return this.getOkResponseResult("success", "成功");

    }

    @ApiOperation(value = "领导人员定性考核计算票数接口")
    @RequestMapping("/countLeadQuality")
    public ResponseResult countLeadQuality(@RequestBody LaunchQuality launchQuality) {
        String userId = getUserId();
        OrganizationFacade organizationFacade = super.getOrganizationFacade();
        UserModel user = organizationFacade.getUser(userId);
        BizObjectFacade bizObjectFacade = super.getBizObjectFacade();
        // 有关组织机构的引擎类
        DepartmentModel department = organizationFacade.getDepartment(user.getDepartmentId());
        String responseResult = iLeaderAssessProxyService.proxycountLeadQuality(launchQuality, user, department);
        if ("success".equals(responseResult)) {
            return this.getOkResponseResult(responseResult, "成功");
        } else {
            return this.getErrResponseResult(ErrCode.UNKNOW_ERROR.getErrCode(), "responseResult");
        }
    }


}
