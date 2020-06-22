package com.authine.cloudpivot.web.api.controller;

import com.alibaba.fastjson.JSON;
import com.authine.cloudpivot.engine.api.facade.BizObjectFacade;
import com.authine.cloudpivot.engine.api.facade.OrganizationFacade;
import com.authine.cloudpivot.engine.api.facade.WorkflowInstanceFacade;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.engine.api.model.runtime.BizObjectModel;
import com.authine.cloudpivot.web.api.bean.GraduateInfo;
import com.authine.cloudpivot.web.api.bean.GraduateMajor;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.service.GraduateRecruitService;
import com.authine.cloudpivot.web.api.utils.BirthUtils;
import com.authine.cloudpivot.web.api.utils.MailUtils;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;
@Api(tags = "毕业生招聘接口")
@RestController
@RequestMapping("/ext/graduaterecruit")
public class GraduateRecruitController extends BaseController {
    private Logger log = LoggerFactory.getLogger(GraduateRecruitController.class);

    @Autowired
    private GraduateRecruitService graduateRecruitService;
    /**
     * 简历投递,毕业生通过扫描二维码在网上投递简历,简历通过之后,生成评估表数据,开启流程
     * @param graduateInfo
     * @return
     */
    @ApiOperation(value = "简历通过后开启面试评估表流程")
    @RequestMapping(value = "/resumedelivery",method = RequestMethod.POST)
    public ResponseResult<String> findList(@RequestBody @ApiParam(name = "毕业生信息") GraduateInfo graduateInfo) {
        try{
            Date dateOfBirth = graduateInfo.getDateOfBirth();
            BizObjectFacade bizObjectFacade = super.getBizObjectFacade();
            // 创建流程的引擎类
            WorkflowInstanceFacade workflowInstanceFacade = super.getWorkflowInstanceFacade();
            // 有关组织机构的引擎类
            OrganizationFacade organizationFacade = super.getOrganizationFacade();

            // 当前用户id
            String userId = super.getUserId();
            if (userId == null) {
                userId = "2c9280a26706a73a016706a93ccf002b";
            }
            UserModel user = organizationFacade.getUser(userId);
            log.info("当前用户id：" + userId);
            BizObjectModel model = new BizObjectModel();
            model.setSchemaCode("graduateassessment");
            String schoolName = graduateInfo.getSchoolName();
            String facultyAndMajor = graduateInfo.getFacultyAndMajor();
            String schoolAndMajor = schoolName + facultyAndMajor;
            String nationPlace = graduateInfo.getNationPlace();
            String nation = graduateInfo.getNation();
            String nationplaceAndNation = nationPlace+nation;
            Map<String, Object> data = new HashMap<>();
            data.put("userName",graduateInfo.getUserName());
            data.put("gender",graduateInfo.getGender());
            data.put("schoolAndMajor",schoolAndMajor);
            data.put("dateOfBirth",graduateInfo.getDateOfBirth());
            data.put("studentcadres",graduateInfo.getStudentcadres());
            data.put("politicsStatus",graduateInfo.getPoliticsStatus());
            data.put("phone",graduateInfo.getPhone());
            data.put("email",graduateInfo.getEmail());
            data.put("nationplaceAndNation",nationplaceAndNation);
            data.put("nationPlace",nationPlace);
            data.put("school",schoolName);
            data.put("major",facultyAndMajor);
            data.put("partyMember",graduateInfo.getPartyMember());
            data.put("educationBackground",graduateInfo.getEducationBackground());
            data.put("adminSelector",JSON.toJSONString(graduateInfo.getAdminSelector()));
            // 将数据写入到model中
            model.put(data);
            log.info("生成招聘评估表的数据：" + data);
            // 创建毕业生招聘评估表,返回id值
            String objectId = bizObjectFacade.saveBizObject(userId, model, false);
            workflowInstanceFacade.startWorkflowInstance( user.getDepartmentId(), user.getId(), "graduateassessmentfw", objectId, true);
            MailUtils.sendMail(graduateInfo.getEmail(),graduateInfo.getNote(),"中铁四局");
            return this.getOkResponseResult("成功", "success");
        }catch (Exception e){
            return this.getOkResponseResult("失败", "error");
        }

    }

    /**
     * 线上简历投递后,启动简历筛选流程
     * @param objectId
     * @return
     */
    @ApiOperation(value = "线上简历投递后开启筛选流程")
    @RequestMapping(value = "/resumeworkflow",method = RequestMethod.GET)
    public ResponseResult<String> startworkflow(@ApiParam(name = "此条数据id") String objectId) {
        try{
            // 创建流程的引擎类
            WorkflowInstanceFacade workflowInstanceFacade = super.getWorkflowInstanceFacade();
            // 有关组织机构的引擎类
            OrganizationFacade organizationFacade = super.getOrganizationFacade();

            // 当前用户id
            String userId = super.getUserId();
            if (userId == null) {
                userId = "2c9280a26706a73a016706a93ccf002b";
            }
            UserModel user = organizationFacade.getUser(userId);
            workflowInstanceFacade.startWorkflowInstance( user.getDepartmentId(), user.getId(), "OnlineResumeUploadWf", objectId, true);

            return this.getOkResponseResult("成功", "success");
        }catch (Exception e){
            return this.getOkResponseResult("出错", "error");
        }

    }

    /**
     * 面试评估表查出有招聘计划的年度
     * @param userName
     * @return
     */
    @ApiOperation(value = "加载有招聘计划的年度列表")
    @RequestMapping(value = "/yeardropdownboxlist",method = RequestMethod.GET)
    public ResponseResult<List<String>> selectAnnualList(String userName ) {
        try{
            List<String> list = graduateRecruitService.selectYearDropdownBox(userName);
            return this.getOkResponseResult(list, "success");

        }catch (Exception e){
            log.error(e.getMessage());
            List errorList = new ArrayList();
            return this.getOkResponseResult(errorList, "error");

        }
    }

    /**
     * 面试评估表,根据年度取出该年有招聘计划的公司
     * @param year
     * @return
     */
    @ApiOperation(value = "根据年度加载有招聘计划的公司")
    @RequestMapping(value = "/companydropdownboxlist",method = RequestMethod.GET)
    public ResponseResult<List<Map<String,Object>>> selectCompanyList(String year ) {
        try{
            List<Map<String,Object>> list = graduateRecruitService.selectComDropdownBox(year);
            return this.getOkResponseResult(list, "success");

        }catch (Exception e){
            log.error(e.getMessage());
            List errorList = new ArrayList();
            return this.getOkResponseResult(errorList, "error");

        }
    }

    /**
     * 面试评估表,根据年度和公司Id得到有剩余招聘计划的专业
     * @param year
     * @param companyId
     * @return
     */
    @ApiOperation(value = "根据年度和公司Id加载有剩余招聘计划的专业")
    @RequestMapping(value = "/majordropdownboxlist",method = RequestMethod.GET)
    public ResponseResult<List<Map<String,Object>>> selectList(String year,String companyId ) {
        try{
            List<Map<String,Object>> list= graduateRecruitService.selectDropdownBox(year,companyId);
            return this.getOkResponseResult(list, "success");
        }catch (Exception e){
            log.error(e.getMessage());
            List errorList = new ArrayList();
            return this.getOkResponseResult(errorList, "error");
        }

    }





    @ApiOperation(value = "录用后生成统计表数据")
    @RequestMapping(value = "/createstatistics",method = RequestMethod.POST)
    public ResponseResult<String> createStatistics( @RequestBody GraduateInfo graduateInfo ) {
        try{
            String assignmentMajor = graduateInfo.getAssignmentMajor().getId();
            BigDecimal surplusNum = graduateRecruitService.checkremainingNum(assignmentMajor);
            if (surplusNum!=null&&surplusNum.doubleValue() == 0){
                return this.getOkResponseResult("该公司该专业已招满人员,请重新选择", "error");

            }
            Date dateOfBirth = graduateInfo.getDateOfBirth();
            BizObjectFacade bizObjectFacade = super.getBizObjectFacade();
            // 有关组织机构的引擎类
            OrganizationFacade organizationFacade = super.getOrganizationFacade();

            // 当前用户id
            String userId = super.getUserId();
            if (userId == null) {
                userId = "2c9280a26706a73a016706a93ccf002b";
            }
            UserModel user = organizationFacade.getUser(userId);
            log.info("当前用户id：" + userId);
            BizObjectModel model = new BizObjectModel();
            model.setSchemaCode("CampusRecruitmentSummary");
            Map<String, Object> data = new HashMap<>();
            data.put("graduateName",graduateInfo.getUserName());
            int ageByBirth = BirthUtils.getAgeByBirth(dateOfBirth);
            data.put("age",ageByBirth);
            data.put("birth",graduateInfo.getDateOfBirth());
            data.put("studentcadres",graduateInfo.getStudentcadres());
            data.put("contractingUnit",graduateInfo.getContractingUnit());
            data.put("status","正常");
            data.put("nationPlace",graduateInfo.getNationPlace());
            data.put("schoolName",graduateInfo.getSchoolName());
            data.put("major",graduateInfo.getAssignmentMajor().getName());
            data.put("partyMember",graduateInfo.getPartyMember());
            data.put("signatory",graduateInfo.getSignatory());
            data.put("educationBackground",graduateInfo.getEducationBackground());
            data.put("agreementType",graduateInfo.getAgreementType());
            // 将数据写入到model中
            model.put(data);
            String objectId = bizObjectFacade.saveBizObject(userId, model, false);
            double v = surplusNum.doubleValue() - 1;
            BigDecimal bigDecimal = new BigDecimal(v);
            graduateRecruitService.updateremainingNum(bigDecimal,assignmentMajor);
            return this.getOkResponseResult("成功", "success");
        }catch(Exception e){
            return this.getOkResponseResult("数据库出错", "error");

        }

    }


}

