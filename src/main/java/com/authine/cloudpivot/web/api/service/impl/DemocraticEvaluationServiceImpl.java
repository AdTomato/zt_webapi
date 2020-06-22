package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.web.api.bean.*;
import com.authine.cloudpivot.web.api.mapper.DemocraticEvaluationMapper;
import com.authine.cloudpivot.web.api.parameter.DemocraticEvaluationUpdate;
import com.authine.cloudpivot.web.api.service.DemocraticEvaluationService;
import jodd.util.StringUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @Author: wangyong
 * @Date: 2020-01-05 02:14
 * @Description: 民主评议表service实现类
 */
@Service
public class DemocraticEvaluationServiceImpl implements DemocraticEvaluationService {

    @Resource
    DemocraticEvaluationMapper democraticEvaluationMapper;

    /**
     * @param id : 发起工作发起工作民主评议表id
     * @return : com.authine.cloudpivot.web.api.bean.SDemocraticEvaluation
     * @Author: wangyong
     * @Date: 2020/1/5 2:15
     * @Description: 获取发起工作民主评议表所有信息
     */
    @Override
    public SDemocraticEvaluation getSDemocraticEvaluationDataById(String id) {
        return democraticEvaluationMapper.getSDemocraticEvaluationDataById(id);
    }

    /**
     * @param map : 参数
     * @return : java.util.List<com.authine.cloudpivot.web.api.bean.DemocraticEvaluation>
     * @Author: wangyong
     * @Date: 2020/1/5 2:16
     * @Description: 根据传递过去的参数, 获取符合规则的工作民主评议表
     */
    @Override
    public List<DemocraticEvaluation> getAllDemocraticEvaluationData(Map map) {
        return democraticEvaluationMapper.getAllDemocraticEvaluationData(map);
    }

    /**
     * @param sd : 发起工作民主评议表
     * @return : void
     * @Author: wangyong
     * @Date: 2020/1/5 2:16
     * @Description: 更新发起工作民主评议表所有信息
     */
    @Override
    public void updateSDemocraticEvaluation(SDemocraticEvaluation sd) {
        democraticEvaluationMapper.updateSDemocraticEvaluation(sd);
    }

    /**
     * @param democraticEvaluations: 工作民主评议表数据
     * @Author: wangyong
     * @Date: 2020/3/27 10:44
     * @return: void
     * @Description: 插入工作民主评议表数据
     */
    @Override
    @Transactional
    public void insertDemocraticEvaluation(List<GDemocraticEvaluation> democraticEvaluations) {
        democraticEvaluationMapper.insertDemocraticEvaluation(democraticEvaluations);
    }

    /**
     * @param democraticEvaluationUpdate:
     * @Author: wangyong
     * @Date: 2020/3/27 12:29
     * @return: void
     * @Description: 更新民主评议表
     */
    @Override
    @Transactional
    public void updateDemocratic(DemocraticEvaluationUpdate democraticEvaluationUpdate) {
        SendDemocraticEvaluation sendDemocraticEvaluation = democraticEvaluationMapper.getSendDemocraticEvaluationById(democraticEvaluationUpdate.getUnit());

        updateGeneralEvaluation(sendDemocraticEvaluation, democraticEvaluationUpdate.getGeneralEvaluation());

        updateRegulationLawsOpinion(sendDemocraticEvaluation, democraticEvaluationUpdate.getRegulationLawsOpinion());

        updateBadPractiveOpinion(sendDemocraticEvaluation, democraticEvaluationUpdate.getBadPractiveOpinion());

        updateInstitutionalReformOpinion(sendDemocraticEvaluation, democraticEvaluationUpdate.getInstitutionalReformOpinion());

        updateProminentProblem(sendDemocraticEvaluation, democraticEvaluationUpdate);

        sendDemocraticEvaluation.setVotePeoples(sendDemocraticEvaluation.getVotePeoples() + 1);

        democraticEvaluationMapper.updateSendDemocraticEvaluation(sendDemocraticEvaluation);
    }

    @Override
    public void updateDemocratic(String id) {
        List<NewDemocraticEvaluation> allDemocraticEvaluation = democraticEvaluationMapper.getAllDemocraticEvaluation(id);
        SendDemocraticEvaluation sendDemocraticEvaluation = new SendDemocraticEvaluation();
        for (NewDemocraticEvaluation newDemocraticEvaluation : allDemocraticEvaluation) {

            if (newDemocraticEvaluation.getGSatisfiedPoll() == 1) {
                sendDemocraticEvaluation.setGSatisfiedPoll(sendDemocraticEvaluation.getGSatisfiedPoll() + 1);
            } else if (newDemocraticEvaluation.getGBasicSatisfiedPoll() == 1) {
                sendDemocraticEvaluation.setGBasicSatisfiedPoll(sendDemocraticEvaluation.getGBasicSatisfiedPoll() + 1);
            } else if (newDemocraticEvaluation.getGNoSatisfiedPoll() == 1) {
                sendDemocraticEvaluation.setGNoSatisfiedPoll(sendDemocraticEvaluation.getGNoSatisfiedPoll() + 1);
            } else if (newDemocraticEvaluation.getGNoUnderstandPoll() == 1) {
                sendDemocraticEvaluation.setGNoUnderstandPoll(sendDemocraticEvaluation.getGNoUnderstandPoll() + 1);
            }

            if (newDemocraticEvaluation.getRSatisfiedPoll() == 1) {
                sendDemocraticEvaluation.setRSatisfiedPoll(sendDemocraticEvaluation.getRSatisfiedPoll() + 1);
            } else if (newDemocraticEvaluation.getRBasicSatisfiedPoll() == 1) {
                sendDemocraticEvaluation.setRBasicSatisfiedPoll(sendDemocraticEvaluation.getRBasicSatisfiedPoll() + 1);
            } else if (newDemocraticEvaluation.getRNoSatisfiedPoll() == 1) {
                sendDemocraticEvaluation.setRNoSatisfiedPoll(sendDemocraticEvaluation.getRNoSatisfiedPoll() + 1);
            } else if (newDemocraticEvaluation.getRNoUnderstandPoll() == 1) {
                sendDemocraticEvaluation.setRNoUnderstandPoll(sendDemocraticEvaluation.getRNoUnderstandPoll() + 1);
            }

            if (newDemocraticEvaluation.getBSatisfiedPoll() == 1) {
                sendDemocraticEvaluation.setBSatisfiedPoll(sendDemocraticEvaluation.getBSatisfiedPoll() + 1);
            } else if (newDemocraticEvaluation.getBBasicSatisfiedPoll() == 1) {
                sendDemocraticEvaluation.setBBasicSatisfiedPoll(sendDemocraticEvaluation.getBBasicSatisfiedPoll() + 1);
            } else if (newDemocraticEvaluation.getBNoSatisfiedPoll() == 1) {
                sendDemocraticEvaluation.setBNoSatisfiedPoll(sendDemocraticEvaluation.getBNoSatisfiedPoll() + 1);
            } else if (newDemocraticEvaluation.getBNoUnderstandPoll() == 1) {
                sendDemocraticEvaluation.setBNoUnderstandPoll(sendDemocraticEvaluation.getBNoUnderstandPoll() + 1);
            }

            if (newDemocraticEvaluation.getISatisfiedPoll() == 1) {
                sendDemocraticEvaluation.setISatisfiedPoll(sendDemocraticEvaluation.getISatisfiedPoll() + 1);
            } else if (newDemocraticEvaluation.getIBasicSatisfiedPoll() == 1) {
                sendDemocraticEvaluation.setIBasicSatisfiedPoll(sendDemocraticEvaluation.getIBasicSatisfiedPoll() + 1);
            } else if (newDemocraticEvaluation.getINoSatisfiedPoll() == 1) {
                sendDemocraticEvaluation.setINoSatisfiedPoll(sendDemocraticEvaluation.getINoSatisfiedPoll() + 1);
            } else if (newDemocraticEvaluation.getINoUnderstandPoll() == 1) {
                sendDemocraticEvaluation.setINoUnderstandPoll(sendDemocraticEvaluation.getINoUnderstandPoll() + 1);
            }

            String problem = newDemocraticEvaluation.getProminentProblem();
            if (!StringUtils.isEmpty(problem)) {
                if (problem.contains("1. 执行《干部任用条例》规定的资格、条件和程序不严格")) {
                    sendDemocraticEvaluation.setNoStrictPoll(sendDemocraticEvaluation.getNoStrictPoll() + 1);
                }
                if (problem.contains("2. 任人唯亲")) {
                    sendDemocraticEvaluation.setAppointPeoplePoll(sendDemocraticEvaluation.getAppointPeoplePoll() + 1);
                }
                if (problem.contains("3. 领导干部用人上个人说了算")) {
                    sendDemocraticEvaluation.setIndividualPoll(sendDemocraticEvaluation.getIndividualPoll() + 1);
                }
                if (problem.contains("4. 跑官要官，说情打招呼")) {
                    sendDemocraticEvaluation.setRunOfficePoll(sendDemocraticEvaluation.getRunOfficePoll() + 1);
                }
                if (problem.contains("5. 买官卖官")) {
                    sendDemocraticEvaluation.setBuySellOfficePoll(sendDemocraticEvaluation.getBuySellOfficePoll() + 1);
                }
                if (problem.contains("6. 拉票")) {
                    sendDemocraticEvaluation.setCanvassingPoll(sendDemocraticEvaluation.getCanvassingPoll() + 1);
                }
                if (problem.contains("7. 其他")) {
                    if (StringUtil.isEmpty(sendDemocraticEvaluation.getOtherProblems())) {
                        sendDemocraticEvaluation.setOtherProblems(newDemocraticEvaluation.getConcreteContent() + "\n");
                    } else {
                        sendDemocraticEvaluation.setOtherProblems(sendDemocraticEvaluation.getOtherProblems() + newDemocraticEvaluation.getConcreteContent() + "\n");
                    }
                }
                if (problem.contains("8. 不存在突出问题")) {
                    sendDemocraticEvaluation.setNoOutstandingProblemsPoll(sendDemocraticEvaluation.getNoOutstandingProblemsPoll() + 1);
                }
            }
            if (!StringUtil.isEmpty(newDemocraticEvaluation.getCommentSuggestion())) {
                if (StringUtil.isEmpty(sendDemocraticEvaluation.getCommentSuggestion())) {
                    sendDemocraticEvaluation.setCommentSuggestion(newDemocraticEvaluation.getCommentSuggestion() + "\n");
                } else {
                    sendDemocraticEvaluation.setCommentSuggestion(sendDemocraticEvaluation.getCommentSuggestion() + newDemocraticEvaluation.getCommentSuggestion() + "\n");
                }
            }
        }
        sendDemocraticEvaluation.setVotePeoples(allDemocraticEvaluation.size());
        sendDemocraticEvaluation.setId(id);
        democraticEvaluationMapper.updateSendDemocraticEvaluation(sendDemocraticEvaluation);

    }

    /**
     * @Author: wangyong
     * @Date: 2020/3/27 12:31
     * @param sendDemocraticEvaluation:
     * @param democraticEvaluationUpdate:
     * @return: void
     * @Description: 更新您认为目前本企业（本单位）选人用人工作存在的突出问题是什么
     */
    private void updateProminentProblem(SendDemocraticEvaluation sendDemocraticEvaluation, DemocraticEvaluationUpdate democraticEvaluationUpdate) {
        String problem = democraticEvaluationUpdate.getProminentProblem();
        if (!StringUtils.isEmpty(problem)) {
            if (problem.contains("1. 执行《干部任用条例》规定的资格、条件和程序不严格")) {
                sendDemocraticEvaluation.setNoStrictPoll(sendDemocraticEvaluation.getNoStrictPoll() + 1);
            }
            if (problem.contains("2. 任人唯亲")) {
                sendDemocraticEvaluation.setAppointPeoplePoll(sendDemocraticEvaluation.getAppointPeoplePoll() + 1);
            }
            if (problem.contains("3. 领导干部用人上个人说了算")) {
                sendDemocraticEvaluation.setIndividualPoll(sendDemocraticEvaluation.getIndividualPoll() + 1);
            }
            if (problem.contains("4. 跑官要官，说情打招呼")) {
                sendDemocraticEvaluation.setRunOfficePoll(sendDemocraticEvaluation.getRunOfficePoll() + 1);
            }
            if (problem.contains("5. 买官卖官")) {
                sendDemocraticEvaluation.setBuySellOfficePoll(sendDemocraticEvaluation.getBuySellOfficePoll() + 1);
            }
            if (problem.contains("6. 拉票")) {
                sendDemocraticEvaluation.setCanvassingPoll(sendDemocraticEvaluation.getCanvassingPoll() + 1);
            }
            if (problem.contains("7. 其他")) {
                if (StringUtil.isEmpty(sendDemocraticEvaluation.getOtherProblems())) {
                    sendDemocraticEvaluation.setOtherProblems(democraticEvaluationUpdate.getConcreteContent() + "\n");
                } else {
                    sendDemocraticEvaluation.setOtherProblems(sendDemocraticEvaluation.getOtherProblems() + democraticEvaluationUpdate.getConcreteContent() + "\n");
                }
            }
            if (problem.contains("8. 不存在突出问题")) {
                sendDemocraticEvaluation.setNoOutstandingProblemsPoll(sendDemocraticEvaluation.getNoOutstandingProblemsPoll() + 1);
            }
        }
        if (!StringUtil.isEmpty(democraticEvaluationUpdate.getCommentSuggestion())) {
            if (StringUtil.isEmpty(sendDemocraticEvaluation.getCommentSuggestion())) {
                sendDemocraticEvaluation.setCommentSuggestion(democraticEvaluationUpdate.getCommentSuggestion() + "\n");
            } else {
                sendDemocraticEvaluation.setCommentSuggestion(sendDemocraticEvaluation.getCommentSuggestion() + democraticEvaluationUpdate.getCommentSuggestion() + "\n");
            }
        }
    }

    /**
     * @Author: wangyong
     * @Date: 2020/3/27 12:30
     * @param sendDemocraticEvaluation:
     * @param institutionalReformOpinion:
     * @return: void
     * @Description: 更新对本企业（本单位）深化干部人事制度改革的看法
     */
    private void updateInstitutionalReformOpinion(SendDemocraticEvaluation sendDemocraticEvaluation, String institutionalReformOpinion) {


        switch (institutionalReformOpinion) {
            case "满意":
                sendDemocraticEvaluation.setISatisfiedPoll(sendDemocraticEvaluation.getISatisfiedPoll() + 1);
                break;
            case "基本满意":
                sendDemocraticEvaluation.setIBasicSatisfiedPoll(sendDemocraticEvaluation.getIBasicSatisfiedPoll() + 1);
                break;
            case "不满意":
                sendDemocraticEvaluation.setINoSatisfiedPoll(sendDemocraticEvaluation.getINoSatisfiedPoll() + 1);
                break;
            case "不了解":
                sendDemocraticEvaluation.setINoUnderstandPoll(sendDemocraticEvaluation.getINoUnderstandPoll() + 1);
                break;
        }
    }

    /**
     * @Author: wangyong
     * @Date: 2020/3/27 12:30
     * @param sendDemocraticEvaluation:
     * @param badPractiveOpinion:
     * @return: void
     * @Description: 更新对本企业（本单位）整治用人上不正之风工作的看法
     */
    private void updateBadPractiveOpinion(SendDemocraticEvaluation sendDemocraticEvaluation, String badPractiveOpinion) {


        switch (badPractiveOpinion) {
            case "满意":
                sendDemocraticEvaluation.setBSatisfiedPoll(sendDemocraticEvaluation.getBSatisfiedPoll() + 1);
                break;
            case "基本满意":
                sendDemocraticEvaluation.setBBasicSatisfiedPoll(sendDemocraticEvaluation.getBBasicSatisfiedPoll() + 1);
                break;
            case "不满意":
                sendDemocraticEvaluation.setBNoSatisfiedPoll(sendDemocraticEvaluation.getBNoSatisfiedPoll() + 1);
                break;
            case "不了解":
                sendDemocraticEvaluation.setBNoUnderstandPoll(sendDemocraticEvaluation.getBNoUnderstandPoll() + 1);
                break;
        }
    }

    /**
     * @Author: wangyong
     * @Date: 2020/3/27 12:29
     * @param sendDemocraticEvaluation:
     * @param regulationLawsOpinion:
     * @return: void
     * @Description: 更新对本企业（本单位）执行选人用人工作政策法规情况的看法
     */
    private void updateRegulationLawsOpinion(SendDemocraticEvaluation sendDemocraticEvaluation, String regulationLawsOpinion) {


        switch (regulationLawsOpinion) {
            case "满意":
                sendDemocraticEvaluation.setRSatisfiedPoll(sendDemocraticEvaluation.getRSatisfiedPoll() + 1);
                break;
            case "基本满意":
                sendDemocraticEvaluation.setRBasicSatisfiedPoll(sendDemocraticEvaluation.getRBasicSatisfiedPoll() + 1);
                break;
            case "不满意":
                sendDemocraticEvaluation.setRNoSatisfiedPoll(sendDemocraticEvaluation.getRNoSatisfiedPoll() + 1);
                break;
            case "不了解":
                sendDemocraticEvaluation.setRNoUnderstandPoll(sendDemocraticEvaluation.getRNoUnderstandPoll() + 1);
                break;
        }
    }

    /**
     * @param sendDemocraticEvaluation:
     * @param generalEvaluation:
     * @Author: wangyong
     * @Date: 2020/3/27 11:35
     * @return: void
     * @Description: 更新对本企业（本单位）选人用人工作的总体评价
     */
    private void updateGeneralEvaluation(SendDemocraticEvaluation sendDemocraticEvaluation, String generalEvaluation) {
        switch (generalEvaluation) {
            case "满意":
                sendDemocraticEvaluation.setGSatisfiedPoll(sendDemocraticEvaluation.getGSatisfiedPoll() + 1);
                break;
            case "基本满意":
                sendDemocraticEvaluation.setGBasicSatisfiedPoll(sendDemocraticEvaluation.getGBasicSatisfiedPoll() + 1);
                break;
            case "不满意":
                sendDemocraticEvaluation.setGNoSatisfiedPoll(sendDemocraticEvaluation.getGNoSatisfiedPoll() + 1);
                break;
            case "不了解":
                sendDemocraticEvaluation.setGNoUnderstandPoll(sendDemocraticEvaluation.getGNoUnderstandPoll() + 1);
                break;
        }
    }


}
