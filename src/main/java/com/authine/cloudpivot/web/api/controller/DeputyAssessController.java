package com.authine.cloudpivot.web.api.controller;

import com.alibaba.fastjson.JSON;
import com.authine.cloudpivot.engine.api.facade.BizObjectFacade;
import com.authine.cloudpivot.engine.api.facade.OrganizationFacade;
import com.authine.cloudpivot.engine.api.facade.WorkflowInstanceFacade;
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
            for (SubmitDeputyAssChild submitDeputyAssChild : params.getSubmitDeputyAssChildren()) {
                submitDeputyAssChild.setParentId(params.getParentId());
                submitDeputyAssChild.setOldParentId(params.getOldParentId());
                submitDeputyAssChild.setUserId(userId);
                submitDeputyAssChild.setAssessedPersonId(params.getPerson().get(0).getId());
                submitDeputyAssChild.setDeptId(params.getDept().getId());
                submitDeputyAssChild.setAnnual(params.getAnnual());
                submitDeputyAssChild.setWeight(params.getWeight());
                //存储考核明细
                deputyAssessService.insertSubmitDeputyAsselement(submitDeputyAssChild);
            }
            //算分
            deputyAssessService.insertOrUpdateDeputyAssesment(params.getOldParentId(), params.getPerson().get(0).getId());
        } catch (Exception e) {
            log.error("错误信息", e);
            return getErrResponseResult(null, 404L, e.getMessage());
        }

        return getOkResponseResult("success", "成功");


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
            return  getOkResponseResult(depts,"success");
        } catch (Exception e) {
            log.error("错误信息", e);
            return getErrResponseResult(null, 404L, e.getMessage());
        }
    }


    @ApiOperation(value = "筛选框年度初始化")
    @GetMapping("/queryAnnualInit")
    public ResponseResult<Object> queryAnnualDept() {
        List<String> annuals = deputyAssessService.selectAnnualFromLaunch();
        return  getOkResponseResult(annuals,"success");
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
            strings = deputyAssessService.selecAssessedPeopleFromLaunch(id);
        } catch (Exception e) {
            log.error("错误信息", e);
            return getErrResponseResult(null, 404L, e.getMessage());
        }

        return this.getOkResponseResult(strings,"success");
    }

    @ApiOperation(value = "查询考核结果的表头")
    @GetMapping("/queryDeputyHeader")
    public ResponseResult<Object> queryDeputyHeader(
            @ApiParam(name = "部门id") String deptId,
            @ApiParam(name = "被考核人id") String assessedPersonId,
            @ApiParam(name = "年度") String annual
    ){
        List<Header> headViews = null;
        try {
            List<String> strings = deputyAssessService.selectHeaders(deptId, assessedPersonId, annual);
            headViews = new ArrayList<>();
            Header nameheader = new Header();
            Header annualheader = new Header();
            annualheader.setProp("年度");
            annualheader.setLabel("年度");
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
        return this.getOkResponseResult(headViews,"success");
    }

    /**
     * Query deputy result response result.
     *
     * @param deptId           the dept id
     * @param assessedPersonId the assessed person id
     * @return the response result
     */
    @ApiOperation(value = "查询考核结果")
    @GetMapping("/queryDeputyResult")
    public ResponseResult<Object> queryDeputyResult(
                                                    @ApiParam(name = "部门id") String deptId,
                                                    @ApiParam(name = "被考核人id") String assessedPersonId,
                                                    @ApiParam(name = "年度") String annual
                                                    ){
        try {
            if ("".equals(annual)&&!"".equals(deptId)&&!"".equals(assessedPersonId)){
                List<SubmitDeputyAssChild> submitDeputyAssChildren = deputyAssessService.selectAssessByDeptIdAndAssessedPersonIdAndAnnual(deptId, assessedPersonId, annual);
                Map<String, List<SubmitDeputyAssChild>> indexMap = submitDeputyAssChildren.stream().collect(Collectors.groupingBy(SubmitDeputyAssChild::getAnnual));
                List< Map<String,Object>> resultList = new ArrayList<>();
                if (submitDeputyAssChildren.isEmpty()||submitDeputyAssChildren == null){
                    return this.getErrResponseResult(resultList,500L,"结果为空");
                }
                for (List<SubmitDeputyAssChild> value : indexMap.values()) {
                    //第一次进来是三个年度第一个年度
                    Map<String,Object> map = new LinkedHashMap<>();
                    map.put("年度",value.get(0).getAnnual());
                    map.put("姓名",value.get(0).getName());
                    BigDecimal totalScore = new BigDecimal("0");
                    for (SubmitDeputyAssChild submitDeputyAssChild : value) {
                        String assessIndex = submitDeputyAssChild.getAssessIndex();
                        BigDecimal score = submitDeputyAssChild.getScore();
                        map.put(assessIndex,score);
                        totalScore = totalScore.add(score);
                    }
                    map.put("总分",totalScore);
                    resultList.add(map);
                }
                return this.getOkResponseResult(resultList,"success");
            }
            if(!"".equals(annual)&&!"".equals(deptId)&&"".equals(assessedPersonId)){
                List<SubmitDeputyAssChild> submitDeputyAssChildren = deputyAssessService.selectAssessByDeptIdAndAssessedPersonIdAndAnnual(deptId, assessedPersonId, annual);
                Map<String, List<SubmitDeputyAssChild>> indexMap = submitDeputyAssChildren.stream().collect(Collectors.groupingBy(SubmitDeputyAssChild::getAssessedPersonId));
                List< Map<String,Object>> resultList = new ArrayList<>();
                for (List<SubmitDeputyAssChild> value : indexMap.values()) {
                    //第一次进入循环是 某一年度某部门第一个人全部考核项目成绩
                    Map<String,Object> map = new LinkedHashMap<>();
                    map.put("年度",value.get(0).getAnnual());
                    map.put("姓名",value.get(0).getName());
                    BigDecimal totalScore = new BigDecimal("0");
                    for (SubmitDeputyAssChild submitDeputyAssChild : value) {
                        String assessIndex = submitDeputyAssChild.getAssessIndex();
                        BigDecimal score = submitDeputyAssChild.getScore();
                        map.put(assessIndex,score);
                        totalScore = totalScore.add(score);
                    }
                    map.put("总分",totalScore);
                    resultList.add(map);
                }
                return this.getOkResponseResult(resultList,"success");
            }
            if(!"".equals(annual)&&!"".equals(deptId)&&!"".equals(assessedPersonId)){
                List<SubmitDeputyAssChild> submitDeputyAssChildren = deputyAssessService.selectAssessByDeptIdAndAssessedPersonIdAndAnnual(deptId, assessedPersonId, annual);
                Map<String, List<SubmitDeputyAssChild>> indexMap = submitDeputyAssChildren.stream().collect(Collectors.groupingBy(SubmitDeputyAssChild::getAssessedPersonId));
                List< Map<String,Object>> resultList = new ArrayList<>();
                for (List<SubmitDeputyAssChild> value : indexMap.values()) {
                    //第一次进入循环是 某一年度某部门第一个人全部考核项目成绩
                    Map<String,Object> map = new LinkedHashMap<>();
                    map.put("年度",value.get(0).getAnnual());
                    map.put("姓名",value.get(0).getName());
                    BigDecimal totalScore = new BigDecimal("0");
                    for (SubmitDeputyAssChild submitDeputyAssChild : value) {
                        String assessIndex = submitDeputyAssChild.getAssessIndex();
                        BigDecimal score = submitDeputyAssChild.getScore();
                        map.put(assessIndex,score);
                        totalScore = totalScore.add(score);
                    }
                    map.put("总分",totalScore);
                    resultList.add(map);
                }
                return this.getOkResponseResult(resultList,"success");
            }
        } catch (Exception e) {
            log.error("错误信息", e);
            return getErrResponseResult(null, 404L, e.getMessage());        }
        List< Map<String,Object>> emptyResultList = new ArrayList<>();
        return this.getOkResponseResult(emptyResultList,"success");
    }


    @ApiOperation(value = "导出考核结果")
    @GetMapping("/expertDeputyResult")
    public void expertResult(HttpServletResponse response,
                                               @ApiParam(name = "部门id") String deptId,
                                               @ApiParam(name = "被考核人id") String assessedPersonId,
                                               @ApiParam(name = "年度") String annual
    ) throws IOException, ParseException {
        List< Map<String,Object>> resultList = null;
        List<String> strings = null;
        List<String> headers = null;
        try {
            resultList = new ArrayList<>();
            if ("".equals(annual)&&!"".equals(deptId)&&!"".equals(assessedPersonId)){
                List<SubmitDeputyAssChild> submitDeputyAssChildren = deputyAssessService.selectAssessByDeptIdAndAssessedPersonIdAndAnnual(deptId, assessedPersonId, annual);
                Map<String, List<SubmitDeputyAssChild>> indexMap = submitDeputyAssChildren.stream().collect(Collectors.groupingBy(SubmitDeputyAssChild::getAnnual));
                for (List<SubmitDeputyAssChild> value : indexMap.values()) {
                    //第一次进来是三个年度第一个年度
                    Map<String,Object> map = new LinkedHashMap<>();
                    map.put("年度",value.get(0).getAnnual());
                    map.put("姓名",value.get(0).getName());
                    BigDecimal totalScore = new BigDecimal("0");
                    for (SubmitDeputyAssChild submitDeputyAssChild : value) {
                        String assessIndex = submitDeputyAssChild.getAssessIndex();
                        BigDecimal score = submitDeputyAssChild.getScore();
                        map.put(assessIndex,score);
                        totalScore = totalScore.add(score);
                    }
                    map.put("总分",totalScore);
                    resultList.add(map);
                }
            }
            if(!"".equals(annual)&&!"".equals(deptId)&&"".equals(assessedPersonId)){
                List<SubmitDeputyAssChild> submitDeputyAssChildren = deputyAssessService.selectAssessByDeptIdAndAssessedPersonIdAndAnnual(deptId, assessedPersonId, annual);
                Map<String, List<SubmitDeputyAssChild>> indexMap = submitDeputyAssChildren.stream().collect(Collectors.groupingBy(SubmitDeputyAssChild::getAssessedPersonId));
                for (List<SubmitDeputyAssChild> value : indexMap.values()) {
                    //第一次进入循环是 某一年度某部门第一个人全部考核项目成绩
                    Map<String,Object> map = new LinkedHashMap<>();
                    map.put("年度",value.get(0).getAnnual());
                    map.put("姓名",value.get(0).getName());
                    BigDecimal totalScore = new BigDecimal("0");
                    for (SubmitDeputyAssChild submitDeputyAssChild : value) {
                        String assessIndex = submitDeputyAssChild.getAssessIndex();
                        BigDecimal score = submitDeputyAssChild.getScore();
                        map.put(assessIndex,score);
                        totalScore = totalScore.add(score);
                    }
                    map.put("总分",totalScore);
                    resultList.add(map);
                }


            }

            if(!"".equals(annual)&&!"".equals(deptId)&&!"".equals(assessedPersonId)) {
                List<SubmitDeputyAssChild> submitDeputyAssChildren = deputyAssessService.selectAssessByDeptIdAndAssessedPersonIdAndAnnual(deptId, assessedPersonId, annual);
                Map<String, List<SubmitDeputyAssChild>> indexMap = submitDeputyAssChildren.stream().collect(Collectors.groupingBy(SubmitDeputyAssChild::getAssessedPersonId));
                for (List<SubmitDeputyAssChild> value : indexMap.values()) {
                    //第一次进入循环是 某一年度某部门第一个人全部考核项目成绩
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("年度", value.get(0).getAnnual());
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
            }
            strings = deputyAssessService.selectHeaders(deptId, assessedPersonId, annual);
            headers = new ArrayList<>();
            headers.add("年度");
            headers.add("姓名");
            for (String string : strings) {
                headers.add(string);
            }
            headers.add("总分");
        } catch (Exception e) {
            log.error("错误信息", e);
            response.sendError(500, "未查到部门的考核数据,导出失败");
        }

        //exportExcel(response,headers,resultList);

        String id = UUID.randomUUID().toString().replace("-", "");
        String fileName = id + "部门副职及以上领导人员评价表.xls";
        String realPath = new StringBuilder().append("D:")
                .append(File.separator)
                .append("upload")
                .append(File.separator)
                .append(fileName).toString();
        File file = new File(realPath);
        Workbook workbook = null;
        if (resultList.isEmpty()||resultList == null){
            response.sendError(500, "未查到部门的考核数据,导出失败");
        }else{
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
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, strings.size() + 2));
            Row row1 = sheet.createRow(rowNum++);
            Cell cell1 = row1.createCell(0);
            cell1.setCellValue("考核时间：   " + annual);
            cell1.setCellStyle(ExportExcel.createSecondHeadCellStyle(workbook));
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, strings.size() + 2));
            //第三行表头
            Row row2 = sheet.createRow(rowNum++);
            Cell cell20 = row2.createCell(0);
            cell20.setCellValue("年度");
            cell20.setCellStyle(headerStyle);
            Cell cell21 = row2.createCell(1);
            cell21.setCellValue("姓名");
            cell21.setCellStyle(headerStyle);
            for (int i = 2; i < (strings.size() + 2); i++) {
                Cell cell = row2.createCell(i);
                cell.setCellValue("考核内容");
                cell.setCellStyle(headerStyle);
            }
            Cell cellLast = row2.createCell(strings.size() + 2);
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
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 2, strings.size()+1));
            sheet.addMergedRegion(new CellRangeAddress(2, 3, strings.size()+2, strings.size()+2));
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
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                workbook.write(fos);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                fos.close();
            }
        }
        ExportExcel.outputToWeb(realPath, response, workbook);


        }


//    public void exportExcel(HttpServletResponse response, List<String> headers, List<Map<String, Object>> exportDatas) throws IOException, ParseException {
//        String id = UUID.randomUUID().toString().replace("-", "");
//        String fileName = id + "测试.xls";
//        StringBuilder stringBuilder = new StringBuilder();
//        String realPath = stringBuilder.append("D://upload//").append(fileName).toString();
//        File file = new File(realPath);
//        // 不需要标题
//        if (!file.exists()) {
//            file.createNewFile();
//        }
//        String position = "";
//
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//
//        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
////        String startString = format.format(start);
////        String endString = format.format(end);
////        String title = startString + "至" + endString + reportFormCondition.getDepartmentIds()[0] + "全体" + position + "考勤报表";
//        Workbook workbook = new HSSFWorkbook();
//        Sheet sheet = workbook.createSheet("测试");
//        /** 第三步，设置样式以及字体样式*/
//        CellStyle titleStyle = ExportExcel.createTitleCellStyle(workbook);
//        CellStyle headerStyle = ExportExcel.createHeadCellStyle(workbook);
//        int rowNum = 0;
//        // 创建第一页的第一行，索引从0开始
//        Row row0 = sheet.createRow(rowNum++);
//        row0.setHeight((short) 800);// 设置行高
//        Cell c00 = row0.createCell(0);
//        //c00.setCellValue(title);
//        c00.setCellStyle(titleStyle);
//        // 合并单元格，参数依次为起始行，结束行，起始列，结束列 （索引0开始）
//        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headers.size()));//标题合并单元格操作，6为总列数
//        Row row1 = sheet.createRow(rowNum++);
//        row1.setHeight((short) 500);
//        Cell cell = null;
//        for (int i = 0; i < headers.size(); i++) {
//            cell = row1.createCell(i);
//            cell.setCellValue(headers.get(i));
//            cell.setCellStyle(headerStyle);
//        }
//        /**
//         * 设置列值
//         */
//        int rows = 2;
//        for (Map<String, Object> data : exportDatas) {
//            Row row = sheet.createRow(rows++);
//            int initCellNo = 0;
//            int titleSize = headers.size();
//            for (int i = 0; i < titleSize; i++) {
//                String key = headers.get(i);
//                Object object = data.get(key);
//                if (object == null) {
//                    row.createCell(initCellNo).setCellValue("");
//                } else {
//                    row.createCell(initCellNo).setCellValue(String.valueOf(object));
//                }
//                initCellNo++;
//            }
//        }
//        FileOutputStream fos = null;
//        try {
//            fos = new FileOutputStream(file);
//            workbook.write(fos);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            fos.close();
//        }
//        ExportExcel.outputToWeb(realPath, response, workbook);
//
//
//    }





    //===============================================================
    //---------------------------------------------------------------
    //==================================================================







    @ApiOperation(value = "科长考核发起表发起流程")
    @PostMapping("/launchSectionAssess")
    public ResponseResult<Object> launchSectionAssess(@RequestBody @ApiParam(name = "发起流程信息") LaunchSectionAssessRequest params) {
        log.info("发起科长及以下评价");
        if (params == null || StringUtils.isEmpty(params.getId()) || params.getDeputy_assesselement() == null || params.getDeputy_assesselement().size() == 0) {
            return this.getErrResponseResult(null, 404L, "参数为空");
        }
        String id = params.getId();
        List<LaunchDeputyAssChild> deputy_assesselement = params.getDeputy_assesselement();

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
                    model.setSchemaCode("section_chief_assess");
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
                    deputyAssessService.insertSectionAsselement(deptDeputyAssessTables);
                    workflowInstanceFacade.startWorkflowInstance(user.getDepartmentId(), user.getId(), "section_chief_fw", objectId, true);

                }

            }
        } catch (Exception e) {
            log.error("错误信息", e);
            getErrResponseResult(null, 404L, e.getMessage());
        }
        return this.getOkResponseResult(null, "success");
    }


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
            for (SubmitDeputyAssChild submitDeputyAssChild : params.getSubmitDeputyAssChildren()) {
                submitDeputyAssChild.setParentId(params.getParentId());
                submitDeputyAssChild.setOldParentId(params.getOldParentId());
                submitDeputyAssChild.setUserId(userId);
                submitDeputyAssChild.setAssessedPersonId(params.getPerson().get(0).getId());
                submitDeputyAssChild.setDeptId(params.getDept().getId());
                submitDeputyAssChild.setAnnual(params.getAnnual());
                submitDeputyAssChild.setWeight(params.getWeight());
                //存储考核明细
                deputyAssessService.insertSubmitSectionAsselement(submitDeputyAssChild);
            }
            //算分
            deputyAssessService.insertOrUpdateSectionAssesment(params.getOldParentId(), params.getPerson().get(0).getId());
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
            return  getOkResponseResult(depts,"success");
        } catch (Exception e) {
            log.error("错误信息", e);
            return getErrResponseResult(null, 404L, e.getMessage());
        }
    }

    @ApiOperation(value = "筛选框通过部门查询人员")
    @GetMapping("/querySectionAssessedPeople")
    public ResponseResult<Object> querySectionAssessed(@ApiParam(name = "部门id") String id) {
        List<LeadPerson> strings = deputyAssessService.selectSectionAssessedPeopleFromLaunch(id);

        return this.getOkResponseResult(strings,"success");
    }

    @ApiOperation(value = "查询考核结果")
    @GetMapping("/querySectionResult")
    public ResponseResult<Object> querySectionResult(
            @ApiParam(name = "部门id") String deptId,
            @ApiParam(name = "被考核人id") String assessedPersonId,
            @ApiParam(name = "年度") String annual
    ){
        if ("".equals(annual)&&!"".equals(deptId)&&!"".equals(assessedPersonId)){
            List<SubmitDeputyAssChild> submitDeputyAssChildren = deputyAssessService.selectSectionAssessByDeptIdAndAssessedPersonIdAndAnnual(deptId, assessedPersonId, annual);
            Map<String, List<SubmitDeputyAssChild>> indexMap = submitDeputyAssChildren.stream().collect(Collectors.groupingBy(SubmitDeputyAssChild::getAnnual));
            List< Map<String,Object>> resultList = new ArrayList<>();
            if (submitDeputyAssChildren.isEmpty()||submitDeputyAssChildren == null){
                return this.getErrResponseResult(resultList,500L,"结果为空");
            }
            for (List<SubmitDeputyAssChild> value : indexMap.values()) {
                //第一次进来是三个年度第一个年度
                Map<String,Object> map = new LinkedHashMap<>();
                map.put("年度",value.get(0).getAnnual());
                map.put("姓名",value.get(0).getName());
                BigDecimal totalScore = new BigDecimal("0");
                for (SubmitDeputyAssChild submitDeputyAssChild : value) {
                    String assessIndex = submitDeputyAssChild.getAssessIndex();
                    BigDecimal score = submitDeputyAssChild.getScore();
                    map.put(assessIndex,score);
                    totalScore = totalScore.add(score);
                }
                map.put("总分",totalScore);
                resultList.add(map);
            }
            return this.getOkResponseResult(resultList,"success");
        }
        if(!"".equals(annual)&&!"".equals(deptId)&&"".equals(assessedPersonId)){
            List<SubmitDeputyAssChild> submitDeputyAssChildren = deputyAssessService.selectSectionAssessByDeptIdAndAssessedPersonIdAndAnnual(deptId, assessedPersonId, annual);
            Map<String, List<SubmitDeputyAssChild>> indexMap = submitDeputyAssChildren.stream().collect(Collectors.groupingBy(SubmitDeputyAssChild::getAssessedPersonId));
            List< Map<String,Object>> resultList = new ArrayList<>();
            for (List<SubmitDeputyAssChild> value : indexMap.values()) {
                //第一次进入循环是 某一年度某部门第一个人全部考核项目成绩
                Map<String,Object> map = new LinkedHashMap<>();
                map.put("年度",value.get(0).getAnnual());
                map.put("姓名",value.get(0).getName());
                BigDecimal totalScore = new BigDecimal("0");
                for (SubmitDeputyAssChild submitDeputyAssChild : value) {
                    String assessIndex = submitDeputyAssChild.getAssessIndex();
                    BigDecimal score = submitDeputyAssChild.getScore();
                    map.put(assessIndex,score);
                    totalScore = totalScore.add(score);
                }
                map.put("总分",totalScore);
                resultList.add(map);
            }
            return this.getOkResponseResult(resultList,"success");
        }
        if(!"".equals(annual)&&!"".equals(deptId)&&!"".equals(assessedPersonId)){
            List<SubmitDeputyAssChild> submitDeputyAssChildren = deputyAssessService.selectSectionAssessByDeptIdAndAssessedPersonIdAndAnnual(deptId, assessedPersonId, annual);
            Map<String, List<SubmitDeputyAssChild>> indexMap = submitDeputyAssChildren.stream().collect(Collectors.groupingBy(SubmitDeputyAssChild::getAssessedPersonId));
            List< Map<String,Object>> resultList = new ArrayList<>();
            for (List<SubmitDeputyAssChild> value : indexMap.values()) {
                //第一次进入循环是 某一年度某部门第一个人全部考核项目成绩
                Map<String,Object> map = new LinkedHashMap<>();
                map.put("年度",value.get(0).getAnnual());
                map.put("姓名",value.get(0).getName());
                BigDecimal totalScore = new BigDecimal("0");
                for (SubmitDeputyAssChild submitDeputyAssChild : value) {
                    String assessIndex = submitDeputyAssChild.getAssessIndex();
                    BigDecimal score = submitDeputyAssChild.getScore();
                    map.put(assessIndex,score);
                    totalScore = totalScore.add(score);
                }
                map.put("总分",totalScore);
                resultList.add(map);
            }
            return this.getOkResponseResult(resultList,"success");
        }
        List< Map<String,Object>> resultList = new ArrayList<>();
        return null;
    }

    @ApiOperation(value = "查询考核结果的表头")
    @GetMapping("/querySectionHeader")
    public ResponseResult<Object> querySectionHeader(
            @ApiParam(name = "部门id") String deptId,
            @ApiParam(name = "被考核人id") String assessedPersonId,
            @ApiParam(name = "年度") String annual
    ){
        List<String> strings = deputyAssessService.selectSectionHeaders(deptId, assessedPersonId, annual);
        List<Header> headViews = new ArrayList<>();
        Header nameheader = new Header();
        Header annualheader = new Header();
        annualheader.setProp("年度");
        annualheader.setLabel("年度");
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
        return this.getOkResponseResult(headViews,"success");
    }
}
