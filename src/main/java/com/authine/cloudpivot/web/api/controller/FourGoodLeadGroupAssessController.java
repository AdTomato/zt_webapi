package com.authine.cloudpivot.web.api.controller;

import com.alibaba.fastjson.JSON;
import com.authine.cloudpivot.engine.api.facade.BizObjectFacade;
import com.authine.cloudpivot.engine.api.facade.OrganizationFacade;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.engine.api.model.runtime.BizObjectModel;
import com.authine.cloudpivot.web.api.bean.deputyassess.LaunchDeputyAssChild;
import com.authine.cloudpivot.web.api.bean.fourgoodleadgroup.SubmitFourGoodAssessChild;
import com.authine.cloudpivot.web.api.bean.fourgoodleadgroup.SubmitFourGoodAssessRequest;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.service.FourGoodLeadGroupAssessService;
import com.authine.cloudpivot.web.api.utils.RedisUtils;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author zhengshihao
 * @Date: 2021/01/28 14:41
 */
@Api(tags = "四好领导班子考核接口")
@RestController
@RequestMapping("/ext/fourGoodAssess")
@Slf4j
public class FourGoodLeadGroupAssessController extends BaseController {
    @Autowired
    RedisUtils redisUtils;
    @Autowired
    FourGoodLeadGroupAssessService fourGoodLeadGroupAssessService;

    @ApiOperation(value = "业绩评价与能力素质评价初始化")
    @GetMapping("/initEvaluationElement")
    public ResponseResult<Object> initFourGoodLeadGroupEvaluation() {
        List<LaunchDeputyAssChild> evaluationTables = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            LaunchDeputyAssChild evaluationTable = new LaunchDeputyAssChild();
            // 设置测评项目和测评内容
            switch (i) {
                case 0: {
                    evaluationTable.setAssess_index("政治方向");
                    evaluationTable.setAssess_content("理想信念和政治立场坚定，全面落实科学发展观；坚决执行党的路线方针政策和股份公司规章制度，组织纪律观念和大局意识强；学习型党组织建设成效显著；坚持正确的国有企业改革方向。");
                    evaluationTable.setSortKey(new BigDecimal("10"));
                }
                break;
                case 1: {
                    evaluationTable.setAssess_index("责任意识");
                    evaluationTable.setAssess_content("对企业忠诚，有强烈的事业心和责任感，工作勤奋，真抓实干；围绕企业中心开展工作，有激情，在状态；切实履行社会责任，具有良好的社会形象和美誉度。");
                    evaluationTable.setSortKey(new BigDecimal("20"));

                }
                break;
                case 2: {
                    evaluationTable.setAssess_index("企业党建");
                    evaluationTable.setAssess_content("党组织和群团组织机构健全，党组织坚强有力，党员先锋模范作用有效发挥；有效参与重大问题决策，党管干部原则落到实处，政治核心作用突出；紧密围绕企业中心安排部署党建思想政治工作，思想政治工作扎实有效，有力促进生产经营。");
                    evaluationTable.setSortKey(new BigDecimal("30"));

                }
                break;
                case 3: {
                    evaluationTable.setAssess_index("科学管理");
                    evaluationTable.setAssess_content("企业发展战略目标明确，可持续发展能力强；经营决策机制健全，方法科学，无重大决策失误；管理科学规范，管理创新成效明显，执行力强；安全生产制度健全，施工生产有序可控；注重科技创新和品牌建设，企业核心竞争力不断提升；风险管控机制健全，运作规范，重大风险可控；加强人才队伍建设，人事制度改革不断深化；按要求完成节能减排工作目标。");
                    evaluationTable.setSortKey(new BigDecimal("40"));

                }
                break;
                case 4: {
                    evaluationTable.setAssess_index("发扬民主");
                    evaluationTable.setAssess_content("坚持民主集中制，“三重一大”问题集体讨论决定；班子内部民主气氛好，企业主要负责人民主意识强，善于发挥班子成员作用；领导班子民主生活会制度规范，有解决自身矛盾和问题的能力。");
                    evaluationTable.setSortKey(new BigDecimal("50"));

                }
                break;
                case 5: {
                    evaluationTable.setAssess_index("整体合力");
                    evaluationTable.setAssess_content("领导班子凝聚力、创造力和战斗力强，整体功能有效发挥；主要领导驾驭全局能力强，党政正职团结一致，沟通顺畅，在班子中起模范带头作用；班子副职相互支持，工作协调，领导集体坚强有力，执行力强。");
                    evaluationTable.setSortKey(new BigDecimal("60"));

                }
                break;
                case 6: {
                    evaluationTable.setAssess_index("廉洁自律");
                    evaluationTable.setAssess_content("建立健全教育、制度、监督并重的惩治和预防腐败体系，党风建设和反腐倡廉各项制度健全；遵守党和国家廉洁自律各项规定，无违法违纪现象；坚持勤俭办企业，职务消费制度健全；恪守职业道德，保护企业商业机密，自觉维护企业利益。");
                    evaluationTable.setSortKey(new BigDecimal("70"));

                }
                break;
                case 7: {
                    evaluationTable.setAssess_index("勤勉务实");
                    evaluationTable.setAssess_content("班子成员作风严谨、深入、扎实，注重深入基层开展调查研究，解决实际问题；注重思考谋划企业改革发展重大问题，指导工作实践；忠于职守，勤勉尽责。");
                    evaluationTable.setSortKey(new BigDecimal("80"));

                }
                break;
                case 8: {
                    evaluationTable.setAssess_index("联系群众");
                    evaluationTable.setAssess_content("坚持依靠方针,全心全意依靠职工群众办企业；职代会制度健全，坚持领导干部述职述廉、民主评议领导干部等制度；坚持(厂)务公开，全面落实民主管理制度；坚持以人为本，关心职工群众，维护职工群众权益，企业稳定，干群关系和谐。");
                    evaluationTable.setSortKey(new BigDecimal("90"));

                }
                break;
                default:
                    throw new IllegalStateException("Unexpected value: " + i);
            }
            evaluationTables.add(evaluationTable);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("launch_fourgood_element", evaluationTables);
        return this.getOkResponseResult(map, "success");
    }

    @PostMapping("/saveScore")
    public ResponseResult<Object> saveFourGoodLeadGroupScore(@RequestBody SubmitFourGoodAssessRequest param) {

        // 创建数据的引擎类
        BizObjectFacade bizObjectFacade = super.getBizObjectFacade();
        String userId = this.getUserId();
        if (userId == null) {
            userId = "2c9280a26706a73a016706a93ccf002b";
        }
        OrganizationFacade organizationFacade = super.getOrganizationFacade();
        UserModel user = organizationFacade.getUser(userId);
        synchronized (DeputyAssessController.class) {
            if (redisUtils.hasKey(userId + "-" + param.getId())) {
                log.info("重复提交数据：" + param);
                log.info("用户" + user.getName() + userId);
                return this.getErrResponseResult(null, 444L, "禁止重复提交");
            } else {
                redisUtils.set(userId + "-" + param.getId(), 1, 30);
            }
        }
        String finalUserId = userId;
        for (SubmitFourGoodAssessChild submitFourGoodAssessChild : param.getSubmitFourGoodAssessChildren()) {
            BizObjectModel model = new BizObjectModel();
            UserModel assessedPerson = organizationFacade.getUser(userId);
            model.setSchemaCode("fourgoodleadgroupdetail");
            Map<String, Object> data = new HashMap<>();
            data.put("date", param.getDate());
            data.put("fourgoodform", param.getId());
            data.put("dept", JSON.toJSONString(param.getDept()));
            data.put("assess_index", submitFourGoodAssessChild.getAssessIndex());
            data.put("score", submitFourGoodAssessChild.getScore());
            model.put(data);
            log.info("存入数据库中的数据：" + data);
            String objectId = bizObjectFacade.saveBizObject(userId, model, false);

        }

        return this.getOkResponseResult("ok", "success");

    }

    @PutMapping("/countScore")
    public ResponseResult<Object> countFourGoodLeadGroupScore( String id) {
        List<SubmitFourGoodAssessChild> submitFourGoodAssessChildren = fourGoodLeadGroupAssessService.countAvgScore(id);
        fourGoodLeadGroupAssessService.updateAvgScore(submitFourGoodAssessChildren,id);
        return this.getOkResponseResult("ok", "success");

    }

}
