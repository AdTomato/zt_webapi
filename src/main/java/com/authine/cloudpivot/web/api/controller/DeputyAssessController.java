package com.authine.cloudpivot.web.api.controller;

import com.alibaba.fastjson.JSON;
import com.authine.cloudpivot.engine.api.facade.BizObjectFacade;
import com.authine.cloudpivot.engine.api.facade.OrganizationFacade;
import com.authine.cloudpivot.engine.api.facade.WorkflowInstanceFacade;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.engine.api.model.runtime.BizObjectModel;
import com.authine.cloudpivot.web.api.bean.User;
import com.authine.cloudpivot.web.api.bean.deputyassess.*;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.service.DeputyAssessService;
import com.authine.cloudpivot.web.api.utils.CreateEvaluationTableUtils;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The type Deputy assess controller.
 *
 * @Author Asuvera
 * @Date 2020 /7/20 11:24
 * @Version 1.0
 */
@RestController
@RequestMapping("/ext/deputyLeaderAssess")
@Slf4j
public class DeputyAssessController extends BaseController {
    @Autowired
    private DeputyAssessService deputyAssessService;

    /**
     * Launch deputy assess response result.
     *
     * @param params the params
     * @return the response result
     */
    @PostMapping("/launchDeputyAssess")
    public ResponseResult<Object> launchDeputyAssess(@RequestBody LaunchDeputyAssessRequest params) {
        log.info("发起部门副职及以上领导人员能力素质评价");
        if (params == null || StringUtils.isEmpty(params.getId()) || params.getDeputy_assesselement() == null || params.getDeputy_assesselement().size() == 0) {
            return this.getErrResponseResult(null, 404L, "参数为空");
        }
        String id = params.getId();
        List<LaunchDeputyAssChild> deputy_assesselement = params.getDeputy_assesselement();
        List<LaunchJudges> deputy_judges = params.getDeputy_judges();

        try {
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
            for (User person : params.getPeople()) {
                for (LaunchJudges deputy_judge : params.getDeputy_judges()) {
                    BizObjectModel model = new BizObjectModel();
                    model.setSchemaCode("dept_deputy_assess");
                    Map<String, Object> data = new HashMap<>();
                    // 被考核人
                    List<User> people = new ArrayList<>();
                    people.add(person);
                    data.put("annual", params.getAnnual());
                    // 评委
                    data.put("judges", JSON.toJSONString(deputy_judge.getJudges()));
                    data.put("person", JSON.toJSONString(people));
                    data.put("dept", JSON.toJSONString(params.getDept()));
                    data.put("weight", deputy_judge.getWeight());
                    data.put("oldParentId", params.getId());
                    // 将数据写入到model中
                    model.put(data);

                    log.info("存入数据库中的数据：" + data);
                    // 创建机关部门打分表,返回领导人员打分表的id值
                    String objectId = bizObjectFacade.saveBizObject(userId, model, false);
                    List<LaunchDeputyAssChild> deptDeputyAssessTables = CreateEvaluationTableUtils.getDeptDeputyAssessTables(deputy_assesselement, objectId);
                    deputyAssessService.insertDeptDeputyAsselement(deptDeputyAssessTables);
                    workflowInstanceFacade.startWorkflowInstance(user.getDepartmentId(), user.getId(), "dept_deputy_assessflow", objectId, true);

                }

            }
        } catch (Exception e) {
            log.error("错误信息", e);
            getErrResponseResult(null, 404L, e.getMessage());
        }
        return this.getOkResponseResult("ok", "成功");
    }


    @PostMapping("/submitDeputyAssess")
    public ResponseResult<Object> launchDeputyAssess(@RequestBody SubmitDeputyAssessRequest params) {
        log.info("提交部门副职及以上领导人员能力素质评价");
        if (params == null || StringUtils.isEmpty(params.getParentId()) || params.getSubmitDeputyAssChildren() == null || params.getSubmitDeputyAssChildren().size() == 0) {
            return this.getErrResponseResult(null, 404L, "参数为空");
        }
        String userId = this.getUserId();
        for (SubmitDeputyAssChild submitDeputyAssChild : params.getSubmitDeputyAssChildren()) {
            submitDeputyAssChild.setParentId(params.getParentId());
            submitDeputyAssChild.setOldParentId(params.getOldParentId());
            submitDeputyAssChild.setUserId(userId);
            submitDeputyAssChild.setAssessedPersonId(params.getPerson().get(0).getId());
            submitDeputyAssChild.setDeptId(params.getDept().getId());
            deputyAssessService.insertSubmitDeputyAsselement(submitDeputyAssChild);
        }

        return null;

    }

}
