package com.authine.cloudpivot.web.api.controller;

import com.alibaba.fastjson.JSON;
import com.authine.cloudpivot.engine.api.facade.BizObjectFacade;
import com.authine.cloudpivot.engine.api.facade.OrganizationFacade;
import com.authine.cloudpivot.engine.api.facade.WorkflowInstanceFacade;
import com.authine.cloudpivot.engine.api.model.organization.DepartmentModel;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.engine.api.model.runtime.BizObjectModel;
import com.authine.cloudpivot.web.api.bean.EmployeeEvalution.LaunchEvaluationRequest;
import com.authine.cloudpivot.web.api.bean.User;
import com.authine.cloudpivot.web.api.bean.deputyassess.LaunchDeputyAssChild;
import com.authine.cloudpivot.web.api.bean.deputyassess.LaunchJudges;
import com.authine.cloudpivot.web.api.bean.deputyassess.SubmitDeputyAssChild;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.service.DeputyAssessService;
import com.authine.cloudpivot.web.api.service.EmployeeEvaluationService;
import com.authine.cloudpivot.web.api.utils.CreateEvaluationTableUtils;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author Asuvera
 * @Date 2020/8/17 15:26
 * @Version 1.0
 */
@Api(tags = "员工个人考核接口")
@RestController
@RequestMapping("/ext/employee")
@Slf4j
public class EmployeeEvaluationController extends BaseController {
    @Autowired
    private DeputyAssessService deputyAssessService;
    @Autowired
    private EmployeeEvaluationService employeeEvaluationService;

    /**
     * 从基础数据维护表中获取默认考核项
     * @param assess_name 用来区分是业绩评价还是能力素质评价
     * @return
     */
    @ApiOperation(value = "业绩评价与能力素质评价初始化")
    @GetMapping("/initEvaluationElement")
    public ResponseResult<Object> initPerformanceAndCompetenceEvaluation(String assess_name){
            if (null == assess_name||"".equals(assess_name)){
               return this.getErrResponseResult(null,500L,"考核类型出错");
            }
            if ("员工个人业绩评价".equals(assess_name)){
                String id = "b2d3afdc47f64ac887cd911659061f1b";
                List<LaunchDeputyAssChild> launch_deputy_ass_ele = employeeEvaluationService.initPerformanceEvaluationDeputyElement(id);
                List<LaunchDeputyAssChild> launch_section_ass_ele = employeeEvaluationService.initPerformanceEvaluationSectionElement(id);
                List<LaunchJudges> launch_emp_per_judges = employeeEvaluationService.initPerformanceEvaluationJudgesElement(id);

                Map<String,Object> map = new HashMap<>();
                map.put("launch_deputy_ass_ele",launch_deputy_ass_ele);
                map.put("launch_section_ass_ele",launch_section_ass_ele);
                map.put("launch_emp_per_judges",launch_emp_per_judges);
                return this.getOkResponseResult(map,"success");

            }
            if ("员工能力素质评价".equals(assess_name)){
                String id = "568588a3a3834f5a971130d5119487c5";
                List<LaunchDeputyAssChild> launch_deputy_ass_ele = employeeEvaluationService.initPerformanceEvaluationDeputyElement(id);
                List<LaunchDeputyAssChild> launch_section_ass_ele = employeeEvaluationService.initPerformanceEvaluationSectionElement(id);
                List<LaunchJudges> launch_emp_per_judges = employeeEvaluationService.initPerformanceEvaluationJudgesElement(id);
                Map<String,Object> map = new HashMap<>();
                map.put("launch_deputy_ass_ele",launch_deputy_ass_ele);
                map.put("launch_section_ass_ele",launch_section_ass_ele);
                map.put("launch_emp_per_judges",launch_emp_per_judges);
                return this.getOkResponseResult(map,"success");
            }
            return this.getErrResponseResult(null,500L,"考核类型出错");

    }



    @ApiOperation(value = "员工评价发起表发起流程")
    @PostMapping("/launchEvaluation")
    public ResponseResult<Object> launchPerformanceEvaluation(@RequestBody @ApiParam(name = "发起流程信息") LaunchEvaluationRequest params) {
        if (params.getAssess_name() ==null||"".equals(params.getAssess_name())){
            return this.getErrResponseResult(null,500L,"考核类型出错");
        }
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
        List<LaunchDeputyAssChild> deputy_assesselement = params.getDeputy_assesselement();
        List<LaunchDeputyAssChild> section_assesselement = params.getSection_assesselement();
        UserModel user = organizationFacade.getUser(userId);
        if ("员工个人业绩评价".equals(params.getAssess_name())){
            List<LaunchJudges> deputy_judges =  params.getDeputy_judges();
            Map<String, List<LaunchJudges>> collect = deputy_judges.parallelStream().collect(Collectors.groupingBy(LaunchJudges::getMutual));
            for (List<LaunchJudges> value : collect.values()) {
                //刚进来是 是,参与互评   或者 否
                if ("是".equals(value.get(0).getMutual())){
                    Map<String, List<LaunchJudges>> collectMutual = value.parallelStream().collect(Collectors.groupingBy(LaunchJudges::getTable));
                    for (List<LaunchJudges> launchJudges : collectMutual.values()) {
                        //进来 是参与互评, 副职/科长表
                        if("副职及以上".equals(launchJudges.get(0).getTable())){
                            for (LaunchJudges launchJudge : launchJudges) {
                                if (null != launchJudge.getJudges()) {
                                    List<User> judges = launchJudge.getJudges();
                                    for (User person : judges) {
                                        for (LaunchJudges deputy_judge : deputy_judges) {
                                            BizObjectModel model = new BizObjectModel();
                                            UserModel assessedPerson = organizationFacade.getUser(person.getId());
                                            DepartmentModel department = organizationFacade.getDepartment(params.getDept().getId());
                                            String name = params.getAnnual() + department.getName() + assessedPerson.getName() + "副职及以上考核";
                                            model.setName(name);
                                            model.setSchemaCode("dept_deputy_assess");
                                            Map<String, Object> data = new HashMap<>();
                                            // 被考核人
                                            List<User> people = new ArrayList<>();
                                            people.add(person);
                                            // 评委
                                            if (null != deputy_judge.getJudges()) {
                                                data.put("judges", JSON.toJSONString(deputy_judge.getJudges()));
                                                data.put("annual", params.getAnnual());
                                                data.put("person", JSON.toJSONString(people));
                                                data.put("personName", assessedPerson.getName());
                                                data.put("assess_name", params.getAssess_name());
                                                data.put("season", params.getSeason());
                                                data.put("dept", JSON.toJSONString(params.getDept()));
                                                BigDecimal weight = null;
                                                if (deputy_judge.getWeight() == null || deputy_judge.getWeight().compareTo(BigDecimal.ZERO) == 0) {
                                                    weight = new BigDecimal("40.1");
                                                } else {
                                                    weight = deputy_judge.getWeight();
                                                }
                                                data.put("weight", weight);
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
                                    }
                                }
                            }
                        }
                        if("科长及以下".equals(launchJudges.get(0).getTable())) {
                            for (LaunchJudges launchJudge : launchJudges) {
                                if (null != launchJudge.getJudges()) {
                                    List<User> judges = launchJudge.getJudges();
                                    for (User person : judges) {
                                        for (LaunchJudges deputy_judge : deputy_judges) {
                                            BizObjectModel model = new BizObjectModel();
                                            UserModel assessedPerson = organizationFacade.getUser(person.getId());
                                            DepartmentModel department = organizationFacade.getDepartment(params.getDept().getId());
                                            String name = params.getAnnual() + department.getName() + assessedPerson.getName() + "副职及以上考核";
                                            model.setName(name);
                                            model.setSchemaCode("section_chief_assess");
                                            Map<String, Object> data = new HashMap<>();
                                            // 被考核人
                                            List<User> people = new ArrayList<>();
                                            people.add(person);

                                            if (null != deputy_judge.getJudges()) {
                                                // 评委
                                                data.put("judges", JSON.toJSONString(deputy_judge.getJudges()));
                                                data.put("annual", params.getAnnual());
                                                data.put("person", JSON.toJSONString(people));
                                                data.put("personName", assessedPerson.getName());
                                                data.put("assess_name", params.getAssess_name());
                                                data.put("season", params.getSeason());
                                                data.put("dept", JSON.toJSONString(params.getDept()));
                                                BigDecimal weight = null;
                                                if (deputy_judge.getWeight() == null || deputy_judge.getWeight().compareTo(BigDecimal.ZERO) == 0) {
                                                    weight = new BigDecimal("40.1");
                                                } else {
                                                    weight = deputy_judge.getWeight();
                                                }
                                                data.put("weight", weight);
                                                data.put("oldParentId", params.getId());
                                                // 将数据写入到model中
                                                model.put(data);

                                                log.info("存入数据库中的数据：" + data);
                                                // 创建机关部门打分表,返回领导人员打分表的id值
                                                String objectId = bizObjectFacade.saveBizObject(userId, model, false);
                                                List<LaunchDeputyAssChild> deptDeputyAssessTables = CreateEvaluationTableUtils.getDeptDeputyAssessTables(section_assesselement, objectId);
                                                //deputyAssessService.insertDeptDeputyAsselement(deptDeputyAssessTables);
                                                deputyAssessService.insertSectionAsselement(deptDeputyAssessTables);
                                                workflowInstanceFacade.startWorkflowInstance(user.getDepartmentId(), user.getId(), "section_chief_fw", objectId, true);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }



        }

        if ("员工能力素质评价".equals(params.getAssess_name())){
            List<LaunchJudges> deputy_judges =  params.getDeputy_judges();
            Map<String, List<LaunchJudges>> collect = deputy_judges.stream().collect(Collectors.groupingBy(LaunchJudges::getMutual));
            List<User> mutual = new ArrayList<>();

            for (List<LaunchJudges> value : collect.values()) {
                //刚进来是 是,参与互评   或者 否
                if ("是".equals(value.get(0).getMutual())) {
                    for (LaunchJudges launchJudges : value) {
                        if (null != launchJudges.getJudges()) {
                            for (User judge : launchJudges.getJudges()) {
                                mutual.add(judge);
                            }
                        }
                    }

                    Map<String, List<LaunchJudges>> collectMutual = value.stream().collect(Collectors.groupingBy(LaunchJudges::getTable));
                    for (List<LaunchJudges> launchJudges : collectMutual.values()) {
                        if("副职及以上".equals(launchJudges.get(0).getTable())){
                            for (LaunchJudges launchJudge : launchJudges) {
                                if (null != launchJudge.getJudges()) {
                                    for (User person : launchJudge.getJudges()) {
                                        BizObjectModel model = new BizObjectModel();
                                        UserModel assessedPerson = organizationFacade.getUser(person.getId());
                                        DepartmentModel department = organizationFacade.getDepartment(params.getDept().getId());
                                        String name = params.getAnnual() + department.getName() + assessedPerson.getName() + "副职及以上考核";
                                        model.setName(name);
                                        model.setSchemaCode("dept_deputy_assess");
                                        Map<String, Object> data = new HashMap<>();
                                        // 被考核人
                                        List<User> people = new ArrayList<>();
                                        people.add(person);
                                        data.put("annual", params.getAnnual());
                                        // 评委
                                        if (null != mutual && mutual.size() != 0) {
                                            data.put("judges", JSON.toJSONString(mutual));
                                            data.put("person", JSON.toJSONString(people));
                                            data.put("personName", assessedPerson.getName());
                                            data.put("assess_name", params.getAssess_name());
                                            data.put("season", params.getSeason());
                                            data.put("dept", JSON.toJSONString(params.getDept()));
                                            data.put("weight", new BigDecimal("40"));
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
                                }
                            }
                        }
                        if("科长及以下".equals(launchJudges.get(0).getTable())){
                            for (LaunchJudges launchJudge : launchJudges) {
                                if (null != launchJudge.getJudges()) {
                                    for (User person : launchJudge.getJudges()) {
                                        BizObjectModel model = new BizObjectModel();
                                        UserModel assessedPerson = organizationFacade.getUser(person.getId());
                                        DepartmentModel department = organizationFacade.getDepartment(params.getDept().getId());
                                        String name = params.getAnnual() + department.getName() + assessedPerson.getName() + "副职及以上考核";
                                        model.setName(name);
                                        model.setSchemaCode("section_chief_assess");
                                        Map<String, Object> data = new HashMap<>();
                                        // 被考核人
                                        List<User> people = new ArrayList<>();
                                        people.add(person);
                                        data.put("annual", params.getAnnual());
                                        if (null != mutual && mutual.size() != 0) {
                                            data.put("judges", JSON.toJSONString(mutual));
                                            data.put("person", JSON.toJSONString(people));
                                            data.put("personName", assessedPerson.getName());
                                            data.put("assess_name", params.getAssess_name());
                                            data.put("season", params.getSeason());
                                            data.put("dept", JSON.toJSONString(params.getDept()));
                                            data.put("weight", new BigDecimal("40"));
                                            data.put("oldParentId", params.getId());
                                            // 将数据写入到model中
                                            model.put(data);

                                            log.info("存入数据库中的数据：" + data);
                                            // 创建机关部门打分表,返回领导人员打分表的id值
                                            String objectId = bizObjectFacade.saveBizObject(userId, model, false);
                                            List<LaunchDeputyAssChild> deptDeputyAssessTables = CreateEvaluationTableUtils.getDeptDeputyAssessTables(section_assesselement, objectId);
                                            deputyAssessService.insertSectionAsselement(deptDeputyAssessTables);
                                            workflowInstanceFacade.startWorkflowInstance(user.getDepartmentId(), user.getId(), "section_chief_fw", objectId, true);
                                        }
                                    }
                                }
                            }
                        }

                    }


                }

            }
            List<User> notMutualJudges = new ArrayList<>();
            for (LaunchJudges deputy_judge : deputy_judges) {
                if ("否".equals(deputy_judge.getMutual()) ){
                    if (null != deputy_judge.getJudges()) {
                        notMutualJudges = deputy_judge.getJudges();
                    }
                }
            }
            for (LaunchJudges deputy_judge : deputy_judges) {
                if ("是".equals(deputy_judge.getMutual()) && "副职及以上".equals(deputy_judge.getTable())) {
                    if(null != deputy_judge.getJudges()){
                        for (User person : deputy_judge.getJudges()) {
                            BizObjectModel model = new BizObjectModel();
                            UserModel assessedPerson = organizationFacade.getUser(person.getId());
                            DepartmentModel department = organizationFacade.getDepartment(params.getDept().getId());
                            String name = params.getAnnual()+department.getName()+assessedPerson.getName()+"副职及以上考核";
                            model.setName(name);
                            model.setSchemaCode("dept_deputy_assess");
                            Map<String, Object> data = new HashMap<>();
                            // 被考核人
                            List<User> people = new ArrayList<>();
                            people.add(person);
                            data.put("annual", params.getAnnual());
                            if (null != notMutualJudges && notMutualJudges.size() != 0) {
                                // 评委
                                data.put("judges", JSON.toJSONString(notMutualJudges));
                                data.put("person", JSON.toJSONString(people));
                                data.put("personName", assessedPerson.getName());
                                data.put("assess_name", params.getAssess_name());
                                data.put("season", params.getSeason());
                                data.put("dept", JSON.toJSONString(params.getDept()));
                                data.put("weight", new BigDecimal("60"));
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
                    }

                }
                if ("是".equals(deputy_judge.getMutual()) && "科长及以下".equals(deputy_judge.getTable())) {
                    if(null != deputy_judge.getJudges()){

                        for (User person : deputy_judge.getJudges()) {
                            BizObjectModel model = new BizObjectModel();
                            UserModel assessedPerson = organizationFacade.getUser(person.getId());
                            DepartmentModel department = organizationFacade.getDepartment(params.getDept().getId());
                            String name = params.getAnnual() + department.getName() + assessedPerson.getName() + "副职及以上考核";
                            model.setName(name);
                            model.setSchemaCode("section_chief_assess");
                            Map<String, Object> data = new HashMap<>();
                            // 被考核人
                            List<User> people = new ArrayList<>();
                            people.add(person);
                            data.put("annual", params.getAnnual());
                            if (null != notMutualJudges && notMutualJudges.size() != 0) {

                                // 评委
                                data.put("judges", JSON.toJSONString(notMutualJudges));
                                data.put("person", JSON.toJSONString(people));
                                data.put("personName", assessedPerson.getName());
                                data.put("assess_name", params.getAssess_name());
                                data.put("season", params.getSeason());
                                data.put("dept", JSON.toJSONString(params.getDept()));
                                data.put("weight", new BigDecimal("60"));
                                data.put("oldParentId", params.getId());
                                // 将数据写入到model中
                                model.put(data);

                                log.info("存入数据库中的数据：" + data);
                                // 创建机关部门打分表,返回领导人员打分表的id值
                                String objectId = bizObjectFacade.saveBizObject(userId, model, false);
                                List<LaunchDeputyAssChild> deptDeputyAssessTables = CreateEvaluationTableUtils.getDeptDeputyAssessTables(section_assesselement, objectId);
                                //deputyAssessService.insertDeptDeputyAsselement(deptDeputyAssessTables);
                                deputyAssessService.insertSectionAsselement(deptDeputyAssessTables);
                                workflowInstanceFacade.startWorkflowInstance(user.getDepartmentId(), user.getId(), "section_chief_fw", objectId, true);
                            }
                        }
                    }
                }
            }
        }

     return this.getOkResponseResult("成功","success");
    }


    @ApiOperation(value = "员工评价发起表结束流程")
    @PostMapping("/finishEvaluation")
    public ResponseResult<Object> finishPerformanceEvaluation(@RequestBody @ApiParam(name = "发起流程信息") LaunchEvaluationRequest params) {
        try{

        for (LaunchJudges deputy_judge : params.getDeputy_judges()) {
                if ("副职及以上".equals(deputy_judge.getTable())) {
                    if (null != deputy_judge.getJudges()) {
                        for (User judge : deputy_judge.getJudges()) {
                            deputyAssessService.insertOrUpdateDeputyAssesment(params.getId(), judge.getId(), params.getAssess_name());
                        }
                    }
                }
                if ("科长及以下".equals(deputy_judge.getTable())){
                    if (null != deputy_judge.getJudges()) {
                        for (User judge : deputy_judge.getJudges()) {
                            deputyAssessService.insertOrUpdateSectionAssesment(params.getId(), judge.getId(), params.getAssess_name());
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("异常",e);
            return this.getErrResponseResult("error",404L, "结束评价出错");
        }
        return this.getOkResponseResult("success", "结束评价成功");

    }



    @GetMapping("/testfinish")
    public ResponseResult<Object> test(String oldParentId,String assessedPersonId,String assessName) {
        try{

                        deputyAssessService.insertOrUpdateSectionAssesment(oldParentId,assessedPersonId,assessName);


        } catch (Exception e) {
            log.error("异常",e);
            return this.getErrResponseResult("error",404L, "结束评价出错");
        }
        return this.getOkResponseResult("success", "结束评价成功");

    }


}





