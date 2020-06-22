package com.authine.cloudpivot.web.api.controller;

import com.alibaba.fastjson.JSON;
import com.authine.cloudpivot.engine.api.model.organization.DepartmentModel;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.engine.enums.ErrCode;
import com.authine.cloudpivot.web.api.bean.DemocraticEvaluation;
import com.authine.cloudpivot.web.api.bean.GDemocraticEvaluation;
import com.authine.cloudpivot.web.api.bean.SDemocraticEvaluation;
import com.authine.cloudpivot.web.api.bean.User;
import com.authine.cloudpivot.web.api.constants.Constants;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.parameter.DemocraticEvaluationSendWf;
import com.authine.cloudpivot.web.api.parameter.DemocraticEvaluationUpdate;
import com.authine.cloudpivot.web.api.service.DemocraticEvaluationService;
import com.authine.cloudpivot.web.api.utils.*;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @Author: wangyong
 * @Date: 2020-01-05 09:33
 * @Description: 工作民主评议表controller类
 */
@RestController
@RequestMapping("/ext/democraticEvaluation")
@Slf4j
public class DemocraticEvaluationController extends BaseController {

    @Autowired
    DemocraticEvaluationService democraticEvaluationService;

    @Autowired
    RedisUtils redisUtils;

    /**
     * @param id : 发起民主评议表的id值
     * @return : com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.Void>
     * @Author: wangyong
     * @Date: 2020/1/5 15:18
     * @Description: 计算民主评议表结果
     */
    @GetMapping("/calculateResult")
    public ResponseResult<Void> calculateResult(String id) {
        log.info("开始计算民主评议表结果");
        log.info("当前计算的民主评议表的id为:" + id);
        if ("".equals(id)) {
            return getErrResponseResult(ErrCode.SYS_PARAMETER_EMPTY.getErrCode(), ErrCode.SYS_PARAMETER_EMPTY.getErrMsg());
        }
        SDemocraticEvaluation sd = democraticEvaluationService.getSDemocraticEvaluationDataById(id);
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("max", sd.getParticipantsPeoples());
        List<DemocraticEvaluation> dList = democraticEvaluationService.getAllDemocraticEvaluationData(map);

        // 设初值
        init(sd);

        if (null != dList && 0 != dList.size()) {
            for (DemocraticEvaluation d :
                    dList) {
                switch (d.getGeneralEvaluation()) {
                    case Points.SATISFIED_POINT:
                        sd.setGSatisfiedPoll(sd.getGSatisfiedPoll() + 1);
                        break;
                    case Points.BASIC_SATISFIED_POINT:
                        sd.setGBasicSatisfiedPoll(sd.getGBasicSatisfiedPoll() + 1);
                        break;
                    case Points.NO_SATISFIED_POINT:
                        sd.setGNoSatisfiedPoll(sd.getGNoSatisfiedPoll() + 1);
                        break;
                    case Points.NO_UNDERSTAND_POINT:
                        sd.setGNoUnderstandPoll(sd.getGNoUnderstandPoll() + 1);
                        break;
                }

                switch (d.getRegulationLawsOpinion()) {
                    case Points.SATISFIED_POINT:
                        sd.setRSatisfiedPoll(sd.getRSatisfiedPoll() + 1);
                        break;
                    case Points.BASIC_SATISFIED_POINT:
                        sd.setRBasicSatisfiedPoll(sd.getRBasicSatisfiedPoll() + 1);
                        break;
                    case Points.NO_SATISFIED_POINT:
                        sd.setRNoSatisfiedPoll(sd.getRNoSatisfiedPoll() + 1);
                        break;
                    case Points.NO_UNDERSTAND_POINT:
                        sd.setRNoUnderstandPoll(sd.getRNoUnderstandPoll() + 1);
                        break;
                }

                switch (d.getBadPractiveOpinion()) {
                    case Points.SATISFIED_POINT:
                        sd.setBSatisfiedPoll(sd.getBSatisfiedPoll() + 1);
                        break;
                    case Points.BASIC_SATISFIED_POINT:
                        sd.setBBasicSatisfiedPoll(sd.getBBasicSatisfiedPoll() + 1);
                        break;
                    case Points.NO_SATISFIED_POINT:
                        sd.setBNoSatisfiedPoll(sd.getBNoSatisfiedPoll() + 1);
                        break;
                    case Points.NO_UNDERSTAND_POINT:
                        sd.setBNoUnderstandPoll(sd.getBNoUnderstandPoll() + 1);
                        break;
                }

                switch (d.getInstitutionalReformOpinion()) {
                    case Points.SATISFIED_POINT:
                        sd.setISatisfiedPoll(sd.getISatisfiedPoll() + 1);
                        break;
                    case Points.BASIC_SATISFIED_POINT:
                        sd.setIBasicSatisfiedPoll(sd.getIBasicSatisfiedPoll() + 1);
                        break;
                    case Points.NO_SATISFIED_POINT:
                        sd.setINoSatisfiedPoll(sd.getINoSatisfiedPoll() + 1);
                        break;
                    case Points.NO_UNDERSTAND_POINT:
                        sd.setINoUnderstandPoll(sd.getINoUnderstandPoll() + 1);
                        break;
                }

                if (d.getProminentProblem().contains(Points.NO_STRICT_POINT)) {
                    sd.setNoStrictPoll(sd.getNoStrictPoll() + 1);
                }
                if (d.getProminentProblem().contains(Points.APPOINT_PEOPLE_POINT)) {
                    sd.setAppointPeoplePoll(sd.getAppointPeoplePoll() + 1);
                }
                if (d.getProminentProblem().contains(Points.INDIVIDUAL_POINT)) {
                    sd.setIndividualPoll(sd.getIndividualPoll() + 1);
                }
                if (d.getProminentProblem().contains(Points.RUN_OFFICE_POINT)) {
                    sd.setRunOfficePoll(sd.getRunOfficePoll() + 1);
                }
                if (d.getProminentProblem().contains(Points.BUY_SELL_OFFICE_POINT)) {
                    sd.setBuySellOfficePoll(sd.getBuySellOfficePoll() + 1);
                }
                if (d.getProminentProblem().contains(Points.CANVASSING_POINT)) {
                    sd.setCanvassingPoll(sd.getCanvassingPoll() + 1);
                }
                if (d.getProminentProblem().contains(Points.NO_OUTSTANDING_PROBLEMS_POINT)) {
                    sd.setNoOutstandingProblemsPoll(sd.getNoOutstandingProblemsPoll() + 1);
                }
                if (!"".equals(d.getConcreteContent())) {
                    if ("".equals(sd.getOtherProblems())) {
                        sd.setOtherProblems(d.getConcreteContent());
                    } else {
                        sd.setOtherProblems(sd.getOtherProblems() + "\n\n" + d.getConcreteContent());
                    }
                }
                if (!"".equals(d.getCommentSuggestion())) {
                    if ("".equals(sd.getCommentSuggestion())) {
                        sd.setCommentSuggestion(d.getCommentSuggestion());
                    } else {
                        sd.setCommentSuggestion(sd.getCommentSuggestion() + "\n\n" + d.getCommentSuggestion());
                    }
                }
            }
        }
        sd.setVotePeoples(dList.size());
        sd.setIsOrNotCloseVote("是");
        democraticEvaluationService.updateSDemocraticEvaluation(sd);
        log.info("民主评议表结果计算成功");
        return getErrResponseResult(ErrCode.OK.getErrCode(), ErrCode.OK.getErrMsg());
    }

    /**
     * @Author: wangyong
     * @Date: 2020/3/27 0:18
     * @param democraticEvaluationSendWf: 发起民主评议表人员数据
     * @return: com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.Object>
     * @Description: 发起民主评议表
     */
    @PostMapping("/sendDemocraticWf")
    public ResponseResult<Object> sendDemocraticWf(@RequestBody DemocraticEvaluationSendWf democraticEvaluationSendWf) {

        log.info("发起民主评议表流程");

        String id = democraticEvaluationSendWf.getId();
        log.info("id:" + id);
        Date date = democraticEvaluationSendWf.getDate();
        log.info("date:" + date);
        List<User> users = democraticEvaluationSendWf.getUsers();
        log.info("users:" + users);
        if (StringUtil.isEmpty(id) || date == null || users == null || users.size() == 0) {
            log.info("参数为空");
            return this.getErrResponseResult(null, 404L, "参数为空");
        }

        String userId = UserUtils.getUserId(getUserId());
//        userId = Points.ADMIN_ID;
        UserModel userModel = this.getOrganizationFacade().getUserById(userId);
        DepartmentModel departmentModel = this.getOrganizationFacade().getDepartment(userModel.getDepartmentId());
        List<GDemocraticEvaluation> democraticEvaluations = new ArrayList<>();
        int num = 0;
        List<String> ids = new ArrayList<>();
        for (User user : users) {
            GDemocraticEvaluation democraticEvaluation = new GDemocraticEvaluation();
            DataSetUtils.setBaseData(democraticEvaluation, userModel, departmentModel, "工作民主评议表", Constants.PROCESSING_STATUS);

            democraticEvaluation.setUnit(id);
            democraticEvaluation.setDate(date);
            democraticEvaluation.setCommentPerson(JSON.toJSONString(Arrays.asList(user)));

            democraticEvaluation.setGSatisfiedPoll(1);
            democraticEvaluation.setGBasicSatisfiedPoll(0);
            democraticEvaluation.setGNoSatisfiedPoll(0);
            democraticEvaluation.setGNoUnderstandPoll(0);

            democraticEvaluation.setRSatisfiedPoll(1);
            democraticEvaluation.setRBasicSatisfiedPoll(0);
            democraticEvaluation.setRNoSatisfiedPoll(0);
            democraticEvaluation.setRNoUnderstandPoll(0);

            democraticEvaluation.setBSatisfiedPoll(1);
            democraticEvaluation.setBBasicSatisfiedPoll(0);
            democraticEvaluation.setBNoSatisfiedPoll(0);
            democraticEvaluation.setBNoUnderstandPoll(0);

            democraticEvaluation.setISatisfiedPoll(1);
            democraticEvaluation.setIBasicSatisfiedPoll(0);
            democraticEvaluation.setINoUnderstandPoll(0);
            democraticEvaluation.setINoSatisfiedPoll(0);

            democraticEvaluations.add(democraticEvaluation);
            ids.add(democraticEvaluation.getId());
            num++;
            if (num == 500) {
                startWorkflow(democraticEvaluations, ids, userModel.getDepartmentId(), userModel.getId(), Constants.DEMOCRATIC_EVALUATION_WF);
                ids.clear();
                democraticEvaluations.clear();
                num = 0;
            }
        }

        if (num != 0) {
            startWorkflow(democraticEvaluations, ids, userModel.getDepartmentId(), userModel.getId(), Constants.DEMOCRATIC_EVALUATION_WF);
        }

        return this.getErrResponseResult(null, ErrCode.OK.getErrCode(), ErrCode.OK.getErrMsg());
    }

    @PutMapping("/updateDemocratic")
    public ResponseResult<Object> updateDemocratic(@RequestBody DemocraticEvaluationUpdate democraticEvaluationUpdate) {
        log.info("开始更新发起工作民主评议表数据");
        String id = democraticEvaluationUpdate.getUnit();
        String userId = this.getUserId();
        if (StringUtil.isEmpty(id)) {
            return this.getErrResponseResult(null, 404L, "参数不能为空");
        }

        synchronized (DemocraticEvaluationController.class) {
            if (redisUtils.hasKey(userId + "-" + id)) {
                log.info("重复提交数据：" + democraticEvaluationUpdate);
                return this.getErrResponseResult(null, 444L, "禁止重复提交");
            } else {
                redisUtils.set(userId + "-" + id, 1, 30);
            }
        }
        log.info("当前提交数据：" + democraticEvaluationUpdate);
        democraticEvaluationService.updateDemocratic(democraticEvaluationUpdate);
        return this.getErrResponseResult(null, ErrCode.OK.getErrCode(), ErrCode.OK.getErrMsg());
    }

    @PutMapping("/updateDemocratic2")
    public ResponseResult<Object> updateDemocratic2(String id) {
        democraticEvaluationService.updateDemocratic(id);
        return this.getErrResponseResult(null, ErrCode.OK.getErrCode(), ErrCode.OK.getErrMsg());
    }

    /**
     * @Author: wangyong
     * @Date: 2020/3/27 11:05
     * @param democraticEvaluations:
     * @param ids:
     * @param departmentId:
     * @param userId:
     * @param workflowCode:
     * @return: void
     * @Description: 用于创建数据以及开启流程
     */
    private void startWorkflow(List<GDemocraticEvaluation> democraticEvaluations, List<String> ids, String departmentId, String userId, String workflowCode) {
        log.info("创建工作民主评议表数据");
        democraticEvaluationService.insertDemocraticEvaluation(democraticEvaluations);
        log.info("发起工作民主评议表流程");
        WorkflowUtils.startWorkflow(this.getWorkflowInstanceFacade(), ids, departmentId, userId, Constants.DEMOCRATIC_EVALUATION_WF,true);
        log.info("工作民主评议表流程发起完毕");
    }



    /**
     * @param sd : 发起民主评议表
     * @return : void
     * @Author: wangyong
     * @Date: 2020/1/5 13:59
     * @Description: 初始化发起民主评议表
     */
    private void init(SDemocraticEvaluation sd) {

        sd.setGBasicSatisfiedPoll(0);
        sd.setGNoSatisfiedPoll(0);
        sd.setGNoUnderstandPoll(0);
        sd.setGSatisfiedPoll(0);

        sd.setRBasicSatisfiedPoll(0);
        sd.setRNoSatisfiedPoll(0);
        sd.setRNoUnderstandPoll(0);
        sd.setRSatisfiedPoll(0);

        sd.setBSatisfiedPoll(0);
        sd.setBNoUnderstandPoll(0);
        sd.setBBasicSatisfiedPoll(0);
        sd.setBNoSatisfiedPoll(0);

        sd.setIBasicSatisfiedPoll(0);
        sd.setINoSatisfiedPoll(0);
        sd.setINoUnderstandPoll(0);
        sd.setISatisfiedPoll(0);

        sd.setNoStrictPoll(0);
        sd.setAppointPeoplePoll(0);
        sd.setIndividualPoll(0);
        sd.setRunOfficePoll(0);
        sd.setBuySellOfficePoll(0);
        sd.setCanvassingPoll(0);
        sd.setNoOutstandingProblemsPoll(0);
        sd.setNoOutstandingProblemsPoll(0);
        sd.setOtherProblems("");
        sd.setCommentSuggestion("");
    }

}
