package com.authine.cloudpivot.web.api.controller;

import com.alibaba.fastjson.JSON;
import com.authine.cloudpivot.engine.api.facade.BizObjectFacade;
import com.authine.cloudpivot.engine.api.facade.OrganizationFacade;
import com.authine.cloudpivot.engine.api.facade.WorkflowInstanceFacade;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.engine.api.model.runtime.BizObjectModel;
import com.authine.cloudpivot.web.api.bean.*;
import com.authine.cloudpivot.web.api.bean.deptSeasonAssess.DeptSeasonAssChild;
import com.authine.cloudpivot.web.api.bean.deptSeasonAssess.LaunchDeptSeasonRequest;
import com.authine.cloudpivot.web.api.bean.deptSeasonAssess.SubmitDeptSeasonRequest;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.service.IAssessmentDetail;
import com.authine.cloudpivot.web.api.service.SeasonAssessService;
import com.authine.cloudpivot.web.api.utils.CreateEvaluationTableUtils;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import jodd.util.StringUtil;
import org.dom4j.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;

/**
 * 部门季度考核 基础部门15个人打分
 *
 * @author zsh
 * @since 2019-12-2
 */
@RestController
@RequestMapping("/ext/seasonbasic")
public class SeasonBasicController extends BaseController {
    private Logger log = LoggerFactory.getLogger(SeasonBasicController.class);

    @Autowired
     SeasonAssessService seasonAssessService;

    @Autowired
    IAssessmentDetail assessmentDetail;

    @RequestMapping("/startDeptPerformEva")
    public ResponseResult<String> startDeptPerformEva(@RequestBody LaunchDeptSeasonRequest launchDeptSeasonRequest) {
        // 创建数据的引擎类
        BizObjectFacade bizObjectFacade = super.getBizObjectFacade();
        // 有关组织机构的引擎类
        OrganizationFacade organizationFacade = super.getOrganizationFacade();
        // 创建流程的引擎类
        WorkflowInstanceFacade workflowInstanceFacade = super.getWorkflowInstanceFacade();
        try {
        // 当前用户id
        String userId = super.getUserId();
        if (userId == null) {
            userId = "2c9280a26706a73a016706a93ccf002b";
        }
        UserModel user = organizationFacade.getUser(userId);

        for (DeptEffect deptEffect : launchDeptSeasonRequest.getDeptEffectList()) {
            BizObjectModel model = new BizObjectModel();
            model.setSchemaCode("dept_perform_eva");
            Map<String, Object> data = new HashMap<>();
            data.put("assessment_year",launchDeptSeasonRequest.getAssessmentYear());
            data.put("assessment_season",launchDeptSeasonRequest.getAssessmentSeason());
            data.put("dept", JSON.toJSONString(deptEffect.getDeptNameList()));
            data.put("basic_selector",  JSON.toJSONString(launchDeptSeasonRequest.getBasicSelector()));
            data.put("handle_selector",  JSON.toJSONString(launchDeptSeasonRequest.getHandleSelector()));
            data.put("partyhandle_selector",  JSON.toJSONString(launchDeptSeasonRequest.getPartyhandleSelector()));
            data.put("old_parent_id",launchDeptSeasonRequest.getOldParentId());
            // 将数据写入到model中
            model.put(data);
            log.info("存入数据库中的数据：" + data);
            // 创建机关部门打分表,返回领导人员打分表的id值
            String objectId = bizObjectFacade.saveBizObject(userId, model, false);
            List<DeptSeasonAssChild> deptSeasonAssTables = CreateEvaluationTableUtils.getDeptSeasonAssTables(objectId);
            seasonAssessService.insertDeptSeasonAssTables(deptSeasonAssTables);
            workflowInstanceFacade.startWorkflowInstance(user.getDepartmentId(), user.getId(), "dept_perform_evawf", objectId, true);

        }
        } catch (Exception e) {
            log.error("错误信息", e);
            return getErrResponseResult(null, 404L, e.getMessage());
        }
        return this.getOkResponseResult("ok", "成功");
    }

    @RequestMapping("/submitDeptSeasonBasic")
    public ResponseResult<String> submitDeptSeasonBasic(@RequestBody SubmitDeptSeasonRequest  submitDeptSeasonRequest) {
        try {
            String userId = this.getUserId();
            if (userId == null){
                userId = "2c9280a26706a73a016706a93ccf002b";
            }
        if ("Activity3".equals(submitDeptSeasonRequest.getActivityCode()) ){
            //储存基层打分分数

            // 有关组织机构的引擎类
            OrganizationFacade organizationFacade = super.getOrganizationFacade();
            submitDeptSeasonRequest.setDeptNameId(submitDeptSeasonRequest.getDeptName().get(0).getId());
            submitDeptSeasonRequest.setUserId(userId);
            submitDeptSeasonRequest.setDeptNameToString(JSON.toJSONString(submitDeptSeasonRequest.getDeptName()));
            seasonAssessService.insertDeptSeasonEvaInfo(submitDeptSeasonRequest);
            return this.getOkResponseResult("success", "存储基层分数成功");

        }
        if ("Activity5".equals(submitDeptSeasonRequest.getActivityCode())){
            //计算基层打分平均分,填回发起表
            // 行政督查督办分数填回发起表
            submitDeptSeasonRequest.setDeptNameToString(JSON.toJSONString(submitDeptSeasonRequest.getDeptName()));
            seasonAssessService.updateBasicAvg(submitDeptSeasonRequest);
            seasonAssessService.updateHandleScore(submitDeptSeasonRequest);
            return this.getOkResponseResult("success", "计算基层平均分,行政督察分数回写");

        }
        if ("Activity6".equals(submitDeptSeasonRequest.getActivityCode())){
            //党委督查督办分数填回发起表
            //算总分
            submitDeptSeasonRequest.setDeptNameToString(JSON.toJSONString(submitDeptSeasonRequest.getDeptName()));
            seasonAssessService.updatePartyHandleScore(submitDeptSeasonRequest);

            int year = submitDeptSeasonRequest.getAssessmentYear();
            String val = year + "";
            String oldParentId = submitDeptSeasonRequest.getOldParentId();
            String season = submitDeptSeasonRequest.getAssessmentSeason();
            log.info("年度:" + year);
            log.info("季度:" + season);
            log.info("id:" + oldParentId);

            TotalScore totalScore = seasonAssessService.countDeptTotal(submitDeptSeasonRequest);
            String assessmentId = assessmentDetail.getAssessmentIdByAnnual(year + "年度");
            if (assessmentId == null) {
                log.info("创建年度考核得分汇总表");
                assessmentId = insertAssessmentDetail(year + "年度", userId);
            }
            seasonAssessService.updateTotal(totalScore);
            if (assessmentId != null) {
                AssessmentSummaryDetail assessmentSummaryDetail = assessmentDetail.getAssessmentDetailByParentIdAndDepartment(assessmentId, totalScore.getDepartment());
                if (assessmentSummaryDetail == null) {
                    // 不存在，创建
                    log.info("创建年度考核得分汇总表明细");
                    insertAssessmentSummaryDetail(assessmentId, totalScore.getDepartment(), season, totalScore.getScore());
                } else {
                    // 存在更新
                    log.info("更新年度考核得分汇总表明细");
                    updateAssessmentSummaryDetail(assessmentSummaryDetail, season, totalScore.getScore());
                }
            }
            return this.getOkResponseResult("success", "党委督察分数回写计算总分");

        }
        } catch (Exception e) {
            log.error("异常",e);
            return this.getOkResponseResult("error", "计算总分出错");
        }
        return this.getOkResponseResult("error", "计算总分出错");

    }



    @RequestMapping("/savescore")
    public ResponseResult<String> savescore(@RequestBody SaveScoreSubmitVO saveScoreSubmit) {
        String userId = this.getUserId();
        String seasonassessmentId = saveScoreSubmit.getSeasonassessmentId();
        List<DeptEffect> deptEffectList = saveScoreSubmit.getDeptEffectList();
//        List<String> depteffectIds = new ArrayList<>();
        log.info("执行：checkRepeat");
        log.info("存储的数据为:" + saveScoreSubmit);
        int num = seasonAssessService.checkRepeat(seasonassessmentId, userId);
        if (num != 0) {
            return this.getOkResponseResult("error", "重复储存分数");
        }
        List<VoteInfo> voteInfos = new ArrayList<>();
        try {

            for (int i = 0; i < deptEffectList.size(); i++) {
                if (null == deptEffectList.get(i).getId() || "".equals(deptEffectList.get(i).getId())) {
                    continue;
                }
                VoteInfo voteInfo = new VoteInfo();
                voteInfo.setAssessmentSeason(saveScoreSubmit.getAssessmentSeason());
                voteInfo.setAssessmentYear(saveScoreSubmit.getAssessmentYear());
                voteInfo.setUserId(userId);
                voteInfo.setDeptNameId(deptEffectList.get(i).getDeptNameList().get(0).getId());
                voteInfo.setBasicScore(deptEffectList.get(i).getBasicScore());
                voteInfo.setHandleScore(deptEffectList.get(i).getHandleScore());
                voteInfo.setWorkareaDeduct(deptEffectList.get(i).getWorkareaDeduct());
                voteInfo.setOfficialcontentDeduct(deptEffectList.get(i).getOfficialcontentDeduct());
                voteInfo.setHandleDeduct(deptEffectList.get(i).getHandleDeduct());
                voteInfo.setTotalScore(deptEffectList.get(i).getTotalScore());
                voteInfo.setDeptEffectId(deptEffectList.get(i).getId());
                voteInfo.setParentId(seasonassessmentId);
//                depteffectIds.add(i, deptEffectList.get(i).getId());
                voteInfos.add(voteInfo);
//                seasonAssessService.saveScore(voteInfo);
            }
            seasonAssessService.saveScore(voteInfos);
            seasonAssessService.resetScore(seasonassessmentId);
            log.info("存储分数成功,当前存储分数的userId为:" + userId);
            return this.getOkResponseResult("success", "存储分数成功");

        } catch (Exception e) {
            log.error(e.getMessage());
            return this.getOkResponseResult("error", "存储分数出错");
        }

    }

    @RequestMapping("/countavg")
    public ResponseResult<String> countavg(@RequestParam(required = false) String id, String instanceid) {

        try {

            log.info("执行：countavg方法");
            log.info("参数id：" + id);
            List<AvgScore> results = seasonAssessService.countAvg(id);
            if (results.size() == 0) {
                return this.getOkResponseResult("error", "计算平均数出错");
            }

            log.info("删除基层打分全部已办：deleteWorkitemfinished");
            seasonAssessService.deleteWorkitemfinished(instanceid);

            for (int i = 0; i < results.size(); i++) {
                AvgScore avgScore = results.get(i);
                seasonAssessService.updateAvg(avgScore);
            }
            return this.getOkResponseResult("success", "计算和存储平均数成功");

        } catch (Exception e) {
            log.error("exception",e);
            return this.getOkResponseResult("error", "存储平均数出错");
        }
    }

    @RequestMapping("/counttotal")
    public ResponseResult<String> counttotal(@RequestParam(required = false) BigDecimal year, @RequestParam(required = false) String season, @RequestParam(required = false) String id) {
        String val = year + "";
        log.info("年度:" + year);
        log.info("季度:" + season);
        log.info("id:" + id);
        if ("".equals(val)) {
            return this.getOkResponseResult("error", "年份为空");
        }
        if ("".equals(season)) {
            return this.getOkResponseResult("error", "季度为空");
        }
        try {
            List<TotalScore> results = seasonAssessService.countTotal(id);
            String assessmentId = assessmentDetail.getAssessmentIdByAnnual(year + "年度");
            ;
            if (results.size() == 0 || null == results.get(0)) {
                return this.getOkResponseResult("error", "返回results为空");
            }

            if (assessmentId == null) {
                log.info("创建年度考核得分汇总表");
                assessmentId = insertAssessmentDetail(year + "年度", getUserId());
            }

            for (int i = 0; i < results.size(); i++) {
                TotalScore totalScore = results.get(i);
                seasonAssessService.updateTotal(totalScore);
                if (assessmentId != null) {
                    AssessmentSummaryDetail assessmentSummaryDetail = assessmentDetail.getAssessmentDetailByParentIdAndDepartment(assessmentId, totalScore.getDepartment());
                    if (assessmentSummaryDetail == null) {
                        // 不存在，创建
                        log.info("创建年度考核得分汇总表明细");
                        insertAssessmentSummaryDetail(assessmentId, totalScore.getDepartment(), season, totalScore.getScore());
                    } else {
                        // 存在更新
                        log.info("更新年度考核得分汇总表明细");
                        updateAssessmentSummaryDetail(assessmentSummaryDetail, season, totalScore.getScore());
                    }
                }
            }
            return this.getOkResponseResult("success", "计算和存储总分成功");

        } catch (Exception e) {
            log.error("异常",e);
            return this.getOkResponseResult("error", "计算总分出错");
        }
    }

    /**
     * 创建年度考核得分汇总表
     *
     * @param annual 年度
     * @param userId 用户
     * @return 汇总表id
     */
    private String insertAssessmentDetail(String annual, String userId) {
        BizObjectFacade facade = super.getBizObjectFacade();
        BizObjectModel model = new BizObjectModel();
        model.setSchemaCode("assessment_summary");
        Map<String, Object> map = new HashMap<>();
        map.put("annual", annual);
        model.put(map);
        model.setSequenceStatus("COMPLETED");
        return facade.saveBizObjectModel(userId, model, "id");
    }

    /**
     * 创建机关部门年度考核得分明细
     *
     * @param assessmentId 部门id
     * @param season       季度
     * @param totalScore   分数
     */
    private void insertAssessmentSummaryDetail(String assessmentId, String department, String season, BigDecimal totalScore) {
        AssessmentSummaryDetail assessmentSummaryDetail = new AssessmentSummaryDetail();
        assessmentSummaryDetail.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        assessmentSummaryDetail.setParentId(assessmentId);
        assessmentSummaryDetail.setDepartment(department);
        switch (season) {
            case "第一季度": {
                assessmentSummaryDetail.setFirstQuarter(totalScore.doubleValue());
                assessmentSummaryDetail.setSecondQuarter(0D);
                assessmentSummaryDetail.setThirdQuarter(0D);
                assessmentSummaryDetail.setFourQuarter(0D);
            }
            break;
            case "第二季度": {
                assessmentSummaryDetail.setFirstQuarter(0D);
                assessmentSummaryDetail.setSecondQuarter(totalScore.doubleValue());
                assessmentSummaryDetail.setThirdQuarter(0D);
                assessmentSummaryDetail.setFourQuarter(0D);
            }
            break;
            case "第三季度": {
                assessmentSummaryDetail.setFirstQuarter(0D);
                assessmentSummaryDetail.setSecondQuarter(0D);
                assessmentSummaryDetail.setThirdQuarter(totalScore.doubleValue());
                assessmentSummaryDetail.setFourQuarter(0D);
            }
            break;
            case "第四季度": {
                assessmentSummaryDetail.setFirstQuarter(0D);
                assessmentSummaryDetail.setSecondQuarter(0D);
                assessmentSummaryDetail.setThirdQuarter(0D);
                assessmentSummaryDetail.setFourQuarter(totalScore.doubleValue());
            }
            break;
        }
        assessmentSummaryDetail.setAnnualEvaluation(0D);
        double v = totalScore.divide(new BigDecimal("4"), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("0.6")).doubleValue();
        assessmentSummaryDetail.setAnnualScore(v);

        log.info("创建的明细：" + assessmentSummaryDetail);
        assessmentDetail.insertAssessmentSummaryDetail(assessmentSummaryDetail);
    }

    /**
     * 更新年度机关考核得分汇总表明细
     *
     * @param assessmentSummaryDetail 机关部门考核得分明细表
     * @param season                  季度
     * @param totalScore              分数
     */
    private void updateAssessmentSummaryDetail(AssessmentSummaryDetail assessmentSummaryDetail, String season, BigDecimal totalScore) {
        Double score = 0D;
        switch (season) {
            case "第一季度":
                assessmentSummaryDetail.setFirstQuarter(totalScore.doubleValue());
                break;
            case "第二季度":
                assessmentSummaryDetail.setSecondQuarter(totalScore.doubleValue());
                break;
            case "第三季度":
                assessmentSummaryDetail.setThirdQuarter(totalScore.doubleValue());
                break;
            case "第四季度":
                assessmentSummaryDetail.setFourQuarter(totalScore.doubleValue());
                break;
        }
        Double seasonTotalScore = 0D;
        seasonTotalScore = (assessmentSummaryDetail.getFirstQuarter() +
                assessmentSummaryDetail.getSecondQuarter() +
                assessmentSummaryDetail.getThirdQuarter() +
                assessmentSummaryDetail.getFourQuarter()) / 4;
//        BigDecimal add = new BigDecimal(assessmentSummaryDetail.getFirstQuarter().doubleValue())
//                .add(new BigDecimal(assessmentSummaryDetail.getSecondQuarter().doubleValue()))
//                .add(new BigDecimal(assessmentSummaryDetail.getThirdQuarter()))
//                .add(new BigDecimal(assessmentSummaryDetail.getFourQuarter()));
//        BigDecimal decimal1 = add.divide(new BigDecimal("4"), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("0.6"));
//        BigDecimal decimal2 = new BigDecimal(assessmentSummaryDetail.getAnnualEvaluation()).multiply(new BigDecimal("0.4"));
//        double v = decimal1.add(decimal2).doubleValue();
//        assessmentSummaryDetail.setAnnualScore(v);

        assessmentSummaryDetail.setAnnualScore(seasonTotalScore * 0.6 + assessmentSummaryDetail.getAnnualEvaluation() * 0.4);
        assessmentDetail.updateAssessmentDetailById(assessmentSummaryDetail);
    }

}
