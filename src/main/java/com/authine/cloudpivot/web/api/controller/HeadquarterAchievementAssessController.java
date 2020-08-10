package com.authine.cloudpivot.web.api.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.authine.cloudpivot.engine.api.facade.OrganizationFacade;
import com.authine.cloudpivot.engine.api.facade.WorkflowInstanceFacade;
import com.authine.cloudpivot.engine.api.model.organization.DepartmentModel;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.engine.enums.ErrCode;
import com.authine.cloudpivot.web.api.bean.*;
import com.authine.cloudpivot.web.api.bean.vo.HeadView;
import com.authine.cloudpivot.web.api.constants.Constants;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.dto.DeptDto;
import com.authine.cloudpivot.web.api.dto.DeptPerformanceAssessDto;
import com.authine.cloudpivot.web.api.dto.TestProject;
import com.authine.cloudpivot.web.api.service.HeadquarterAchievementAssessService;
import com.authine.cloudpivot.web.api.utils.DataSetUtils;
import com.authine.cloudpivot.web.api.utils.DoubleUtils;
import com.authine.cloudpivot.web.api.utils.ExportExcel;
import com.authine.cloudpivot.web.api.utils.UserUtils;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

/**
 * @ClassName HeadquarterAchievementAssessController
 * @author: lfh
 * @Date:2020/7/20 11:50
 * @Description: 总部部门业绩评价表控制层
 **/
@Api(tags = "总部部门业绩评价接口")
@RestController
@Slf4j
@RequestMapping("ext/headquarterAchievementAssess")
public class HeadquarterAchievementAssessController extends BaseController {

    @Resource
    private HeadquarterAchievementAssessService headquarterAchievementAssessService;
    /**
     * 发待办
     * @param beginAchievementAssess
     * @return
     */
    @PostMapping("/sendPending")
    public ResponseResult<Object> sendPingding(@RequestBody BeginAchievementAssess beginAchievementAssess) {

        //有关组织机构的引擎类
        OrganizationFacade organizationFacade = super.getOrganizationFacade();

        // 创建流程的引擎类
        WorkflowInstanceFacade workflowInstanceFacade = super.getWorkflowInstanceFacade();

        // 当前用户id
        String userId = UserUtils.getUserId(getUserId());
        UserModel userModel = this.getOrganizationFacade().getUser(userId);
        DepartmentModel departmentModel = this.getOrganizationFacade().getDepartment(userModel.getDepartmentId());
        //考核项目
        List<AssessmentProject> assessmentProjects = beginAchievementAssess.getAssessmentProjects();
        if (assessmentProjects.isEmpty() || assessmentProjects == null){
            log.info("没有考核项");
            return this.getOkResponseResult(500, "没有考核项");
        }
        //用于存总部部门业绩评价数据
        List<HeadquarterAchievement> headquarterAchievements = new ArrayList<>();
        //评委权重子表
        List<JudgesWeight> judgesWeights = beginAchievementAssess.getJudgesWeights();
        if (judgesWeights.isEmpty() || judgesWeights == null){
            log.info("没有评委或者权重");
            return this.getOkResponseResult(500, "没有评委或者权重");
        }
        //遍历发起的子表评委权重
        for (JudgesWeight judgesWeight : judgesWeights) {
            List<User> judges = JSONArray.parseArray(judgesWeight.getJudges(), User.class);
            try {
                for (User judge : judges) {
                    HeadquarterAchievement headquarterAchievement = new HeadquarterAchievement();
                    headquarterAchievement.setAssessmentDeptment(JSON.toJSONString(Arrays.asList(beginAchievementAssess.getAssessmentDepartment())));
                    headquarterAchievement.setAssessmentDate(beginAchievementAssess.getAssessmentDate());
                    DataSetUtils.setBaseData(headquarterAchievement, userModel, departmentModel, "总部部门业绩评价表", Constants.PROCESSING_STATUS);
                    headquarterAchievement.setJudges(JSON.toJSONString(Arrays.asList(judge)));
                    headquarterAchievement.setWeight(judgesWeight.getWeight());
                    headquarterAchievement.setSendFormId(beginAchievementAssess.getId());
                    headquarterAchievements.add(headquarterAchievement);
                }
            }catch (NullPointerException npe){
                npe.printStackTrace();
                return this.getOkResponseResult(500, "存在未填项");
            } catch (ClassCastException cce){
                cce.printStackTrace();
                return this.getOkResponseResult(500, "格式错误");
            }catch (Exception e){
                e.printStackTrace();
                return this.getOkResponseResult(500, "其他错误");
            }
        }
        try {
            log.info("开始插入总部部门业绩评价表主表数据");
            //批量插入总部部门业绩评价表主表数据
            headquarterAchievementAssessService.addHeadquarterAchievements(headquarterAchievements);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("插入失败");
            return this.getErrResponseResult("", 500L, "总部部门业绩评价表主表数据");
        }
        log.info("开始创建子表数据");

        List<AssessmentProject> assessmentProjectList = new ArrayList<>();
        log.info("创建总部部门业绩评价表子表-考核项目数据");
        for (HeadquarterAchievement achievement : headquarterAchievements) {
            for (AssessmentProject project : assessmentProjects) {
                AssessmentProject assessmentProject = new AssessmentProject();
                String id = UUID.randomUUID().toString().replace("-", "");
                assessmentProject.setId(id);
                assessmentProject.setParentId(achievement.getId());
                assessmentProject.setAssessmentContent(project.getAssessmentContent());
                assessmentProject.setAssessmentProject(project.getAssessmentProject());
                assessmentProjectList.add(assessmentProject);
            }
        }
        try {
            log.info("开始插入总部部门业绩评价表子表考核项目的数据");
            headquarterAchievementAssessService.addAssessmentProjects(assessmentProjectList);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("插入总部部门业绩评价表子表考核项目的数据失败");
            return this.getErrResponseResult("", 500L, "开始插入总部部门业绩评价表子表考核项目的数据");
        }
        log.info("当前操作的用户id为" + userId);
        // 用户类
        // 开启流程
        UserModel user = organizationFacade.getUser(userId);
        // 开启机关部门打分表流程
        log.info("开启流程");
        for (HeadquarterAchievement achievement : headquarterAchievements) {
            workflowInstanceFacade.startWorkflowInstance(user.getDepartmentId(), user.getId(), "dept_performance_access", achievement.getId(), true);
        }
        return getOkResponseResult("success", ErrCode.OK.getErrMsg());
    }

    /**
     * 计算
     */
    @PostMapping("/queryResult")
    public ResponseResult<Object> queryResult(String year, @RequestParam(required = false) String deptId,
                                              @RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize) {

        if (year == null || "".equals(year)){
            return this.getOkResponseResult(500, "考核年度不能为空");
        }
        //根据id 查询所有总部部门评价表数据
        year = new StringBuilder().append(year).append("-01-01").toString();
        LocalDate localDate = LocalDate.parse(year);
        List<Map<String, Object>> resultList = new ArrayList<>();
        PageHelper.startPage(pageNum, pageSize);
        log.info("根据时间和部门id查询所有的考核");
        List<DeptPerformanceAssessDto> deptPerformanceAssessDtoList = headquarterAchievementAssessService.queryAllDeptAchievement(localDate, deptId);
        if (deptPerformanceAssessDtoList.isEmpty() || deptPerformanceAssessDtoList == null){
            return this.getOkResponseResult(500, "无考核数据");
        }
        log.info("开始计算总分");
        for (DeptPerformanceAssessDto deptPerformanceAssessDto : deptPerformanceAssessDtoList) {
            Map<String, Object> resultMap = new HashMap<>();
            String dept = deptPerformanceAssessDto.getDept();
            resultMap.put("dept", dept);
            String date = Integer.toString(deptPerformanceAssessDto.getDate().atZone(ZoneId.systemDefault()).toLocalDate().getYear());
            resultMap.put("date", date);
            Double resultScore = 0D;
            List<TestProject> testProjects = deptPerformanceAssessDto.getTestProjects();
            for (TestProject testProject : testProjects) {
                resultMap.put(testProject.getAssessmentProject(), testProject.getScore());
                resultScore += testProject.getScore();
            }
            resultScore = DoubleUtils.doubleRound(resultScore, 1);
            resultMap.put("resultScore", resultScore);
            resultList.add(resultMap);
        }
        PageInfo pageInfo = new PageInfo(resultList);
        return this.getOkResponseResult(pageInfo, "success");
    }

    @PostMapping("/queryHeader")
    public ResponseResult<Object> queryHeader(String year, @RequestParam(value = "deptId", required = false) String deptId) throws ParseException {
        year = new StringBuilder().append(year).append("-01-01").toString();
        LocalDate localDate = LocalDate.parse(year);
        List<String> headers = headquarterAchievementAssessService.queryHeader(localDate, deptId);
        Map<String, Object> headerMap = new HashMap<>();
        List<HeadView> headViews = new ArrayList<>();
        headerMap.put("被考核部门", "dept");
        HeadView headViewDept = new HeadView();
        HeadView headViewSCore = new HeadView();
        headViewDept.setProp("dept");
        headViewDept.setLabel("被考核部门");
        headViews.add(headViewDept);
        for (String header : headers) {
            HeadView headViewContent = new HeadView();
            headViewContent.setLabel(header);
            headViewContent.setProp(header);
            headViews.add(headViewContent);
            headerMap.put(header, header);
        }
        headViewSCore.setLabel("总分");
        headViewSCore.setProp("resultScore");
        headViews.add(headViewSCore);
        headerMap.put("总分", "resultScore");
        return this.getOkResponseResult(headViews, "success");
    }

    /**
     * 查询考核部门接口
     */
    @GetMapping("/queryDepts")
    public ResponseResult<Object> queryDepts() {
        List<DeptDto> deptDtos = headquarterAchievementAssessService.queryAllAssessDepts();
        return this.getOkResponseResult(deptDtos, "success");
    }

    /**
     * 根据部门id查询考核的年度事件
     */
    @GetMapping("/queryDateByDeptId")
    public ResponseResult<Object> queryDateByDeptId(String deptId) {

        List<Instant> dateList = headquarterAchievementAssessService.queryDateByDeptId(deptId);
        if (dateList == null || dateList.isEmpty()) {
            return this.getErrResponseResult("", 500L, "未查到该部门的考核时间");
        }
        List<String> yearList = new ArrayList<>();
        for (Instant instant : dateList) {
            LocalDate localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
            String year = Integer.toString(localDate.getYear());
            yearList.add(year);
        }
        return this.getOkResponseResult(yearList, "success");
    }

    //根据年度查询所有的考核部门
    @GetMapping("/queryDeptListByYear")
    public ResponseResult<Object> queryDeptListByYear(String year){
        year = new StringBuilder().append(year).append("-01-01").toString();
        LocalDate localDate = LocalDate.parse(year);
        List<DeptDto> deptDtos = headquarterAchievementAssessService.queryDeptListByYear(localDate);
        return this.getOkResponseResult(deptDtos, "success");
    }

    /**
     * 导出功能
     */
    @PostMapping("/exportsResult")
    public void exportsResult(@RequestParam(required = false) String year,
                              @RequestParam(required = false) String deptId, HttpServletResponse response) throws IOException {
        String id = UUID.randomUUID().toString().replace("-", "");
        String fileName = id + "总部部门业绩评价表.xls";
        String realPath = new StringBuilder().append("D:")
                .append(File.separator)
                .append("upload")
                .append(File.separator)
                .append(fileName).toString();
        File file = new File(realPath);
        // 不需要标题
        if (!file.exists()) {
            file.createNewFile();
        }
        Workbook workbook = null;
        year = new StringBuilder().append(year).append("-01-01").toString();
        LocalDate localDate = LocalDate.parse(year);
        List<DeptPerformanceAssessDto> deptPerformanceAssessDtoList = headquarterAchievementAssessService.queryAllDeptAchievement(localDate, deptId);
        if (deptPerformanceAssessDtoList == null || deptPerformanceAssessDtoList.isEmpty()) {
            response.sendError(500, "未查到部门的考核数据,导出失败");
        } else {
            DeptPerformanceAssessDto deptPerformanceAssess = deptPerformanceAssessDtoList.get(0);
            List<TestProject> testProjects = deptPerformanceAssess.getTestProjects();
            workbook = new HSSFWorkbook();
            Sheet sheet = workbook.createSheet("sheet1");
            /** 第三步，设置样式以及字体样式*/
            CellStyle titleStyle = ExportExcel.createTitleCellStyle(workbook);
            CellStyle headerStyle = ExportExcel.createHeadCellStyle(workbook);
            CellStyle contentCellStyle = ExportExcel.createContentCellStyle(workbook);
            String title = "总部部门业绩评价表";
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
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, testProjects.size() + 2));
            //第二
            Row row1 = sheet.createRow(rowNum++);
            Cell cell1 = row1.createCell(0);
            cell1.setCellValue("考核时间：   " + year);
            cell1.setCellStyle(ExportExcel.createSecondHeadCellStyle(workbook));
            //标题合并单元格操作，testProjects.size() + 2为总列数
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, testProjects.size() + 2));
            //第三行表头
            Row row2 = sheet.createRow(rowNum++);
            Cell cell20 = row2.createCell(0);
            cell20.setCellValue("序号");
            cell20.setCellStyle(headerStyle);
            Cell cell21 = row2.createCell(1);
            cell21.setCellValue("部门");
            cell21.setCellStyle(headerStyle);
            for (int i = 2; i < (testProjects.size() + 2); i++) {
                Cell cell = row2.createCell(i);
                cell.setCellValue("考核内容");
                cell.setCellStyle(headerStyle);
            }
            Cell cellLast = row2.createCell(testProjects.size() + 2);
            cellLast.setCellValue("总分");
            cellLast.setCellStyle(headerStyle);
            List<Map<String, Object>> listMap = new ArrayList<>();
            int num = 1;
            //整理表格内容
            for (DeptPerformanceAssessDto deptPerformanceAssessDto : deptPerformanceAssessDtoList) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("序号", num++);
                map.put("部门", deptPerformanceAssessDto.getDept());
                List<TestProject> projects = deptPerformanceAssessDto.getTestProjects();
                Double resultScore = 0D;
                for (TestProject project : projects) {
                    map.put(project.getAssessmentProject(), project.getScore());
                    resultScore += project.getScore();
                }
                resultScore = DoubleUtils.doubleRound(resultScore, 2);
                map.put("总分", resultScore);
                listMap.add(map);
            }
            Map<String, Object> headMap = listMap.get(0);
            Row rowHead = sheet.createRow(rowNum++);
            int cellSize = 0;
            //创建表头
            for (String key : headMap.keySet()) {
                Cell cell = rowHead.createCell(cellSize++);
                cell.setCellValue(key);
                cell.setCellStyle(headerStyle);
            }
            // 合并表头单元格
            sheet.addMergedRegion(new CellRangeAddress(2, 3, 0, 0));
            sheet.addMergedRegion(new CellRangeAddress(2, 3, 1, 1));
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 2, testProjects.size()+1));
            sheet.addMergedRegion(new CellRangeAddress(2, 3, testProjects.size()+2, testProjects.size()+2));
            //创建表格内容
            for (Map<String, Object> map : listMap) {
                Row rowContent = sheet.createRow(rowNum++);
                int cellNum = 0;
                for (String key : map.keySet()) {
                    Cell cell = rowContent.createCell(cellNum++);
                    cell.setCellValue(map.get(key).toString());
                    cell.setCellStyle(contentCellStyle);
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

}
