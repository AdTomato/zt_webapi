package com.authine.cloudpivot.web.api.controller;

import com.alibaba.fastjson.JSON;
import com.authine.cloudpivot.engine.api.facade.BizObjectFacade;
import com.authine.cloudpivot.engine.api.facade.OrganizationFacade;
import com.authine.cloudpivot.engine.api.facade.WorkflowInstanceFacade;
import com.authine.cloudpivot.engine.api.model.organization.DepartmentModel;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.engine.api.model.runtime.BizObjectModel;
import com.authine.cloudpivot.web.api.bean.LeadPerson;
import com.authine.cloudpivot.web.api.bean.User;
import com.authine.cloudpivot.web.api.bean.deputyassess.*;
import com.authine.cloudpivot.web.api.bean.deputyassess.Header;
import com.authine.cloudpivot.web.api.bean.sectionassess.LaunchSectionAssessRequest;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.service.DeputyAssessService;
import com.authine.cloudpivot.web.api.utils.CreateEvaluationTableUtils;
import com.authine.cloudpivot.web.api.utils.ExportExcel;
import com.authine.cloudpivot.web.api.utils.RedisUtils;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The type Deputy assess controller.
 *
 * @Author Asuvera
 * @Date 2020 /7/20 11:24
 * @Version 1.0
 */
@Api(tags = "副职及以上考核接口")
@RestController
@RequestMapping("/ext/deputyLeaderAssess")
@Slf4j
public class DeputyAssessController extends BaseController {
    @Autowired
    private DeputyAssessService deputyAssessService;
    @Autowired
    RedisUtils redisUtils;

    /**
     * Launch deputy assess response result.
     *
     * @param params 发起表提交表单数据
     * @return the response result
     */
    @ApiOperation(value = "副职考核发起表发起流程")
    @PostMapping("/launchDeputyAssess")
    public ResponseResult<Object> launchDeputyAssess(@RequestBody @ApiParam(name = "发起流程信息") LaunchDeputyAssessRequest params) {
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
            return getErrResponseResult(null, 404L, e.getMessage());
        }
        return this.getOkResponseResult("ok", "成功");
    }


    /**
     * Launch deputy assess response result.
     * 评价表提交数据接口,先存储,后计算分数
     *
     * @param params the params
     * @return the response result
     */
    @ApiOperation(value = "副职考核表提交并存储打分")
    @PostMapping("/submitDeputyAssess")
    public ResponseResult<Object> submitDeputyAssess(@RequestBody SubmitDeputyAssessRequest params) {
        log.info("提交部门副职及以上领导人员能力素质评价");
        String userId = this.getUserId();
        if (userId == null) {
            userId = "2c9280a26706a73a016706a93ccf002b";
        }
        OrganizationFacade organizationFacade = super.getOrganizationFacade();
        UserModel user = organizationFacade.getUser(userId);
        synchronized (DeputyAssessController.class) {
            if (redisUtils.hasKey(userId + "-" + params.getOldParentId())) {
                log.info("重复提交数据：" + params);
                log.info("用户" + user.getName() + userId);
                return this.getErrResponseResult(null, 444L, "禁止重复提交");
            } else {
                redisUtils.set(userId + "-" + params.getOldParentId(), 1, 30);
            }
        }
        if (params == null || StringUtils.isEmpty(params.getParentId()) || params.getSubmitDeputyAssChildren() == null || params.getSubmitDeputyAssChildren().size() == 0) {
            return this.getErrResponseResult(null, 404L, "参数为空");
        }

        try {
            List<SubmitDeputyAssChild> list = new ArrayList<>();
            for (SubmitDeputyAssChild submitDeputyAssChild : params.getSubmitDeputyAssChildren()) {
                submitDeputyAssChild.setParentId(params.getParentId());
                submitDeputyAssChild.setOldParentId(params.getOldParentId());
                submitDeputyAssChild.setUserId(userId);
                submitDeputyAssChild.setAssessedPersonId(params.getPerson().get(0).getId());
                submitDeputyAssChild.setDeptId(params.getDept().getId());
                submitDeputyAssChild.setAnnual(params.getAnnual());
                submitDeputyAssChild.setSeason(params.getSeason());
                submitDeputyAssChild.setWeight(params.getWeight());
                list.add(submitDeputyAssChild);
                //存储考核明细
                //deputyAssessService.insertSubmitDeputyAsselement(submitDeputyAssChild);
            }
            deputyAssessService.insertDeputyDetails(list);
            //算分
            //deputyAssessService.insertOrUpdateDeputyAssesment(params.getOldParentId(), params.getPerson().get(0).getId(), params.getAssessName());
        } catch (Exception e) {
            log.error("错误信息", e);
            return getErrResponseResult(null, 404L, e.getMessage());
        }

        return getOkResponseResult("success", "成功");


    }


    @ApiOperation(value = "副职考核表提交并存储打分")
    @PostMapping("/countDeputyAssess")
    public ResponseResult<Object> countDeputyAssess(@RequestBody SubmitDeputyAssessRequest params) {
        log.info("提交部门副职及以上领导人员能力素质评价");
        String userId = this.getUserId();
        if (userId == null) {
            userId = "2c9280a26706a73a016706a93ccf002b";
        }
        return null;
    }



        /**
     * Query deputy dept response result.
     *
     * @return the response result
     */
    @ApiOperation(value = "筛选框部门初始化")
    @GetMapping("/queryDeptInit")
    public ResponseResult<Object> queryDeputyDept() {
        List<Dept> depts = new ArrayList<>();
        List<Dept> deptsEmpty = new ArrayList<>();
        try {
            depts = deputyAssessService.selectDeptFromResult();
            if (depts.isEmpty() || depts == null) {
                return getOkResponseResult(deptsEmpty, "success");
            } else {
                for (Dept dept : depts) {
                    if (dept == null) {
                        depts.remove(dept);
                    }
                }
                if (depts.isEmpty() || depts == null) {
                    return getOkResponseResult(deptsEmpty, "success");
                }
            }

            return getOkResponseResult(depts, "success");
        } catch (Exception e) {
            log.error("错误信息", e);
            return getErrResponseResult(null, 404L, e.getMessage());
        }
    }


    @ApiOperation(value = "筛选框年度初始化")
    @GetMapping("/queryAnnualInit")
    public ResponseResult<Object> queryAnnualDept() {
        List<String> annuals = deputyAssessService.selectAnnualFromLaunch();
        return getOkResponseResult(annuals, "success");
    }

    /**
     * 考核汇总筛选,通过部门id筛选人员
     *
     * @param id 部门id
     * @return response result
     */
    @ApiOperation(value = "筛选框通过部门查询人员")
    @GetMapping("/queryAssessedPeople")
    public ResponseResult<Object> queryDeputyAssessed(@ApiParam(name = "部门id") String id) {
        List<LeadPerson> strings = new ArrayList<>();
        try {
            strings = deputyAssessService.selectAssessedPeopleFromResult(id);
        } catch (Exception e) {
            log.error("错误信息", e);
            return getErrResponseResult(null, 404L, e.getMessage());
        }

        return this.getOkResponseResult(strings, "success");
    }


    @ApiOperation(value = "查询考核结果的表头")
    @GetMapping("/queryDeputyHeader")
    public ResponseResult<Object> queryDeputyHeader(
            @ApiParam(name = "部门id") String deptId,
            @ApiParam(name = "年度") String annual
    ) {
        List<Header> headViews = null;
        try {
            List<String> strings = deputyAssessService.selectHeaders(deptId, annual);
            headViews = new ArrayList<>();
            Header nameheader = new Header();
            Header annualheader = new Header();
            Header seasonheader = new Header();
            annualheader.setProp("年度");
            annualheader.setLabel("年度");
            headViews.add(annualheader);
            seasonheader.setProp("季度");
            seasonheader.setLabel("季度");
            headViews.add(seasonheader);
            nameheader.setLabel("姓名");
            nameheader.setProp("姓名");
            headViews.add(nameheader);
            for (String string : strings) {
                Header header = new Header();
                header.setLabel(string);
                header.setProp(string);
                headViews.add(header);
            }
            Header scoreheader = new Header();
            scoreheader.setLabel("总分");
            scoreheader.setProp("总分");
            headViews.add(scoreheader);
        } catch (Exception e) {
            log.error("错误信息", e);
            return getErrResponseResult(null, 404L, e.getMessage());
        }
        return this.getOkResponseResult(headViews, "success");
    }

    /**
     * Query deputy result response result.
     */
    @ApiOperation(value = "查询部门副职及以上考核结果")
    @GetMapping("/queryDeputyResult")
    public ResponseResult<Object> queryDeputyResult(
            @ApiParam(name = "考核名称") String assessName,
            @ApiParam(name = "查询类型") String type,
            @ApiParam(name = "季度") String season,
            @ApiParam(name = "部门id") String deptId,
            @ApiParam(name = "年度") String annual
    ) {
        List<Map<String, Object>> resultList = new ArrayList<>();

        try {
            if (type == null || "".equals(type) || assessName == null || "".equals(assessName)) {
                return this.getErrResponseResult(null, 500L, "参数为空");
            }
            if ("员工个人业绩评价".equals(assessName) && "季度".equals(type)) {
                List<SubmitDeputyAssChild> submitDeputyAssChildren = deputyAssessService.selectAssessByDeptIdAndAnnualAndSeasonAndAssessName(deptId, annual, season, assessName);

                if (submitDeputyAssChildren.isEmpty() || submitDeputyAssChildren == null) {
                    return this.getErrResponseResult(resultList, 500L, "结果为空");
                }
                Map<String, List<SubmitDeputyAssChild>> indexMap = submitDeputyAssChildren.stream().collect(Collectors.groupingBy(SubmitDeputyAssChild::getAssessedPersonId));
                for (List<SubmitDeputyAssChild> value : indexMap.values()) {
                    //第一次进入循环是 某一次考核某部门第一个人全部考核项目成绩
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("年度", value.get(0).getAnnual());
                    map.put("季度", value.get(0).getSeason());
                    map.put("姓名", value.get(0).getName());
                    BigDecimal totalScore = new BigDecimal("0");
                    for (SubmitDeputyAssChild submitDeputyAssChild : value) {
                        // 考核项
                        String assessIndex = submitDeputyAssChild.getAssessIndex();
                        // 分数
                        BigDecimal score = submitDeputyAssChild.getScore();
                        map.put(assessIndex, score);
                        totalScore = totalScore.add(score);
                    }
                    map.put("总分", totalScore);
                    resultList.add(map);
                }
                return this.getOkResponseResult(resultList, "success");
            }
            if ("员工个人业绩评价".equals(assessName) && "年度".equals(type)) {
//                List<SubmitDeputyAssChild> submitDeputyAssChildren = deputyAssessService.selectAssessByDeptIdAndAnnualAndSeasonAndAssessName(deptId,annual,season,assessName);
                List<SubmitDeputyAssChild> submitDeputyAssChildren = deputyAssessService.selectAssessByDeptIdAndAnnualAndAssessName(deptId, annual, assessName);
                if (submitDeputyAssChildren.isEmpty() || submitDeputyAssChildren == null) {
                    return this.getErrResponseResult(resultList, 500L, "结果为空");
                }
                Map<String, Map<String, Object>> resultMap = new HashMap<>();
                Map<String, List<SubmitDeputyAssChild>> indexMap = submitDeputyAssChildren.stream().collect(Collectors.groupingBy(SubmitDeputyAssChild::getAssessedPersonId));

                Map<String, Map<String, Integer>> numMap = new HashMap<>();
                indexMap.keySet().forEach(assessPersonId -> {
                    // 保存一个人的数据
                    Map<String, Object> map = new LinkedHashMap<>();

                    // 获取单个人的数据
                    List<SubmitDeputyAssChild> value = indexMap.get(assessPersonId);
                    map.put("年度", value.get(0).getAnnual());
                    map.put("季度", "");
                    map.put("姓名", value.get(0).getName());
                    value.forEach(submitDeputyAssChild -> {
                        // 遍历单个人的所有数据

                        // 获取单个人的某个考核项
                        String assessIndex = submitDeputyAssChild.getAssessIndex();

                        // 设置单个人某个考核项被考核了几个季度
                        if (numMap.containsKey(assessPersonId)) {
                            if (numMap.get(assessPersonId).containsKey(assessIndex)) {
                                Integer num = numMap.get(assessPersonId).get(assessIndex);
                                numMap.get(assessPersonId).put(assessIndex, num + 1);
                            } else {
                                numMap.get(assessPersonId).put(assessIndex, 1);
                            }
                        } else {
                            Map<String, Integer> num = new HashMap<>();
                            num.put(assessIndex, 1);
                            numMap.put(assessPersonId, num);
                        }

                        // 存储单个考核项的分数
                        if (map.containsKey(assessIndex)) {
                            BigDecimal score = (BigDecimal) map.get(assessIndex);
                            map.put(assessIndex, submitDeputyAssChild.getScore().add(score));
                        } else {
                            map.put(assessIndex, submitDeputyAssChild.getScore());
                        }
                    });

                    // 根据被考核人的id进行存储被考核人的数据
                    resultMap.put(value.get(0).getAssessedPersonId(), map);
                });

                // 根据被考核人的id进行遍历

                resultMap.keySet().forEach(assessPersonId -> {
                    Map<String, Object> personalData = resultMap.get(assessPersonId);
                    BigDecimal totalScore = new BigDecimal("0");
                    BigDecimal[] total = {totalScore};
                    // 遍历一个人的所有数据
                    personalData.keySet().forEach(key -> {
                        if (numMap.get(assessPersonId).containsKey(key)) {
                            // 算分
                            // 获取单项的分数
                            BigDecimal score = (BigDecimal) personalData.get(key);
                            // 获取单项是几个季度
                            Integer num = numMap.get(assessPersonId).get(key);
                            // 计算分数
                            personalData.put(key, score.divide(new BigDecimal(num + ""), 2, BigDecimal.ROUND_HALF_UP));
                            total[0] = total[0].add(score.divide(new BigDecimal(num + ""), 2, BigDecimal.ROUND_HALF_UP));
                        }
                    });
                    personalData.put("总分", total[0]);
                    resultList.add(personalData);
                });
                return this.getOkResponseResult(resultList, "success");
            }
            if ("员工能力素质评价".equals(assessName) && "年度".equals(type)) {
//                List<SubmitDeputyAssChild> submitDeputyAssChildren = deputyAssessService.selectAssessByDeptIdAndAnnualAndSeasonAndAssessName(deptId,annual,season,assessName);
                List<SubmitDeputyAssChild> submitDeputyAssChildren = deputyAssessService.selectAssessByDeptIdAndAnnualAndAssessName(deptId, annual, assessName);
                if (submitDeputyAssChildren.isEmpty() || submitDeputyAssChildren == null) {
                    return this.getErrResponseResult(resultList, 500L, "结果为空");
                }
                Map<String, List<SubmitDeputyAssChild>> indexMap = submitDeputyAssChildren.stream().collect(Collectors.groupingBy(SubmitDeputyAssChild::getAssessedPersonId));
                for (List<SubmitDeputyAssChild> value : indexMap.values()) {
                    //第一次进入循环是 某一次考核某部门第一个人全部考核项目成绩
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("年度", value.get(0).getAnnual());
                    map.put("季度", "");
                    map.put("姓名", value.get(0).getName());
                    BigDecimal totalScore = new BigDecimal("0");
                    for (SubmitDeputyAssChild submitDeputyAssChild : value) {
                        String assessIndex = submitDeputyAssChild.getAssessIndex();
                        BigDecimal score = submitDeputyAssChild.getScore();
                        map.put(assessIndex, score);
                        totalScore = totalScore.add(score);
                    }
                    map.put("总分", totalScore);
                    resultList.add(map);
                }
                return this.getOkResponseResult(resultList, "success");
            }

        } catch (Exception e) {
            log.error("错误信息", e);
            return getErrResponseResult(null, 404L, e.getMessage());
        }
        List<Map<String, Object>> emptyResultList = new ArrayList<>();
        return this.getOkResponseResult(emptyResultList, "success");
    }


    @ApiOperation(value = "导出考核结果")
    @GetMapping("/expertDeputyResult")
    public void expertResult(HttpServletResponse response,
                             @ApiParam(name = "考核名称") String assessName,
                             @ApiParam(name = "查询类型") String type,
                             @ApiParam(name = "季度") String season,
                             @ApiParam(name = "部门id") String deptId,
                             @ApiParam(name = "年度") String annual
    ) throws IOException {
        List<Map<String, Object>> resultList = new ArrayList<>();
        List<String> strings = new ArrayList<>();
        List<String> headers = new ArrayList<>();
        String timeAndDeptTitle = "";
        OrganizationFacade organizationFacade = super.getOrganizationFacade();
        DepartmentModel department = organizationFacade.getDepartment(deptId);
        try {
            if ("员工个人业绩评价".equals(assessName) && "季度".equals(type)) {
                List<SubmitDeputyAssChild> submitDeputyAssChildren = deputyAssessService.selectAssessByDeptIdAndAnnualAndSeasonAndAssessName(deptId, annual, season, assessName);

                if (submitDeputyAssChildren.isEmpty() || submitDeputyAssChildren == null) {
                }
                Map<String, List<SubmitDeputyAssChild>> indexMap = submitDeputyAssChildren.stream().collect(Collectors.groupingBy(SubmitDeputyAssChild::getAssessedPersonId));
                for (List<SubmitDeputyAssChild> value : indexMap.values()) {
                    //第一次进入循环是 某一次考核某部门第一个人全部考核项目成绩
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("年度", value.get(0).getAnnual());
                    map.put("季度", value.get(0).getSeason());
                    map.put("姓名", value.get(0).getName());
                    BigDecimal totalScore = new BigDecimal("0");
                    for (SubmitDeputyAssChild submitDeputyAssChild : value) {
                        // 考核项
                        String assessIndex = submitDeputyAssChild.getAssessIndex();
                        // 分数
                        BigDecimal score = submitDeputyAssChild.getScore();
                        map.put(assessIndex, score);
                        totalScore = totalScore.add(score);
                    }
                    map.put("总分", totalScore);
                    resultList.add(map);
                }
                timeAndDeptTitle = annual + "年度" + season + assessName + "         " +"部门："+ department.getName();

            }
            if ("员工个人业绩评价".equals(assessName) && "年度".equals(type)) {
                List<SubmitDeputyAssChild> submitDeputyAssChildren = deputyAssessService.selectAssessByDeptIdAndAnnualAndAssessName(deptId, annual, assessName);
                if (submitDeputyAssChildren.isEmpty() || submitDeputyAssChildren == null) {
                }
                Map<String, Map<String, Object>> resultMap = new HashMap<>();
                Map<String, List<SubmitDeputyAssChild>> indexMap = submitDeputyAssChildren.stream().collect(Collectors.groupingBy(SubmitDeputyAssChild::getAssessedPersonId));

                Map<String, Map<String, Integer>> numMap = new HashMap<>();
                indexMap.keySet().forEach(assessPersonId -> {
                    // 保存一个人的数据
                    Map<String, Object> map = new LinkedHashMap<>();

                    // 获取单个人的数据
                    List<SubmitDeputyAssChild> value = indexMap.get(assessPersonId);
                    map.put("年度", value.get(0).getAnnual());
                    map.put("季度", value.get(0).getAnnual());
                    map.put("姓名", value.get(0).getName());
                    value.forEach(submitDeputyAssChild -> {
                        // 遍历单个人的所有数据

                        // 获取单个人的某个考核项
                        String assessIndex = submitDeputyAssChild.getAssessIndex();

                        // 设置单个人某个考核项被考核了几个季度
                        if (numMap.containsKey(assessPersonId)) {
                            if (numMap.get(assessPersonId).containsKey(assessIndex)) {
                                Integer num = numMap.get(assessPersonId).get(assessIndex);
                                numMap.get(assessPersonId).put(assessIndex, num + 1);
                            } else {
                                numMap.get(assessPersonId).put(assessIndex, 1);
                            }
                        } else {
                            Map<String, Integer> num = new HashMap<>();
                            num.put(assessIndex, 1);
                            numMap.put(assessPersonId, num);
                        }

                        // 存储单个考核项的分数
                        if (map.containsKey(assessIndex)) {
                            BigDecimal score = (BigDecimal) map.get(assessIndex);
                            map.put(assessIndex, submitDeputyAssChild.getScore().add(score));
                        } else {
                            map.put(assessIndex, submitDeputyAssChild.getScore());
                        }
                    });

                    // 根据被考核人的id进行存储被考核人的数据
                    resultMap.put(value.get(0).getAssessedPersonId(), map);
                });

                // 根据被考核人的id进行遍历
                resultMap.keySet().forEach(assessPersonId -> {
                    Map<String, Object> personalData = resultMap.get(assessPersonId);
                    BigDecimal totalScore = new BigDecimal("0");
                    BigDecimal[] total = {totalScore};
                    // 遍历一个人的所有数据
                    personalData.keySet().forEach(key -> {
                        if (numMap.get(assessPersonId).containsKey(key)) {
                            // 算分
                            // 获取单项的分数
                            BigDecimal score = (BigDecimal) personalData.get(key);
                            // 获取单项是几个季度
                            Integer num = numMap.get(assessPersonId).get(key);
                            // 计算分数
                            personalData.put(key, score.divide(new BigDecimal(num + ""), 2, BigDecimal.ROUND_HALF_UP));
                            total[0] = total[0].add(score.divide(new BigDecimal(num + ""), 2, BigDecimal.ROUND_HALF_UP));
                        }
                    });
                    personalData.put("总分", total[0]);
                    resultList.add(personalData);
                });
                timeAndDeptTitle = annual + "年度" + assessName + "         " +"部门："+ department.getName();

            }
            if ("员工能力素质评价".equals(assessName) && "年度".equals(type)) {
                List<SubmitDeputyAssChild> submitDeputyAssChildren = deputyAssessService.selectAssessByDeptIdAndAnnualAndAssessName(deptId, annual, assessName);
                if (submitDeputyAssChildren.isEmpty() || submitDeputyAssChildren == null) {
                }
                Map<String, List<SubmitDeputyAssChild>> indexMap = submitDeputyAssChildren.stream().collect(Collectors.groupingBy(SubmitDeputyAssChild::getAssessedPersonId));
                for (List<SubmitDeputyAssChild> value : indexMap.values()) {
                    //第一次进入循环是 某一次考核某部门第一个人全部考核项目成绩
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("年度", value.get(0).getAnnual());
                    map.put("季度", "");
                    map.put("姓名", value.get(0).getName());
                    BigDecimal totalScore = new BigDecimal("0");
                    for (SubmitDeputyAssChild submitDeputyAssChild : value) {
                        String assessIndex = submitDeputyAssChild.getAssessIndex();
                        BigDecimal score = submitDeputyAssChild.getScore();
                        map.put(assessIndex, score);
                        totalScore = totalScore.add(score);
                    }
                    map.put("总分", totalScore);
                    resultList.add(map);
                }
                timeAndDeptTitle = annual + "年度" + assessName + "         " +"部门："+ department.getName();

            }
            strings = deputyAssessService.selectHeaders(deptId, annual);
            headers.add("年度");
            headers.add("季度");
            headers.add("姓名");
            for (String string : strings) {
                headers.add(string);
            }
            headers.add("总分");
            Workbook workbook = null;
            if (resultList.isEmpty() || resultList == null) {
                response.sendError(500, "未查到部门的考核数据,导出失败");
            } else {
                workbook = new HSSFWorkbook();
                Sheet sheet = workbook.createSheet("sheet1");
                /** 第三步，设置样式以及字体样式*/
                CellStyle titleStyle = ExportExcel.createTitleCellStyle(workbook);
                CellStyle headerStyle = ExportExcel.createHeadCellStyle(workbook);
                CellStyle contentCellStyle = ExportExcel.createContentCellStyle(workbook);
                String title = "部门副职及以上领导人员能力素质评价表";
                /** 第四步，创建标题 ,合并标题单元格 */
                // 行号
                int rowNum = 0;
                // 创建第一页的第一行，索引从0开始
                Row row0 = sheet.createRow(rowNum++);
                row0.setHeight((short) 800);// 设置行高
                Cell c00 = row0.createCell(0);
                c00.setCellValue(title);
                c00.setCellStyle(titleStyle);
                // 合并单元格，参数依次为起始行，结束行，起始列，结束列 （索引0开始）
                //标题合并单元格操作，testProjects.size() + 2为总列数
                sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, strings.size() + 3));
                Row row1 = sheet.createRow(rowNum++);
                Cell cell1 = row1.createCell(0);

                cell1.setCellValue(timeAndDeptTitle);
                cell1.setCellStyle(ExportExcel.createSecondHeadCellStyle(workbook));
                sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, strings.size() + 3));
                //第三行表头
                Row row2 = sheet.createRow(rowNum++);
                Cell cell20 = row2.createCell(0);
                cell20.setCellValue("年度");
                cell20.setCellStyle(headerStyle);
                Cell cell21 = row2.createCell(1);
                cell21.setCellValue("季度");
                cell21.setCellStyle(headerStyle);
                Cell cell22 = row2.createCell(2);
                cell22.setCellValue("姓名");
                cell22.setCellStyle(headerStyle);
                for (int i = 3; i < (strings.size() + 3); i++) {
                    Cell cell = row2.createCell(i);
                    cell.setCellValue("考核内容");
                    cell.setCellStyle(headerStyle);
                }
                Cell cellLast = row2.createCell(strings.size() + 3);
                cellLast.setCellValue("总分");
                cellLast.setCellStyle(headerStyle);
                List<Map<String, Object>> listMap = new ArrayList<>();
                Row rowHead = sheet.createRow(rowNum++);
                int cellSize = 0;
                //创建表头
                for (String header : headers) {
                    Cell cell = rowHead.createCell(cellSize++);
                    cell.setCellValue(header);
                    cell.setCellStyle(headerStyle);
                }
                sheet.addMergedRegion(new CellRangeAddress(2, 3, 0, 0));
                sheet.addMergedRegion(new CellRangeAddress(2, 3, 1, 1));
                sheet.addMergedRegion(new CellRangeAddress(2, 2, 3, strings.size() + 2));
                sheet.addMergedRegion(new CellRangeAddress(2, 3, strings.size() + 3, strings.size() + 3));
                //创建表格内容
//            for (Map<String, Object> map : listMap) {
//                Row rowContent = sheet.createRow(rowNum++);
//                int cellNum = 0;
//                for (String key : map.keySet()) {
//                    Cell cell = rowContent.createCell(cellNum++);
//                    cell.setCellValue(map.get(key).toString());
//                    cell.setCellStyle(contentCellStyle);
//                }
//            }
                for (Map<String, Object> data : resultList) {
                    Row row = sheet.createRow(rowNum++);
                    int initCellNo = 0;
                    int titleSize = headers.size();
                    for (int i = 0; i < titleSize; i++) {
                        String key = headers.get(i);
                        Object object = data.get(key);
                        if (object == null) {
                            Cell cell = row.createCell(initCellNo);
                            cell.setCellValue("");
                            cell.setCellStyle(contentCellStyle);
                        } else {
                            Cell cell = row.createCell(initCellNo);
                            cell.setCellValue(String.valueOf(object));
                            cell.setCellStyle(contentCellStyle);
                        }
                        initCellNo++;
                    }
                }
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(response.getOutputStream());
                response.setContentType("application/vnd.ms-excel;charset=gb2312");
                workbook.write(bufferedOutputStream);
                bufferedOutputStream.flush();
                bufferedOutputStream.close();

            }
        } catch (Exception e) {
            log.error("错误信息", e);
            response.sendError(500, "未查到部门的考核数据,导出失败");
        }
    }


    //===============================================================
    //---------------------------------------------------------------
    //==================================================================





























    @ApiOperation(value = "科长考核表提交并存储打分")
    @PostMapping("/submitSectionAssess")
    public ResponseResult<Object> submitSectionAssess(@RequestBody SubmitDeputyAssessRequest params) {
        try {
            log.info("提交科长及以下考核");
            if (params == null || StringUtils.isEmpty(params.getParentId()) || params.getSubmitDeputyAssChildren() == null || params.getSubmitDeputyAssChildren().size() == 0) {
                return this.getErrResponseResult(null, 404L, "参数为空");
            }
            String userId = this.getUserId();
            if (userId == null) {
                userId = "2c9280a26706a73a016706a93ccf002b";
            }
            OrganizationFacade organizationFacade = super.getOrganizationFacade();
            UserModel user = organizationFacade.getUser(userId);
            synchronized (DeputyAssessController.class) {
                if (redisUtils.hasKey(userId + "-" + params.getOldParentId())) {
                    log.info("重复提交数据：" + params);
                    log.info("用户" + user.getName() + userId);
                    return this.getErrResponseResult(null, 444L, "禁止重复提交");
                } else {
                    redisUtils.set(userId + "-" + params.getOldParentId(), 1, 30);
                }
            }
            List<SubmitDeputyAssChild> list = new ArrayList<>();
            for (SubmitDeputyAssChild submitDeputyAssChild : params.getSubmitDeputyAssChildren()) {
                submitDeputyAssChild.setParentId(params.getParentId());
                submitDeputyAssChild.setOldParentId(params.getOldParentId());
                submitDeputyAssChild.setUserId(userId);
                submitDeputyAssChild.setAssessedPersonId(params.getPerson().get(0).getId());
                submitDeputyAssChild.setDeptId(params.getDept().getId());
                submitDeputyAssChild.setAnnual(params.getAnnual());
                submitDeputyAssChild.setSeason(params.getSeason());
                submitDeputyAssChild.setWeight(params.getWeight());
                //存储考核明细
                //deputyAssessService.insertSubmitSectionAsselement(submitDeputyAssChild);
                list.add(submitDeputyAssChild);
            }
            deputyAssessService.insertSectionDetails(list);

            //算分
            //deputyAssessService.insertOrUpdateSectionAssesment(params.getOldParentId(), params.getPerson().get(0).getId(), params.getAssessName());
        } catch (Exception e) {
            log.error("错误信息", e);
                getErrResponseResult(null, 404L, e.getMessage());
        }
        return getOkResponseResult("success", "成功");


    }

    @ApiOperation(value = "科长以下筛选框部门初始化")
    @GetMapping("/querySectionDeptInit")
    public ResponseResult<Object> querySectionDept() {
        List<Dept> depts = new ArrayList<>();
        List<Dept> deptsEmpty = new ArrayList<>();
        try {
            depts = deputyAssessService.selectSectionDeptFromResult();
            if (depts.isEmpty() || depts == null) {
                return getOkResponseResult(deptsEmpty, "success");
            } else {
                for (Dept dept : depts) {
                    if (dept == null) {
                        depts.remove(dept);
                    }
                }
                if (depts.isEmpty() || depts == null) {
                    return getOkResponseResult(deptsEmpty, "success");
                }
            }

            return getOkResponseResult(depts, "success");
        } catch (Exception e) {
            log.error("错误信息", e);
            return getErrResponseResult(null, 404L, e.getMessage());
        }
    }

    @ApiOperation(value = "筛选框通过部门查询人员")
    @GetMapping("/querySectionAssessedPeople")
    public ResponseResult<Object> querySectionAssessed(@ApiParam(name = "部门id") String id) {
        List<LeadPerson> strings = deputyAssessService.selectSectionAssessedPeopleFromResult(id);

        return this.getOkResponseResult(strings, "success");
    }

    @ApiOperation(value = "查询科长考核结果")
    @GetMapping("/querySectionResult")
    public ResponseResult<Object> querySectionResult(
            @ApiParam(name = "考核名称") String assessName,
            @ApiParam(name = "查询类型") String type,
            @ApiParam(name = "季度") String season,
            @ApiParam(name = "部门id") String deptId,
            @ApiParam(name = "年度") String annual
    ) {
        List<Map<String, Object>> resultList = new ArrayList<>();

        try {
            if (type == null || "".equals(type) || assessName == null || "".equals(assessName)) {
                return this.getErrResponseResult(null, 500L, "参数为空");
            }
            if ("员工个人业绩评价".equals(assessName) && "季度".equals(type)) {

                List<SubmitDeputyAssChild> submitDeputyAssChildren = deputyAssessService.selectSectionAssessByDeptIdAndAnnualAndSeasonAndAssessName(deptId, annual, season, assessName);
                if (submitDeputyAssChildren.isEmpty() || submitDeputyAssChildren == null) {
                    return this.getErrResponseResult(resultList, 500L, "结果为空");
                }
                Map<String, List<SubmitDeputyAssChild>> indexMap = submitDeputyAssChildren.stream().collect(Collectors.groupingBy(SubmitDeputyAssChild::getAssessedPersonId));
                for (List<SubmitDeputyAssChild> value : indexMap.values()) {
                    //第一次进入循环是 某一次考核某部门第一个人全部考核项目成绩
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("年度", value.get(0).getAnnual());
                    map.put("季度", value.get(0).getSeason());
                    map.put("姓名", value.get(0).getName());
                    BigDecimal totalScore = new BigDecimal("0");
                    for (SubmitDeputyAssChild submitDeputyAssChild : value) {
                        // 考核项
                        String assessIndex = submitDeputyAssChild.getAssessIndex();
                        // 分数
                        BigDecimal score = submitDeputyAssChild.getScore();
                        map.put(assessIndex, score);
                        totalScore = totalScore.add(score);
                    }
                    map.put("总分", totalScore);
                    resultList.add(map);
                }
                return this.getOkResponseResult(resultList, "success");
            }
            if ("员工个人业绩评价".equals(assessName) && "年度".equals(type)) {
//                List<SubmitDeputyAssChild> submitDeputyAssChildren = deputyAssessService.selectAssessByDeptIdAndAnnualAndSeasonAndAssessName(deptId,annual,season,assessName);
                List<SubmitDeputyAssChild> submitDeputyAssChildren = deputyAssessService.selectSectionAssessByDeptIdAndAnnualAndAssessName(deptId, annual, assessName);
                if (submitDeputyAssChildren.isEmpty() || submitDeputyAssChildren == null) {
                    return this.getErrResponseResult(resultList, 500L, "结果为空");
                }
                Map<String, Map<String, Object>> resultMap = new HashMap<>();
                Map<String, List<SubmitDeputyAssChild>> indexMap = submitDeputyAssChildren.stream().collect(Collectors.groupingBy(SubmitDeputyAssChild::getAssessedPersonId));

                Map<String, Map<String, Integer>> numMap = new HashMap<>();
                indexMap.keySet().forEach(assessPersonId -> {
                    // 保存一个人的数据
                    Map<String, Object> map = new LinkedHashMap<>();

                    // 获取单个人的数据
                    List<SubmitDeputyAssChild> value = indexMap.get(assessPersonId);
                    map.put("年度", value.get(0).getAnnual());
                    map.put("季度", "");
                    map.put("姓名", value.get(0).getName());
                    value.forEach(submitDeputyAssChild -> {
                        // 遍历单个人的所有数据

                        // 获取单个人的某个考核项
                        String assessIndex = submitDeputyAssChild.getAssessIndex();

                        // 设置单个人某个考核项被考核了几个季度
                        if (numMap.containsKey(assessPersonId)) {
                            if (numMap.get(assessPersonId).containsKey(assessIndex)) {
                                Integer num = numMap.get(assessPersonId).get(assessIndex);
                                numMap.get(assessPersonId).put(assessIndex, num + 1);
                            } else {
                                numMap.get(assessPersonId).put(assessIndex, 1);
                            }
                        } else {
                            Map<String, Integer> num = new HashMap<>();
                            num.put(assessIndex, 1);
                            numMap.put(assessPersonId, num);
                        }

                        // 存储单个考核项的分数
                        if (map.containsKey(assessIndex)) {
                            BigDecimal score = (BigDecimal) map.get(assessIndex);
                            map.put(assessIndex, submitDeputyAssChild.getScore().add(score));
                        } else {
                            map.put(assessIndex, submitDeputyAssChild.getScore());
                        }
                    });

                    // 根据被考核人的id进行存储被考核人的数据
                    resultMap.put(value.get(0).getAssessedPersonId(), map);
                });

                // 根据被考核人的id进行遍历

                resultMap.keySet().forEach(assessPersonId -> {
                    Map<String, Object> personalData = resultMap.get(assessPersonId);
                    BigDecimal totalScore = new BigDecimal("0");
                    BigDecimal[] total = {totalScore};
                    // 遍历一个人的所有数据
                    personalData.keySet().forEach(key -> {
                        if (numMap.get(assessPersonId).containsKey(key)) {
                            // 算分
                            // 获取单项的分数
                            BigDecimal score = (BigDecimal) personalData.get(key);
                            // 获取单项是几个季度
                            Integer num = numMap.get(assessPersonId).get(key);
                            // 计算分数
                            personalData.put(key, score.divide(new BigDecimal(num + ""), 2, BigDecimal.ROUND_HALF_UP));
                            total[0] = total[0].add(score.divide(new BigDecimal(num + ""), 2, BigDecimal.ROUND_HALF_UP));
                        }
                    });
                    personalData.put("总分", total[0]);
                    resultList.add(personalData);
                });
                return this.getOkResponseResult(resultList, "success");
            }
            if ("员工能力素质评价".equals(assessName) && "年度".equals(type)) {
//                List<SubmitDeputyAssChild> submitDeputyAssChildren = deputyAssessService.selectAssessByDeptIdAndAnnualAndSeasonAndAssessName(deptId,annual,season,assessName);
                List<SubmitDeputyAssChild> submitDeputyAssChildren = deputyAssessService.selectSectionAssessByDeptIdAndAnnualAndAssessName(deptId, annual, assessName);
                if (submitDeputyAssChildren.isEmpty() || submitDeputyAssChildren == null) {
                    return this.getErrResponseResult(resultList, 500L, "结果为空");
                }
                Map<String, List<SubmitDeputyAssChild>> indexMap = submitDeputyAssChildren.stream().collect(Collectors.groupingBy(SubmitDeputyAssChild::getAssessedPersonId));
                for (List<SubmitDeputyAssChild> value : indexMap.values()) {
                    //第一次进入循环是 某一次考核某部门第一个人全部考核项目成绩
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("年度", value.get(0).getAnnual());
                    map.put("季度", "");
                    map.put("姓名", value.get(0).getName());
                    BigDecimal totalScore = new BigDecimal("0");
                    for (SubmitDeputyAssChild submitDeputyAssChild : value) {
                        String assessIndex = submitDeputyAssChild.getAssessIndex();
                        BigDecimal score = submitDeputyAssChild.getScore();
                        map.put(assessIndex, score);
                        totalScore = totalScore.add(score);
                    }
                    map.put("总分", totalScore);
                    resultList.add(map);
                }
                return this.getOkResponseResult(resultList, "success");
            }

        } catch (Exception e) {
            log.error("错误信息", e);
            return getErrResponseResult(null, 404L, e.getMessage());
        }
        List<Map<String, Object>> emptyResultList = new ArrayList<>();
        return this.getOkResponseResult(emptyResultList, "success");
    }

    @ApiOperation(value = "查询科长考核结果的表头")
    @GetMapping("/querySectionHeader")
    public ResponseResult<Object> querySectionHeader(
            @ApiParam(name = "部门id") String deptId,
            @ApiParam(name = "年度") String annual
    ) {

        List<Header> headViews = null;
        try {
            List<String> strings = deputyAssessService.selectSectionHeaders(deptId, annual);
            headViews = new ArrayList<>();
            Header nameheader = new Header();
            Header annualheader = new Header();
            Header seasonheader = new Header();
            annualheader.setProp("年度");
            annualheader.setLabel("年度");
            headViews.add(annualheader);
            seasonheader.setProp("季度");
            seasonheader.setLabel("季度");
            headViews.add(seasonheader);
            nameheader.setLabel("姓名");
            nameheader.setProp("姓名");
            headViews.add(nameheader);
            for (String string : strings) {
                Header header = new Header();
                header.setLabel(string);
                header.setProp(string);
                headViews.add(header);
            }
            Header scoreheader = new Header();
            scoreheader.setLabel("总分");
            scoreheader.setProp("总分");
            headViews.add(scoreheader);
        } catch (Exception e) {
            log.error("错误信息", e);
            return getErrResponseResult(null, 404L, e.getMessage());
        }
        return this.getOkResponseResult(headViews, "success");
    }

    @ApiOperation(value = "导出科长考核结果")
    @GetMapping("/exportSectionResult")
    public void exportSectionResult(HttpServletResponse response,
                                    @ApiParam(name = "考核名称") String assessName,
                                    @ApiParam(name = "查询类型") String type,
                                    @ApiParam(name = "季度") String season,
                                    @ApiParam(name = "部门id") String deptId,
                                    @ApiParam(name = "年度") String annual
    ) throws IOException {
        List<Map<String, Object>> resultList = new ArrayList<>();
        List<String> strings = new ArrayList<>();
        List<String> headers = new ArrayList<>();
        String timeAndDeptTitle = "";
        OrganizationFacade organizationFacade = super.getOrganizationFacade();
        DepartmentModel department = organizationFacade.getDepartment(deptId);
        try {
            if ("员工个人业绩评价".equals(assessName) && "季度".equals(type)) {
                List<SubmitDeputyAssChild> submitDeputyAssChildren = deputyAssessService.selectSectionAssessByDeptIdAndAnnualAndSeasonAndAssessName(deptId, annual, season, assessName);

                if (submitDeputyAssChildren.isEmpty() || submitDeputyAssChildren == null) {
                }
                Map<String, List<SubmitDeputyAssChild>> indexMap = submitDeputyAssChildren.stream().collect(Collectors.groupingBy(SubmitDeputyAssChild::getAssessedPersonId));
                for (List<SubmitDeputyAssChild> value : indexMap.values()) {
                    //第一次进入循环是 某一次考核某部门第一个人全部考核项目成绩
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("年度", value.get(0).getAnnual());
                    map.put("季度", value.get(0).getSeason());
                    map.put("姓名", value.get(0).getName());
                    BigDecimal totalScore = new BigDecimal("0");
                    for (SubmitDeputyAssChild submitDeputyAssChild : value) {
                        // 考核项
                        String assessIndex = submitDeputyAssChild.getAssessIndex();
                        // 分数
                        BigDecimal score = submitDeputyAssChild.getScore();
                        map.put(assessIndex, score);
                        totalScore = totalScore.add(score);
                    }
                    map.put("总分", totalScore);
                    resultList.add(map);
                }
                timeAndDeptTitle = annual + "年度" + season + assessName + "         "+"部门：" + department.getName();

            }
            if ("员工个人业绩评价".equals(assessName) && "年度".equals(type)) {
                List<SubmitDeputyAssChild> submitDeputyAssChildren = deputyAssessService.selectSectionAssessByDeptIdAndAnnualAndAssessName(deptId, annual, assessName);
                if (submitDeputyAssChildren.isEmpty() || submitDeputyAssChildren == null) {
                }
                Map<String, Map<String, Object>> resultMap = new HashMap<>();
                Map<String, List<SubmitDeputyAssChild>> indexMap = submitDeputyAssChildren.stream().collect(Collectors.groupingBy(SubmitDeputyAssChild::getAssessedPersonId));

                Map<String, Map<String, Integer>> numMap = new HashMap<>();
                indexMap.keySet().forEach(assessPersonId -> {
                    // 保存一个人的数据
                    Map<String, Object> map = new LinkedHashMap<>();

                    // 获取单个人的数据
                    List<SubmitDeputyAssChild> value = indexMap.get(assessPersonId);
                    map.put("年度", value.get(0).getAnnual());
                    map.put("季度", value.get(0).getAnnual());
                    map.put("姓名", value.get(0).getName());
                    value.forEach(submitDeputyAssChild -> {
                        // 遍历单个人的所有数据

                        // 获取单个人的某个考核项
                        String assessIndex = submitDeputyAssChild.getAssessIndex();

                        // 设置单个人某个考核项被考核了几个季度
                        if (numMap.containsKey(assessPersonId)) {
                            if (numMap.get(assessPersonId).containsKey(assessIndex)) {
                                Integer num = numMap.get(assessPersonId).get(assessIndex);
                                numMap.get(assessPersonId).put(assessIndex, num + 1);
                            } else {
                                numMap.get(assessPersonId).put(assessIndex, 1);
                            }
                        } else {
                            Map<String, Integer> num = new HashMap<>();
                            num.put(assessIndex, 1);
                            numMap.put(assessPersonId, num);
                        }

                        // 存储单个考核项的分数
                        if (map.containsKey(assessIndex)) {
                            BigDecimal score = (BigDecimal) map.get(assessIndex);
                            map.put(assessIndex, submitDeputyAssChild.getScore().add(score));
                        } else {
                            map.put(assessIndex, submitDeputyAssChild.getScore());
                        }
                    });

                    // 根据被考核人的id进行存储被考核人的数据
                    resultMap.put(value.get(0).getAssessedPersonId(), map);
                });

                // 根据被考核人的id进行遍历
                resultMap.keySet().forEach(assessPersonId -> {
                    Map<String, Object> personalData = resultMap.get(assessPersonId);
                    BigDecimal totalScore = new BigDecimal("0");
                    BigDecimal[] total = {totalScore};
                    // 遍历一个人的所有数据
                    personalData.keySet().forEach(key -> {
                        if (numMap.get(assessPersonId).containsKey(key)) {
                            // 算分
                            // 获取单项的分数
                            BigDecimal score = (BigDecimal) personalData.get(key);
                            // 获取单项是几个季度
                            Integer num = numMap.get(assessPersonId).get(key);
                            // 计算分数
                            personalData.put(key, score.divide(new BigDecimal(num + ""), 2, BigDecimal.ROUND_HALF_UP));
                            total[0] = total[0].add(score.divide(new BigDecimal(num + ""), 2, BigDecimal.ROUND_HALF_UP));
                        }
                    });
                    personalData.put("总分", total[0]);
                    resultList.add(personalData);
                });
                timeAndDeptTitle = annual + "年度" + assessName + "         " +"部门："+ department.getName();

            }
            if ("员工能力素质评价".equals(assessName) && "年度".equals(type)) {
                List<SubmitDeputyAssChild> submitDeputyAssChildren = deputyAssessService.selectSectionAssessByDeptIdAndAnnualAndAssessName(deptId, annual, assessName);
                if (submitDeputyAssChildren.isEmpty() || submitDeputyAssChildren == null) {
                }
                Map<String, List<SubmitDeputyAssChild>> indexMap = submitDeputyAssChildren.stream().collect(Collectors.groupingBy(SubmitDeputyAssChild::getAssessedPersonId));
                for (List<SubmitDeputyAssChild> value : indexMap.values()) {
                    //第一次进入循环是 某一次考核某部门第一个人全部考核项目成绩
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("年度", value.get(0).getAnnual());
                    map.put("季度", "");
                    map.put("姓名", value.get(0).getName());
                    BigDecimal totalScore = new BigDecimal("0");
                    for (SubmitDeputyAssChild submitDeputyAssChild : value) {
                        String assessIndex = submitDeputyAssChild.getAssessIndex();
                        BigDecimal score = submitDeputyAssChild.getScore();
                        map.put(assessIndex, score);
                        totalScore = totalScore.add(score);
                    }
                    map.put("总分", totalScore);
                    resultList.add(map);
                }
                timeAndDeptTitle = annual + "年度" + assessName + "         " +"部门："+ department.getName();

            }
            strings = deputyAssessService.selectSectionHeaders(deptId, annual);
            headers.add("年度");
            headers.add("季度");
            headers.add("姓名");
            for (String string : strings) {
                headers.add(string);
            }
            headers.add("总分");
            Workbook workbook = null;
            if (resultList.isEmpty() || resultList == null) {
                response.sendError(500, "未查到部门的考核数据,导出失败");
            } else {
                workbook = new HSSFWorkbook();
                Sheet sheet = workbook.createSheet("sheet1");
                /** 第三步，设置样式以及字体样式*/
                CellStyle titleStyle = ExportExcel.createTitleCellStyle(workbook);
                CellStyle headerStyle = ExportExcel.createHeadCellStyle(workbook);
                CellStyle contentCellStyle = ExportExcel.createContentCellStyle(workbook);
                String title = "科长及以下岗位人员业绩评价表";
                /** 第四步，创建标题 ,合并标题单元格 */
                // 行号
                int rowNum = 0;
                // 创建第一页的第一行，索引从0开始
                Row row0 = sheet.createRow(rowNum++);
                row0.setHeight((short) 800);// 设置行高
                Cell c00 = row0.createCell(0);
                c00.setCellValue(title);
                c00.setCellStyle(titleStyle);
                // 合并单元格，参数依次为起始行，结束行，起始列，结束列 （索引0开始）
                //标题合并单元格操作，testProjects.size() + 2为总列数
                sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, strings.size() + 3));
                Row row1 = sheet.createRow(rowNum++);
                Cell cell1 = row1.createCell(0);

                cell1.setCellValue(timeAndDeptTitle);
                cell1.setCellStyle(ExportExcel.createSecondHeadCellStyle(workbook));
                sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, strings.size() + 3));
                //第三行表头
                Row row2 = sheet.createRow(rowNum++);
                Cell cell20 = row2.createCell(0);
                cell20.setCellValue("年度");
                cell20.setCellStyle(headerStyle);
                Cell cell21 = row2.createCell(1);
                cell21.setCellValue("季度");
                cell21.setCellStyle(headerStyle);
                Cell cell22 = row2.createCell(2);
                cell22.setCellValue("姓名");
                cell22.setCellStyle(headerStyle);
                for (int i = 3; i < (strings.size() + 3); i++) {
                    Cell cell = row2.createCell(i);
                    cell.setCellValue("考核内容");
                    cell.setCellStyle(headerStyle);
                }
                Cell cellLast = row2.createCell(strings.size() + 3);
                cellLast.setCellValue("总分");
                cellLast.setCellStyle(headerStyle);
                List<Map<String, Object>> listMap = new ArrayList<>();
                Row rowHead = sheet.createRow(rowNum++);
                int cellSize = 0;
                //创建表头
                for (String header : headers) {
                    Cell cell = rowHead.createCell(cellSize++);
                    cell.setCellValue(header);
                    cell.setCellStyle(headerStyle);
                }
                sheet.addMergedRegion(new CellRangeAddress(2, 3, 0, 0));
                sheet.addMergedRegion(new CellRangeAddress(2, 3, 1, 1));
                sheet.addMergedRegion(new CellRangeAddress(2, 2, 3, strings.size() + 2));
                sheet.addMergedRegion(new CellRangeAddress(2, 3, strings.size() + 3, strings.size() + 3));
                //创建表格内容
//            for (Map<String, Object> map : listMap) {
//                Row rowContent = sheet.createRow(rowNum++);
//                int cellNum = 0;
//                for (String key : map.keySet()) {
//                    Cell cell = rowContent.createCell(cellNum++);
//                    cell.setCellValue(map.get(key).toString());
//                    cell.setCellStyle(contentCellStyle);
//                }
//            }
                for (Map<String, Object> data : resultList) {
                    Row row = sheet.createRow(rowNum++);
                    int initCellNo = 0;
                    int titleSize = headers.size();
                    for (int i = 0; i < titleSize; i++) {
                        String key = headers.get(i);
                        Object object = data.get(key);
                        if (object == null) {
                            Cell cell = row.createCell(initCellNo);
                            cell.setCellValue("");
                            cell.setCellStyle(contentCellStyle);
                        } else {
                            Cell cell = row.createCell(initCellNo);
                            cell.setCellValue(String.valueOf(object));
                            cell.setCellStyle(contentCellStyle);
                        }
                        initCellNo++;
                    }
                }
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(response.getOutputStream());
                response.setContentType("application/vnd.ms-excel;charset=gb2312");
                workbook.write(bufferedOutputStream);
                bufferedOutputStream.flush();
                bufferedOutputStream.close();

            }
        } catch (Exception e) {
            log.error("错误信息", e);
            response.sendError(500, "未查到部门的考核数据,导出失败");
        }
    }
}

