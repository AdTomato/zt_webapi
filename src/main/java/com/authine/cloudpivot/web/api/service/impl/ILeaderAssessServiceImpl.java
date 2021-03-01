package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.engine.api.model.organization.DepartmentModel;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.web.api.bean.*;
import com.authine.cloudpivot.web.api.bean.AssessmentDetail;
import com.authine.cloudpivot.web.api.mapper.ILeaderAssessMapper;
import com.authine.cloudpivot.web.api.service.CreateAssessmentResultService;
import com.authine.cloudpivot.web.api.service.ILeaderAssessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ILeaderAssessServiceImpl implements ILeaderAssessService {
    @Autowired
    private ILeaderAssessMapper leaderAssessMapper;

    @Autowired
    private CreateAssessmentResultService createAssessmentResultService;

    @Override
    public void insertEvaluationTable(List<LeaderEvaluationTable> leaderEvaluationTables) {
        leaderAssessMapper.insertTable(leaderEvaluationTables);
    }

    @Override
    public void cleanAssessmentScore(String deartment_assessment) {
        leaderAssessMapper.cleanAssessmentScore(deartment_assessment);
    }

    @Override
    public List<ADComprehensiveAssessment> getADComprehensiveAssessmentByParentId(String objectId) {
        return leaderAssessMapper.getADComprehensiveAssessmentByParentId(objectId);
    }

    @Override
    public BigDecimal getAssessmentDetailResultScore(String parentId, String assessmentProject) {
        return leaderAssessMapper.getAssessmentDetailResultScore(parentId, assessmentProject);
    }


    @Override
    public void insertLeadqualityAndChildTable(List<LaunchQuality> qualities, List<LaunLeadQuaCountRow> list) {
        leaderAssessMapper.insertLeadQuality(qualities);
        leaderAssessMapper.insertleadqualitychildTable(list);
    }

    @Override
    public void insertLeadqualityChildTable(List<LaunLeadQuaCountRow> launLeadQuaCountRows) {
        leaderAssessMapper.insertleadqualitychildTable(launLeadQuaCountRows);

    }




    /**
     * 计算定性考核结果
     * @param launchQuality
     * @param user
     * @param department
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String countLeadQuality(LaunchQuality launchQuality, UserModel user, DepartmentModel department) {
        //获取定性考核评分表所有子表数据
        List<LeaderQualityChild> leaderQualityChildren = launchQuality.getLeaderQualityChildren();
        //发起定性考核评分表的主表Id
        String oldParentId = launchQuality.getLeaderQualityChildren().get(0).getOldParentId();
//        //用发起表的parentId去查询发起表子表的每行数据
//        List<LaunLeadQuaCountRow> launLeadQuaCountRows = leaderAssessMapper.selectLaunchiLeaderqualityCount(oldParentId);
//        for (int i = 0; i < launLeadQuaCountRows.size(); i++) {
//            String leadershipName = launLeadQuaCountRows.get(i).getLeadershipName();
//            String leadershipDuty = launLeadQuaCountRows.get(i).getLeadershipDuty();
//            for (int j = 0; j < leaderQualityChildren.size(); j++) {
//                String leadershipName1 = leaderQualityChildren.get(j).getLeadershipName();
//                String leadershipDuty1 = leaderQualityChildren.get(j).getLeadershipDuty();
//                if (leadershipName.equals(leadershipName1) && leadershipDuty.equals(leadershipDuty1)) {
//                    leaderQualityChildren.get(j).setOldRowId(launLeadQuaCountRows.get(i).getId());
//                }
//            }
//        }


        for (LeaderQualityChild child : leaderQualityChildren) {
            LaunLeadQuaCountRow countRow =   leaderAssessMapper.selectqualitydetails(child);
            countRow.setLeadershipName(child.getLeadershipName());
            countRow.setParentId(oldParentId);
            leaderAssessMapper.updateRow(countRow);
            AssessmentResult ar = new AssessmentResult();
            String leadershipName = countRow.getLeadershipName();
            BigDecimal excellentPollVotes = countRow.getExcellentPoll();
            BigDecimal competentPollVotes = countRow.getCompetentPoll();
            BigDecimal basicCompetentPollVotes = countRow.getBasicCompetentPoll();
            BigDecimal notCompetentPollVotes = countRow.getNotCompetentPoll();
            String result = "优秀" + excellentPollVotes.stripTrailingZeros().toPlainString() + "票" + "\n"
                    + "称职" + competentPollVotes.stripTrailingZeros().toPlainString() + "票" + "\n"
                    + "基本称职" + basicCompetentPollVotes.stripTrailingZeros().toPlainString() + "票" + "\n"
                    + "不称职" + notCompetentPollVotes.stripTrailingZeros().toPlainString() + "票" + "\n";
            ar.setLeadershipPerson(leadershipName);
            ar.setAssessContent("领导人员定性考核");
            ar.setAssessResult(result);
            ar.setPId(oldParentId);
            ar.setAssessTime(new Date());
            List<AssessmentResult> arList = new ArrayList<>();
            arList.add(ar);
            createAssessmentResultService.updateOrInsertAssessmentResultByModel(user, department, arList);

















//            if ("是".equals(child.getExcellentPoll()) && "否".equals(child.getCompetentPoll()) && "否".equals(child.getBasicCompetentPoll()) && "否".equals(child.getNotCompetentPoll())) {
//                String oldRowId = child.getOldRowId();
//                LaunLeadQuaCountRow countRow = leaderAssessMapper.selectRowByrowId(oldRowId);
//                BigDecimal excellentPoll = countRow.getExcellentPoll();
//                excellentPoll = excellentPoll.add(new BigDecimal("1"));
//                countRow.setExcellentPoll(excellentPoll);
//                leaderAssessMapper.updateRow(countRow);
//                AssessmentResult ar = new AssessmentResult();
//                String leadershipName = countRow.getLeadershipName();
//                BigDecimal excellentPollVotes = countRow.getExcellentPoll();
//                BigDecimal competentPollVotes = countRow.getCompetentPoll();
//                BigDecimal basicCompetentPollVotes = countRow.getBasicCompetentPoll();
//                BigDecimal notCompetentPollVotes = countRow.getNotCompetentPoll();
//                String result = "优秀" + excellentPollVotes.stripTrailingZeros().toPlainString() + "票" + "\n"
//                        + "称职" + competentPollVotes.stripTrailingZeros().toPlainString() + "票" + "\n"
//                        + "基本称职" + basicCompetentPollVotes.stripTrailingZeros().toPlainString() + "票" + "\n"
//                        + "不称职" + notCompetentPollVotes.stripTrailingZeros().toPlainString() + "票" + "\n";
//                ar.setLeadershipPerson(leadershipName);
//                ar.setAssessContent("领导人员定性考核");
//                ar.setAssessResult(result);
//                ar.setPId(oldParentId);
//                ar.setAssessTime(new Date());
//                List<AssessmentResult> arList = new ArrayList<>();
//                arList.add(ar);
//                createAssessmentResultService.updateOrInsertAssessmentResultByModel(user, department, arList);
//
//            }
//            if ("否".equals(child.getExcellentPoll()) && "是".equals(child.getCompetentPoll()) && "否".equals(child.getBasicCompetentPoll()) && "否".equals(child.getNotCompetentPoll())) {
//                String oldRowId = child.getOldRowId();
//                LaunLeadQuaCountRow countRow = leaderAssessMapper.selectRowByrowId(oldRowId);
//                BigDecimal competentPoll = countRow.getCompetentPoll();
//                competentPoll = competentPoll.add(new BigDecimal("1"));
//                countRow.setCompetentPoll(competentPoll);
//                leaderAssessMapper.updateRow(countRow);
//                AssessmentResult ar = new AssessmentResult();
//                String leadershipName = countRow.getLeadershipName();
//                BigDecimal excellentPollVotes = countRow.getExcellentPoll();
//                BigDecimal competentPollVotes = countRow.getCompetentPoll();
//                BigDecimal basicCompetentPollVotes = countRow.getBasicCompetentPoll();
//                BigDecimal notCompetentPollVotes = countRow.getNotCompetentPoll();
//                String result = "优秀" + excellentPollVotes.stripTrailingZeros().toPlainString() + "票" + "\n"
//                        + "称职" + competentPollVotes.stripTrailingZeros().toPlainString() + "票" + "\n"
//                        + "基本称职" + basicCompetentPollVotes.stripTrailingZeros().toPlainString() + "票" + "\n"
//                        + "不称职" + notCompetentPollVotes.stripTrailingZeros().toPlainString() + "票" + "\n";
//                ar.setLeadershipPerson(leadershipName);
//                ar.setAssessContent("领导人员定性考核");
//                ar.setAssessResult(result);
//                ar.setPId(oldParentId);
//                ar.setAssessTime(new Date());
//                List<AssessmentResult> arList = new ArrayList<>();
//                arList.add(ar);
//                createAssessmentResultService.updateOrInsertAssessmentResultByModel(user, department, arList);
//
//            }
//            if ("否".equals(child.getExcellentPoll()) && "否".equals(child.getCompetentPoll()) && "是".equals(child.getBasicCompetentPoll()) && "否".equals(child.getNotCompetentPoll())) {
//                String oldRowId = child.getOldRowId();
//                LaunLeadQuaCountRow countRow = leaderAssessMapper.selectRowByrowId(oldRowId);
//                BigDecimal basicCompetentPoll = countRow.getBasicCompetentPoll();
//                basicCompetentPoll = basicCompetentPoll.add(new BigDecimal("1"));
//                countRow.setBasicCompetentPoll(basicCompetentPoll);
//                leaderAssessMapper.updateRow(countRow);
//                AssessmentResult ar = new AssessmentResult();
//                String leadershipName = countRow.getLeadershipName();
//                BigDecimal excellentPollVotes = countRow.getExcellentPoll();
//                BigDecimal competentPollVotes = countRow.getCompetentPoll();
//                BigDecimal basicCompetentPollVotes = countRow.getBasicCompetentPoll();
//                BigDecimal notCompetentPollVotes = countRow.getNotCompetentPoll();
//                String result = "优秀" + excellentPollVotes.stripTrailingZeros().toPlainString() + "票" + "\n"
//                        + "称职" + competentPollVotes.stripTrailingZeros().toPlainString() + "票" + "\n"
//                        + "基本称职" + basicCompetentPollVotes.stripTrailingZeros().toPlainString() + "票" + "\n"
//                        + "不称职" + notCompetentPollVotes.stripTrailingZeros().toPlainString() + "票" + "\n";
//                ar.setLeadershipPerson(leadershipName);
//                ar.setAssessContent("领导人员定性考核");
//                ar.setAssessResult(result);
//                ar.setPId(oldParentId);
//                ar.setAssessTime(new Date());
//                List<AssessmentResult> arList = new ArrayList<>();
//                arList.add(ar);
//                createAssessmentResultService.updateOrInsertAssessmentResultByModel(user, department, arList);
//
//            }
//            if ("否".equals(child.getExcellentPoll()) && "否".equals(child.getCompetentPoll()) && "否".equals(child.getBasicCompetentPoll()) && "是".equals(child.getNotCompetentPoll())) {
//                String oldRowId = child.getOldRowId();
//                LaunLeadQuaCountRow countRow = leaderAssessMapper.selectRowByrowId(oldRowId);
//                BigDecimal notCompetentPoll = countRow.getNotCompetentPoll();
//                notCompetentPoll = notCompetentPoll.add(new BigDecimal("1"));
//                countRow.setNotCompetentPoll(notCompetentPoll);
//                leaderAssessMapper.updateRow(countRow);
//                AssessmentResult ar = new AssessmentResult();
//                String leadershipName = countRow.getLeadershipName();
//                BigDecimal excellentPollVotes = countRow.getExcellentPoll();
//                BigDecimal competentPollVotes = countRow.getCompetentPoll();
//                BigDecimal basicCompetentPollVotes = countRow.getBasicCompetentPoll();
//                BigDecimal notCompetentPollVotes = countRow.getNotCompetentPoll();
//                String result = "优秀" + excellentPollVotes.stripTrailingZeros().toPlainString() + "票" + "\n"
//                        + "称职" + competentPollVotes.stripTrailingZeros().toPlainString() + "票" + "\n"
//                        + "基本称职" + basicCompetentPollVotes.stripTrailingZeros().toPlainString() + "票" + "\n"
//                        + "不称职" + notCompetentPollVotes.stripTrailingZeros().toPlainString() + "票" + "\n";
//                ar.setLeadershipPerson(leadershipName);
//                ar.setAssessContent("领导人员定性考核");
//                ar.setAssessResult(result);
//                ar.setPId(oldParentId);
//                ar.setAssessTime(new Date());
//                List<AssessmentResult> arList = new ArrayList<>();
//                arList.add(ar);
//                createAssessmentResultService.updateOrInsertAssessmentResultByModel(user, department, arList);

        }


        return "success";
    }

    @Override
    public void updateAssessmentResultScoreList(List<FinalTotalResult> lists) {
        leaderAssessMapper.updateAssessmentResultScoreList(lists);
    }



    /**
     * 更新定量考核分数
     *
     * @param
     * @param user
     * @param department
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String updateFixQuanResult(UserModel user, DepartmentModel department, LeadFixQuanCountInfo leadFixQuanCountInfo) {
        log.info("当前计算的id值为：" + leadFixQuanCountInfo.getId());
        //id为空
        if (leadFixQuanCountInfo.getId() == null || leadFixQuanCountInfo.getId().isEmpty()) {
            return "传入的id值为空";
        }

        String objectId = leadFixQuanCountInfo.getId().replaceAll("\"", "");
        objectId = objectId.replaceAll("=", "");
        List<AssessmentDetail> list = leadFixQuanCountInfo.getAdList();
        log.info("当前的列表为：" + leadFixQuanCountInfo.getAdList());
        if (list.size() == 0) {
            return "没有计算数据";
        }

        for (AssessmentDetail assessmentDetail : list) {
            FixQuanAssDetails fixQuanAssDetails = new FixQuanAssDetails();
            fixQuanAssDetails.setLeadPersonId(leadFixQuanCountInfo.getLeadPersonId());
            fixQuanAssDetails.setParentId(leadFixQuanCountInfo.getId());
            fixQuanAssDetails.setOldParentId(leadFixQuanCountInfo.getOldParentId());
            fixQuanAssDetails.setUserId(user.getUserId());
            fixQuanAssDetails.setAnnual(leadFixQuanCountInfo.getAnnual());
            fixQuanAssDetails.setCompanyName(leadFixQuanCountInfo.getCompanyName());
            fixQuanAssDetails.setAssessmentProject(assessmentDetail.getAssessment_project());
            fixQuanAssDetails.setScore(new BigDecimal(Double.toString(assessmentDetail.getScore())));
            leaderAssessMapper.insertFixQuanAssDetails(fixQuanAssDetails);

        }
        AssessmentResult ar = new AssessmentResult();
        String assResult = new String();
        Date dateTime = new Date();
        ar.setLeadershipPerson(leadFixQuanCountInfo.getLeadPersonId());
        ar.setAssessTime(dateTime);
        ar.setPId(leadFixQuanCountInfo.getOldParentId());
        ar.setAssessContent("领导人员定量考核");
        LaunchFixedQuantityRow row = new LaunchFixedQuantityRow();
        row.setAssessedName(leadFixQuanCountInfo.getLeadPersonId());
        String oldParentId = leadFixQuanCountInfo.getOldParentId();
        FixQuanAssDetails fixQuanAssDetails = new FixQuanAssDetails();
        fixQuanAssDetails.setLeadPersonId(leadFixQuanCountInfo.getLeadPersonId());
        fixQuanAssDetails.setOldParentId(oldParentId);
        fixQuanAssDetails.setParentId(leadFixQuanCountInfo.getId());
        fixQuanAssDetails.setAnnual(leadFixQuanCountInfo.getAnnual());
        fixQuanAssDetails.setCompanyName(leadFixQuanCountInfo.getCompanyName());
        List<FixQuanAssDetails> selectfixquanassdetails = leaderAssessMapper.selectfixquanassdetails(fixQuanAssDetails);
        int num = leaderAssessMapper.selectfixquanassdetailsrow(fixQuanAssDetails);
        for (FixQuanAssDetails selectfixquanassdetail : selectfixquanassdetails) {
            switch (selectfixquanassdetail.getAssessmentProject()) {
                case "政治素质": {
                    row.setPolitical_quality(selectfixquanassdetail.getScore());
                    assResult = assResult + selectfixquanassdetail.getAssessmentProject() + selectfixquanassdetail.getScore().toPlainString() + "分" + "\n";
                }
                break;
                case "职业操守": {
                    row.setOccupation_ethics(selectfixquanassdetail.getScore());
                    assResult = assResult + selectfixquanassdetail.getAssessmentProject() + selectfixquanassdetail.getScore().toPlainString() + "分" + "\n";
                }
                break;
                case "作风建设": {
                    row.setStyle_construction(selectfixquanassdetail.getScore());
                    assResult = assResult + selectfixquanassdetail.getAssessmentProject() + selectfixquanassdetail.getScore().toPlainString() + "分" + "\n";
                }
                break;
                case "廉洁从业": {
                    row.setHonest_business(selectfixquanassdetail.getScore());
                    assResult = assResult + selectfixquanassdetail.getAssessmentProject() + selectfixquanassdetail.getScore().toPlainString() + "分" + "\n";

                }
                break;
                case "科学决策能力": {
                    row.setSci_dec_abi(selectfixquanassdetail.getScore());
                    assResult = assResult + selectfixquanassdetail.getAssessmentProject() + selectfixquanassdetail.getScore().toPlainString() + "分" + "\n";

                }
                break;
                case "推动执行能力": {
                    row.setDri_exe_abi(selectfixquanassdetail.getScore());
                    assResult = assResult + selectfixquanassdetail.getAssessmentProject() + selectfixquanassdetail.getScore().toPlainString() + "分" + "\n";

                }
                break;
                case "学习创新能力": {
                    row.setLea_inn_abi(selectfixquanassdetail.getScore());
                    assResult = assResult + selectfixquanassdetail.getAssessmentProject() + selectfixquanassdetail.getScore().toPlainString() + "分" + "\n";

                }
                break;
                case "团结建设能力": {
                    row.setSol_cap_abi(selectfixquanassdetail.getScore());
                    assResult = assResult + selectfixquanassdetail.getAssessmentProject() + selectfixquanassdetail.getScore().toPlainString() + "分" + "\n";
                }
                break;
                case "履职绩效": {
                    row.setPerformance(selectfixquanassdetail.getScore());
                    assResult = assResult + selectfixquanassdetail.getAssessmentProject() + selectfixquanassdetail.getScore().toPlainString() + "分" + "\n";

                }
                break;
                case "协同绩效": {
                    row.setColl_performance(selectfixquanassdetail.getScore());
                    assResult = assResult + selectfixquanassdetail.getAssessmentProject() + selectfixquanassdetail.getScore().toPlainString() + "分" + "\n";

                }
                break;
                default:
                    throw new IllegalStateException("Unexpected value: " + selectfixquanassdetail.getAssessmentProject());
            }
        }
        row.setParentId(oldParentId);
        row.setNum(BigDecimal.valueOf((int)num));
        leaderAssessMapper.updateLaunchFixQuanRow(row);
        log.info(leadFixQuanCountInfo.getLeadPersonId()+"定量考核票数"+num);
        ar.setAssessResult(assResult);
        List<AssessmentResult> arList = new ArrayList<>();
        arList.add(ar);
        createAssessmentResultService.updateOrInsertAssessmentResultByModel(user, department, arList);
        return "成功";
    }


    @Transactional
    @Override
    public void insertFixQuanAssDetails(LeadFixQuanCountInfo leadFixQuanCountInfo, UserModel user) {
        List<AssessmentDetail> list = leadFixQuanCountInfo.getAdList();
        for (AssessmentDetail assessmentDetail : list) {
            FixQuanAssDetails fixQuanAssDetails = new FixQuanAssDetails();
            fixQuanAssDetails.setLeadPersonId(leadFixQuanCountInfo.getLeadPersonId());
            fixQuanAssDetails.setParentId(leadFixQuanCountInfo.getId());
            fixQuanAssDetails.setOldParentId(leadFixQuanCountInfo.getOldParentId());
            fixQuanAssDetails.setUserId(user.getUserId());
            fixQuanAssDetails.setAnnual(leadFixQuanCountInfo.getAnnual());
            fixQuanAssDetails.setCompanyName(leadFixQuanCountInfo.getCompanyName());
            fixQuanAssDetails.setAssessmentProject(assessmentDetail.getAssessment_project());
            fixQuanAssDetails.setScore(new BigDecimal(Double.toString(assessmentDetail.getScore())));
            leaderAssessMapper.insertFixQuanAssDetails(fixQuanAssDetails);
        }

    }

    @Override
    public void insertleaderQualityChildren(LaunchQuality launchQuality, String userId) {
        //获取定性考核评分表所有子表数据
        List<LeaderQualityChild> leaderQualityChildren = launchQuality.getLeaderQualityChildren();
        for (LeaderQualityChild leaderQualityChild : leaderQualityChildren) {
            leaderQualityChild.setUserId(userId);
        }
        leaderAssessMapper.insertLeadQualityDetails(leaderQualityChildren);

    }


}



