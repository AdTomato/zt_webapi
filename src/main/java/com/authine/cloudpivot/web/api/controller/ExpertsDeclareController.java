package com.authine.cloudpivot.web.api.controller;

import cn.hutool.core.collection.CollUtil;
import com.authine.cloudpivot.engine.api.facade.BizObjectFacade;
import com.authine.cloudpivot.engine.api.model.runtime.BizObjectModel;
import com.authine.cloudpivot.engine.enums.ErrCode;
import com.authine.cloudpivot.web.api.bean.*;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.service.ExpertsDeclareService;
import com.authine.cloudpivot.web.api.utils.Points;
import com.authine.cloudpivot.web.api.utils.UserUtils;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.ElementType;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author:lfh
 * @Date: 2020/1/13 14:01
 * @Description：专家申报控制层
 */
@RestController
@RequestMapping("/ext/expertsDeclare")
@Slf4j
public class ExpertsDeclareController extends BaseController {

    @Autowired
    private ExpertsDeclareService expertsDeclareService;

    @RequestMapping("/calculateResult")
    public ResponseResult<List> seclectAllExperts(@RequestBody ExpertsInf expertsInfo) {
        log.info("开始执行专家申报方法");
        log.info("当前传入的申报系别值为：" + expertsInfo.getDeclareDept());
        log.info("当前传入的申报级别值为：" + expertsInfo.getExpertsDeclareRank());
        log.info("当前传入的年度值为：" + expertsInfo.getAnnual());
        //获取所有的专家申报信息id
        List<ExpertsInfo> errorList = new ArrayList<>();
        if (expertsInfo.getDeclareDept() != null && expertsInfo.getExpertsDeclareRank() != null && expertsInfo.getAnnual() != null) {
            List<ExpertsSelectResult> expertsSelectResultList = expertsDeclareService.getExpertsDeclareInfo(expertsInfo);
            return this.getOkResponseResult(expertsSelectResultList, "success");
        }

        return this.getOkResponseResult(errorList, "error");
    }

    // 专家申报明细
    @RequestMapping("/insertEdDetail")
    public ResponseResult<String> calculateReviewAppointmentComments(@RequestBody List<ExpertDeclareDetail> expertDeclareDetailList) {

        //获取聘任意见信息
        List<BizObjectModel> models = new ArrayList<>();
        for (ExpertDeclareDetail expertDeclareDetail : expertDeclareDetailList) {
            BizObjectModel model = new BizObjectModel();
            model.setSchemaCode("expertDeclareDetail");
            Map<String, Object> map = new HashMap<>();
            //专家申报id
            map.put("edDetail", expertDeclareDetail.getEdDetail());
            //聘任意见主表id
            map.put("edOpinionDetail", expertDeclareDetail.getEdOpinionDetail());
            // 表决结果
            map.put("voteResult", expertDeclareDetail.getVoteResult());
            //将数据写入到model
            model.put(map);
            model.setSequenceStatus("COMPLETED");
            models.add(model);
        }
        //创建数据的引擎类
        BizObjectFacade bizObjectFacade = super.getBizObjectFacade();
        String userId = UserUtils.getUserId(getUserId());
        log.info("当前操作的用户id为" + userId);
        //使用引擎方法批量创建数据
        bizObjectFacade.addBizObjects(userId, models, "id");
        return getOkResponseResult("success", ErrCode.OK.getErrMsg());
    }

    /**
     * 计算评价结果
     *
     * @param edoId               评分表id
     * @param passPerson          通过人数
     * @param passPoll            通过票数
     * @param oexpertsDeclareRank 申报登记
     * @param num                 评委数量
     * @return {@link ResponseResult<String>}
     *///计算每个专家的结果
    @RequestMapping("/calculationExpertResult")
    public ResponseResult<String> calculationAssessmentResult(@RequestParam String edoId, @RequestParam Integer passPerson, @RequestParam Integer passPoll, @RequestParam String oexpertsDeclareRank, @RequestParam Integer num) {
        //清空每个专家的表决结果
        expertsDeclareService.clearExpertsReult(edoId);

        //判断流程状态
//        String status = expertsDeclareService.getExpertsDeclareStatus(edoId);

        List<String> peoples = expertsDeclareService.getExpertsDeclareDetailsNum(edoId);
        if (peoples.size() != num) {
            return getErrResponseResult("失败", 404L, "流程尚未结束无需计算");
        }

//        if (!SequenceStatusUtils.isCompleted(status)) {
//        }

        log.info("当前计算的专家申报意见的id值为：" + edoId);
        //获取全部的明细结果
        List<ExpertsResultDetail> expertsResultDetails = expertsDeclareService.findExpertsInfo(edoId);
        if (expertsResultDetails.size() == 0) {
            return getOkResponseResult("error", "列表为空");
        }
        //对申报专家进行初始化，计算票数
        Map<String, ExpertsDeclare> map = new HashMap<>();
        ExpertsDeclare expertsDeclare = null;
        for (ExpertsResultDetail erd : expertsResultDetails) {
            String edDetail = erd.getEdDetail();
            if (map.containsKey(edDetail)) {
                expertsDeclare = map.get(edDetail);
            } else {
                expertsDeclare = new ExpertsDeclare();
                expertsDeclare.setExpertsDeclareRank(oexpertsDeclareRank);
                expertsDeclare.setAgreePoll(0);
                expertsDeclare.setOpposePoll(0);
                expertsDeclare.setWaiverPoll(0);
                expertsDeclare.setId(edDetail);
                expertsDeclare.setPollStatus("待投票");
                map.put(edDetail, expertsDeclare);
            }
            switch (erd.getVoteResult()) {
                case Points.AGREE_POLL:
                    expertsDeclare.setAgreePoll(expertsDeclare.getAgreePoll() + 1);
                    break;
                case Points.WAIVER_POLL:
                    expertsDeclare.setWaiverPoll(expertsDeclare.getWaiverPoll() + 1);
                    break;
                case Points.OPPOSE_POLL:
                    expertsDeclare.setOpposePoll(expertsDeclare.getOpposePoll() + 1);
                    break;
            }
        }
        List<ExpertsDeclare> shouldUpdateEd = new ArrayList<>();  // 需要更新的人员
        List<ExpertsDeclare> passED = new ArrayList<>();  // 存储达到票数的人员
        List<ExpertsDeclare> failED = new ArrayList<>();  // 存储没有达到票数的人员
        for (ExpertsDeclare ed : map.values()) {
            if (ed.getAgreePoll() >= passPoll) {
                passED.add(ed);
            } else {
                failED.add(ed);
            }
        }

        // 对票数合格的人员进行处理
        // 1. 票数合格的人数小于或等于设置的通过人数：合格人数全部通过，对票数不合格的人员进行处理，一级进行降级、二级进行不通过
        // 2. 票数合格人数等于设置的通过人数：合格人数全部通过，对票数不合格的人员进行处理，一级进行降级、二级进行不通过
        // 3. 票数合格人数大于设置的通过人数：合格人数进行处理，对票数不合格的人员进行处理，一级的进行降级、二级的进行不通过

        // 对票数不合格的人员进行处理
        failED.forEach(declare -> {
            if ("一级".equals(oexpertsDeclareRank)) {
                declare.setExpertsDeclareRank("二级");
            } else {
                declare.setPollStatus("未通过");
            }
            shouldUpdateEd.add(declare);
        });
        // 对票数合格的人员进行处理
        // 合格人数小于或者等于设置的通过人数，合格人数全部通过
        if (passED.size() <= passPerson) {
            passED.forEach(declare -> {
                declare.setPollStatus("已通过");
            });
            shouldUpdateEd.addAll(passED);
        } else {
            // 合格人数大于设置的通过人数，需要判断是否存在重票的情况
            List<ExpertsDeclare> passED2 = new ArrayList<>();
            for (int i = 0; i < passED.size(); i++) {
                Integer firstAgreePoll = passED.get(i).getAgreePoll();
                Integer nextAgreePoll = passED.get(i + 1).getAgreePoll();
                if (firstAgreePoll.equals(nextAgreePoll)) {
                    // 当前同意票数和下一个人员的同意票数是一致的，需要查询出存在多少票是相同的
                    List<ExpertsDeclare> passED3 = new ArrayList<>();
                    for (int j = i + 1; j < passED.size(); j++) {
                        if (firstAgreePoll.equals(passED.get(j).getAgreePoll())) {
                            passED3.add(passED.get(j));
                        } else {
                            break;
                        }
                    }
                    // 判断相同人数 + 当前系统通过人数 是否已经超过了设置的通过人数
                    if (passED2.size() + passED3.size() > passPerson) {
                        // 已经超过了，当前通过人数不变，超过人数全部重投
                        break;
                    } else {
                        // 没有超过，将人员全部添加进更新列表中
                        passED2.addAll(passED3);
                        i += passED3.size();
                    }
                } else {
                    // 当前同意票数和下一个人员的同意票数不一致
                    // 判断合格人数是否满员
                    if (passED2.size() + 1 > passPerson) {
                        // 已经满员，当前人员通过，其余人员一级降级、二级不通过
                        for (int j = i; j < passED.size(); j++) {
                            ExpertsDeclare declare = passED.get(j);
                            if ("一级".equals(oexpertsDeclareRank)) {
                                declare.setExpertsDeclareRank("二级");
                            } else {
                                declare.setPollStatus("未通过");
                            }
                            shouldUpdateEd.add(declare);
                        }
                        break;
                    }
                    // 没有满员，继续添加
                    ExpertsDeclare declare = passED.get(i);
                    declare.setPollStatus("已通过");
                    passED2.add(declare);
                }
            }
            shouldUpdateEd.addAll(passED2);
        }
        //更新每个专家的票数
        if (CollUtil.isNotEmpty(shouldUpdateEd)) {
            expertsDeclareService.updateAllExpertsDeclare(shouldUpdateEd);
            return getOkResponseResult("success", "成功计算结果");  // 成功
        }
       return getOkResponseResult("success", "投票无效");
    }

    //获取专家参评人选基本情况
    @RequestMapping("/getExpertsInfoList")
    public ResponseResult<String> getExpertsInfoList(@RequestBody ExpertDeclareInfo expertDeclareInfo) {

        // List<ExpertsInfo> errorList = new ArrayList<>();
        //根据专家名称和单位查询一览表中是否有这个专家信息
        ExpertsInfoList expertsInfoList = expertsDeclareService.findExpertsFromInfoList(expertDeclareInfo.getExpertsDeclareName(), expertDeclareInfo.getExpertsDeclareOrganization());
        //通过专家姓名和单位在一览表没查到专家申报信息
        if (expertsInfoList == null) {
            //将专家信息添加到一览表
            //将每个专家的信息封装到专家信息一览表
            BizObjectModel model = new BizObjectModel();
            model.setSchemaCode("expertsInfoList");
            Map<String, Object> map = new HashMap<>();
            map.put("userName", expertDeclareInfo.getExpertsDeclareName());
            map.put("unit", expertDeclareInfo.getExpertsDeclareOrganization());
            map.put("gender", expertDeclareInfo.getGender());
            map.put("birth", expertDeclareInfo.getDateOfBirth());
            map.put("post", expertDeclareInfo.getTechnicalPosts());
            map.put("positionalTitles", expertDeclareInfo.getPositionalTitles());
            map.put("firstEducation", expertDeclareInfo.getFirstEducation());
            map.put("graduatesAndMajors", expertDeclareInfo.getGraduationSchool() + " " + expertDeclareInfo.getFirstMajor());
            map.put("theHighestEducationDegree", expertDeclareInfo.getOfficialAcademicCredentials() + " " + expertDeclareInfo.getHighestMajor());
            map.put("highestGraduatesAndMajors", expertDeclareInfo.getSchoolOfGraduation() + " " + expertDeclareInfo.getHighestMajor());
            map.put("nowMajorIn", expertDeclareInfo.getNowMajorIn());
            map.put("declareSessionDeptGrade", expertDeclareInfo.getAnnual() + " " + expertDeclareInfo.getDeclareDept() + " " + expertDeclareInfo.getExpertsDeclareRank());
            map.put("mainAchievements", expertDeclareInfo.getKeyPerformance());
            model.put(map);
            model.setSequenceStatus("COMPLETED");

            //创建数据的引擎类
            BizObjectFacade bizObjectFacade = super.getBizObjectFacade();
            String userId = UserUtils.getUserId(getUserId());
            log.info("当前操作的用户id为" + userId);
            //使用引擎方法批量创建数据
            String id = bizObjectFacade.saveBizObjectModel(userId, model, "id");
            //从专家申报的子表查询参评条件，可能存在多个
            List<ConditionsParticipations> conditionsParticipations = expertDeclareInfo.getConditionsParticipations();
            if (conditionsParticipations != null && !conditionsParticipations.isEmpty()) {
                for (ConditionsParticipations conditionsParticipation : conditionsParticipations) {
                    conditionsParticipation.setId(UUID.randomUUID().toString().replace("-", ""));
                    conditionsParticipation.setParentId(id);
                }
                //在一栏表中批量插入参评条件
                expertsDeclareService.insertConditions(conditionsParticipations);
            }
            return this.getOkResponseResult("success", "将专家信息添加到一览表成功");
        } else {
            return this.getOkResponseResult("error", "专家已在一览表中");
        }
    }
}
