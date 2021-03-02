package com.authine.cloudpivot.web.api.controller;

import com.alibaba.fastjson.JSON;
import com.authine.cloudpivot.engine.api.facade.WorkflowInstanceFacade;
import com.authine.cloudpivot.engine.api.model.organization.DepartmentModel;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.engine.enums.ErrCode;
import com.authine.cloudpivot.web.api.bean.*;
import com.authine.cloudpivot.web.api.constants.Constants;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.parameter.EvaluatingCadresSendWf;
import com.authine.cloudpivot.web.api.parameter.EvaluatingCadresUpdate;
import com.authine.cloudpivot.web.api.service.EvaluatingCadresService;
import com.authine.cloudpivot.web.api.utils.*;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.spi.ErrorCode;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @Author:lfh
 * @Date: 2020/1/7 10:59
 * @Description： 新选拔干部民主评议表控制层
 */
@RestController
@RequestMapping("/ext/evaluatingCadres")
@Slf4j
public class EvaluatingCadresController extends BaseController {

    @Autowired
    private EvaluatingCadresService evaluatingCadresService;

    @Autowired
    RedisUtils redisUtils;

    @PostMapping("/sendWf")
    public ResponseResult<Object> sendWf(@RequestBody EvaluatingCadresSendWf evaluatingCadresSendWf) {

        String id = evaluatingCadresSendWf.getId();
        Date date = evaluatingCadresSendWf.getDate();
        List<User> users = evaluatingCadresSendWf.getUsers();
//        List<SendEvaluatingCadreList> sendEvaluatingCadreList = evaluatingCadresSendWf.getEvaluatingCadresLists();

        if (StringUtil.isEmpty(id) || date == null || users == null || users.size() == 0) {
            return this.getErrResponseResult(null, 404L, "参数不能为空");
        }

        List<SendEvaluatingCadreList> sendEvaluatingCadreList = evaluatingCadresService.getSendEvaluatingCadreListByParentId(id);
        if (sendEvaluatingCadreList == null || sendEvaluatingCadreList.size() == 0) {
            return this.getErrResponseResult(null, 404L, "子表数据为空");
        }
        String userId = UserUtils.getUserId(getUserId());
//        userId = Points.ADMIN_ID;
        UserModel userModel = this.getOrganizationFacade().getUser(userId);
        DepartmentModel departmentModel = this.getOrganizationFacade().getDepartment(userModel.getDepartmentId());
        List<EvaluatingCadre> evaluatingCadres = new ArrayList<>();
        List<EvaluatingCadreList> evaluatingCadreLists = new ArrayList<>();
        List<String> ids = new ArrayList<>();
        int num = 0;
        for (User user : users) {
            EvaluatingCadre evaluatingCadre = new EvaluatingCadre();
            DataSetUtils.setBaseData(evaluatingCadre, userModel, departmentModel, "新选拔任用干部民主评议表", Constants.PROCESSING_STATUS);
            evaluatingCadre.setUnit(id);
            evaluatingCadre.setDate(date);
            evaluatingCadre.setCommentPerson(JSON.toJSONString(Arrays.asList(user)));
            evaluatingCadres.add(evaluatingCadre);
            evaluatingCadreLists.addAll(getEvaluatingCadresList(sendEvaluatingCadreList, evaluatingCadre.getId()));
            ids.add(evaluatingCadre.getId());
            num++;
            if (num == 10) {
                startWf(evaluatingCadres, evaluatingCadreLists, ids, this.getWorkflowInstanceFacade(), departmentModel.getId(), userModel.getId());
                evaluatingCadres.clear();
                evaluatingCadreLists.clear();
                ids.clear();
                num = 0;
            }
        }

        if (num != 0) {
            startWf(evaluatingCadres, evaluatingCadreLists, ids, this.getWorkflowInstanceFacade(), departmentModel.getId(), userModel.getId());
        }

        return this.getErrResponseResult(null, ErrCode.OK.getErrCode(), ErrCode.OK.getErrMsg());
    }

    /**
     * 更新新选拔民主干部评价表
     * @param evaluatingCadresUpdate
     * @return 更新结果
     * @author wangyong
     */
    @PutMapping("/updateEvaluatingCadre")
    public ResponseResult<Object> updateEvaluatingCadre(@RequestBody EvaluatingCadresUpdate evaluatingCadresUpdate) {
        String unit = evaluatingCadresUpdate.getUnit();
        String userId = this.getUserId();

        synchronized (EvaluatingCadresController.class) {
            if (redisUtils.hasKey(userId + "-" + unit)) {
                log.info("重复提交数据：" + evaluatingCadresUpdate);
                return this.getErrResponseResult(null, 444L, "禁止重复提交");
            } else {
                redisUtils.set(userId + "-" + unit, 1, 30);
            }
        }
        log.info("当前提交数据：" + evaluatingCadresUpdate);
        List<EvaluatingCadresList> evaluatingCadresLists = evaluatingCadresUpdate.getEvaluatingCadresLists();
        if (StringUtil.isEmpty(unit) || evaluatingCadresLists == null || evaluatingCadresLists.size() == 0) {
            return this.getErrResponseResult(null, 404L, "参数不能为空");
        }
        evaluatingCadresService.updateEvaluatingCadre(unit, evaluatingCadresLists);
        return this.getErrResponseResult(null, ErrCode.OK.getErrCode(), ErrCode.OK.getErrMsg());
    }

    @PutMapping("/updateEvaluatingCadre2")
    public ResponseResult<Object> updateEvaluatingCadre(String id) {
        evaluatingCadresService.updateEvaluatingCadre2(id);
        return this.getErrResponseResult(null, ErrCode.OK.getErrCode(), ErrCode.OK.getErrMsg());
    }

    private void startWf(List<EvaluatingCadre> evaluatingCadres, List<EvaluatingCadreList> evaluatingCadreLists, List<String> ids, WorkflowInstanceFacade workflowInstanceFacade, String departmentId, String userId) {
        evaluatingCadresService.insertEvaluatingCadreData(evaluatingCadres, evaluatingCadreLists);
        log.info("开启流程");
        WorkflowUtils.startWorkflow(workflowInstanceFacade, ids, departmentId, userId, Constants.EVALUATING_CADRE_WF, true);
        log.info("流程开启完毕");

    }

    private List<EvaluatingCadreList> getEvaluatingCadresList(List<SendEvaluatingCadreList> sendEvaluatingCadreList, String parentId) {
        List<EvaluatingCadreList> result = new ArrayList<>();
        for (SendEvaluatingCadreList evaluatingCadresList : sendEvaluatingCadreList) {
            EvaluatingCadreList evaluatingCadreList = new EvaluatingCadreList();
            evaluatingCadreList.setId(UUID.randomUUID().toString().replace("-", ""));
            evaluatingCadreList.setParentId(parentId);
            evaluatingCadreList.setSortKey(evaluatingCadresList.getSortKey());
            evaluatingCadreList.setUserName(evaluatingCadresList.getUserName());
            evaluatingCadreList.setGender(evaluatingCadresList.getGender());
            evaluatingCadreList.setDateOfBirth(evaluatingCadresList.getDateOfBirth());
            evaluatingCadreList.setRawDuty(evaluatingCadresList.getRawDuty());
            evaluatingCadreList.setCashDuty(evaluatingCadresList.getCashDuty());
            evaluatingCadreList.setPromotionDate(evaluatingCadresList.getPromotionDate());
            evaluatingCadreList.setSatisfiedPoll(0);
            evaluatingCadreList.setBasicSatisfiedPoll(0);
            evaluatingCadreList.setNoSatisfiedPoll(0);
            evaluatingCadreList.setNoUnderstandPoll(0);
            evaluatingCadreList.setExistencePoll(0);
            evaluatingCadreList.setNoExistencePoll(0);
            evaluatingCadreList.setINoUnderstandPoll(0);
            evaluatingCadreList.setPId(evaluatingCadresList.getId());
            result.add(evaluatingCadreList);
        }
        return result;
    }


    @RequestMapping("/calculateResoult")
    public ResponseResult<Void> calculateResult(@RequestParam("id") String id) {
        log.info("开始执行新选拔干部民主评议表方法");
        log.info("当前传入的id值为：" + id);
        //获取发起新选拔干部民主评议表的全部信息
        EvaluatingCadres ec = evaluatingCadresService.getEvaluatingCadresInfo(id);
        Map map = new HashMap();
        map.put("id", ec.getId());
        map.put("max", ec.getParticipantsPeoples());
        map.put("createdTime", ec.getCreatedTime());
        //根据unit获取从0到最大投票人数的新选拔干部民主评议表的id
        List<String> idList = evaluatingCadresService.getEvaluatingCadresIdByUnit(map);
        log.info("获取的是0-" + ec.getParticipantsPeoples() + "的新选拔干部民主评议表信息");
        log.info("获取的id列表是" + idList);

        //计算评测干部表
        calculateEvaluatingCadresList(idList, id);
        //更新发起新选拔干部民主评议表主表结果
        Map<String, Object> info = new HashMap<>();
        info.put("votoPeoples", idList.size());
        info.put("id", id);
        evaluatingCadresService.updateEvaluatingCadresInfo(info);
        log.info("新选拔干部民主评议表票结果计算完毕");
        return getOkResponseResult("计算完毕");
    }

    /**
     * 计算评测干部表
     *
     * @param idList
     * @param id
     */
    private void calculateEvaluatingCadresList(List<String> idList, String id) {
        log.info("开始计算新选拔干部民主评议表中评测干部表");

        //获取全部的发起新选拔干部民主评议表的 评测干部表信息
        List<SEvaluatingCadresList> sec = evaluatingCadresService.getAllSEvaluatingCadresListData(id);

        //获取全部的新选拔干部民主评议表的 评测干部表信息
        List<EvaluatingCadresList> ec = evaluatingCadresService.getAllEvaluatingCadresListData(id);

        Map<String, SEvaluatingCadresList> secMap = new HashMap<>();
        //初始化评测干部信息表
        for (SEvaluatingCadresList secList : sec) {
            secList.setSatisfiedPoll(0);
            secList.setBasicSatisfiedPoll(0);
            secList.setNoSatisfiedPoll(0);
            secList.setNoUnderstandPoll(0);
            secList.setExistencePoll(0);
            secList.setNoExistencePoll(0);
            secList.setINoUnderstandPoll(0);
            secMap.put(secList.getUserName() + secList.getDateOfBirth() + secList.getRawDuty() + secList.getCashDuty(), secList);
        }
        //计算给个干部的票数
        for (EvaluatingCadresList ecList : ec) {
            if (idList.contains(ecList.getParentId())) {
                SEvaluatingCadresList secList = secMap.get(ecList.getUserName() + ecList.getDateOfBirth() + ecList.getRawDuty() + ecList.getCashDuty());
                if (null != secList) {
                    switch (ecList.getPerspective()) {
                        case "满意":
                            secList.setSatisfiedPoll(secList.getSatisfiedPoll() + 1);
                            break;
                        case "基本满意":
                            secList.setBasicSatisfiedPoll(secList.getBasicSatisfiedPoll() + 1);
                            break;
                        case "不满意":
                            secList.setNoSatisfiedPoll(secList.getNoSatisfiedPoll() + 1);
                            break;
                        case "不了解":
                            secList.setNoUnderstandPoll(secList.getNoUnderstandPoll() + 1);
                            break;
                    }

                    switch (ecList.getIfThereIsA()) {
                        case "不存在":
                            secList.setNoExistencePoll(secList.getNoExistencePoll() + 1);
                            break;
                        case "存在":
                            secList.setExistencePoll(secList.getExistencePoll() + 1);
                            break;
                        case "不了解":
                            secList.setINoUnderstandPoll(secList.getINoUnderstandPoll() + 1);
                            break;
                    }
                }
            }
        }

        //更新最终结果
        evaluatingCadresService.updateAllEvaluatingCadres(sec);
        log.info("计算新选拔干部民主评议表中评测干部表完成");

    }
}
