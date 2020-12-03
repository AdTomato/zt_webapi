package com.authine.cloudpivot.web.api.controller;

import com.authine.cloudpivot.engine.api.facade.BizObjectFacade;
import com.authine.cloudpivot.engine.api.model.runtime.BizObjectModel;
import com.authine.cloudpivot.engine.enums.ErrCode;
import com.authine.cloudpivot.web.api.bean.ExpertTermAvgScore;
import com.authine.cloudpivot.web.api.bean.ExpertsInfoResult;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.parameter.ExpertBasicInfo;
import com.authine.cloudpivot.web.api.parameter.ExpertTermGradeInfos;
import com.authine.cloudpivot.web.api.parameter.TermOfficeAssessmentScore;
import com.authine.cloudpivot.web.api.service.ExpertTermAssessmentService;
import com.authine.cloudpivot.web.api.utils.UserUtils;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(description = "专家任期考核", tags = "专家任期考核")
@RestController
@RequestMapping("/ext/expertTerm")
@Slf4j
public class ExpertTermAssessmentController extends BaseController {

	@Autowired
	private ExpertTermAssessmentService expertTermAssessmentService;

	/*// 专家任期评分 同步 专家任期基本数据
	@GetMapping("/get")
	public ResponseResult<List<ExpertTermAssessment>> get(@RequestParam String majorCategories,
			@RequestParam String unit, @RequestParam String annual, @RequestParam String assessmentContent) {
		List<ExpertTermAssessment> list = expertTermAssessmentService.get(majorCategories, unit, annual,
				assessmentContent);
		return ResponseResult.<List<ExpertTermAssessment>>builder().errcode(200L).data(list).build();
	}

	// 专家任期评分回写数据
	@PostMapping("/post")
	public ResponseResult<String> post(@RequestParam String json) {
		expertTermAssessmentService.post(json);
		return ResponseResult.<String>builder().errcode(200L).data("success").build();
	}

	@GetMapping("/calc")
	public ResponseResult<String> calc(@RequestParam String pId) {
		expertTermAssessmentService.calc(pId);
		return ResponseResult.<String>builder().errcode(200L).data("success").build();
	}*/


	/**
	 * //专家任期考核评分 获取专家信息
	 * @param expertBasicInfo 专家任期基本信息
	 * @return
	 */
	@RequestMapping("/expertBaiscInfo")
	public ResponseResult<List> findExpertsBasicInfo(@RequestBody ExpertBasicInfo expertBasicInfo){
		log.info("开始执行查询符合条件的专家方法");
		log.info("当前传入的考核类别为值为：" + expertBasicInfo.getAssessment_type());
		log.info("当前传入的年度值为:" + expertBasicInfo.getAnnual() );
		log.info("当前传入的考核主体为：" + expertBasicInfo.getAssessment_content());
		log.info("当前传入的工作单位为：" + expertBasicInfo.getWork_unit());
		List<ExpertBasicInfo> errorList = new ArrayList<>();
		if (expertBasicInfo.getAssessment_type() !=null && expertBasicInfo.getAnnual() !=null && expertBasicInfo.getAssessment_type() != null && expertBasicInfo.getWork_unit() !=null){
			List<ExpertsInfoResult> expertsInfoResults = expertTermAssessmentService.findExpertsBasicInfo(expertBasicInfo);
			log.info("查询的结果为："+expertsInfoResults);
			return this.getOkResponseResult(expertsInfoResults,"success" );

		}
		return this.getErrResponseResult(errorList, 404L, "参数列表存在空值");
	}



	/**
	 * //生成专家任期考核明细
	 * @param expertTermGradeInfos 专家任期考核评分信息
	 * @return
	 */
	@RequestMapping("/createDetail")
	public ResponseResult<String> createDetail(@RequestBody ExpertTermGradeInfos expertTermGradeInfos){

		log.info("开始执行生成明细的方法");
		log.info("获取的数据为：" + expertTermGradeInfos.toString());
		List<BizObjectModel> models =  new ArrayList<>();
		if (expertTermGradeInfos.getTerm_office_assessment_score() == null && expertTermGradeInfos.getTerm_office_assessment_score().isEmpty()){
			return  this.getOkResponseResult("error","无评分子表数据" );
		}
		for (TermOfficeAssessmentScore termOfficeAssessmentScore : expertTermGradeInfos.getTerm_office_assessment_score()) {
			BizObjectModel model = new BizObjectModel();
			model.setSchemaCode("expert_assessment_detail");
			Map<String,Object> map = new HashMap<>();
			//关联专人任期考核评分表
			map.put("expert_office_assessment_id",expertTermGradeInfos.getId());
			//关联专家任期考核
			map.put("expert_term_assessment_id", termOfficeAssessmentScore.getUser_name());
			//履行职责(满分30分)
			map.put("perform_duties",termOfficeAssessmentScore.getPerform_duties());
			//创新工作(满分20分)
			map.put("innovation_work", termOfficeAssessmentScore.getInnovation_work());
			//建章立制(满分20分)
			map.put("establishment",termOfficeAssessmentScore.getEstablishment() );
			//人才培养(满分20分)
			map.put("personnel_training", termOfficeAssessmentScore.getPersonnel_training());
			//总分
			map.put("total_score",termOfficeAssessmentScore.getTotal_score() );
			//讲数据写入model
			model.put(map);
			model.setSequenceStatus("COMPLETED");
			models.add(model);
		}
		//创建数据的引擎类
		BizObjectFacade bizObjectFacade = super.getBizObjectFacade();
		String userId = UserUtils.getUserId(getUserId());
		log.info("当前操作的用户id为" + userId);
		//使用引擎方法批量创建数据
		bizObjectFacade.addBizObjects(userId,models ,"id" );
		//清空打分项
		log.info("清空评分表考核评分");

		return getOkResponseResult("success", ErrCode.OK.getErrMsg());
	}

	//计算专家评分结果
	@RequestMapping("/calculateAssessmentResult")
	public ResponseResult<String> calculateAssessmentResult(@RequestBody ExpertTermGradeInfos expertTermGradeInfos){
		log.info("开始计算专家评分结果");
		log.info("获取的数据为:" + expertTermGradeInfos.toString());
		log.info("获取数据的评委数量:" + expertTermGradeInfos.getNum());
		log.info("清空子表");
		expertTermAssessmentService.clearAssessmentScore(expertTermGradeInfos.getId());
		// 判断流程状态 expert_assessment_detail
		List<String> peoples = expertTermAssessmentService.getAssessMentDetailNum(expertTermGradeInfos.getId());
		if (peoples.size() != expertTermGradeInfos.getNum()){
			return getErrResponseResult("error", 404L, "流程尚未结束，无需计算");
		}
		// 计算平均分
		log.info("开始执行计算平均分的方法");
		List<ExpertTermAvgScore> expertTermAvgScores = expertTermAssessmentService.countFinanceAvg(expertTermGradeInfos.getId());
		//将平均分更新考核评分表
		expertTermAssessmentService.updateFinalAvg(expertTermAvgScores);
		log.info("当前计算的专家任期考核评分表id为:" + expertTermGradeInfos.getId());
		//获取全部的任期考核明细,计算平均分
		if (expertTermGradeInfos.getAssessment_content().length() != 0 && expertTermGradeInfos.getAssessment_content() != null) {
			if ("所在单位".equals(expertTermGradeInfos.getAssessment_content())) {
				log.info("更新专家任期考核所在单位评分");
				expertTermAssessmentService.updateDeptJudgesScore(expertTermAvgScores);
				return this.getOkResponseResult("成功", "success");
			}
			if ("局评委会".equals(expertTermGradeInfos.getAssessment_content())) {
				log.info("更新专家任期考核局评委会评分");
				expertTermAssessmentService.updateBureauJudgesScore(expertTermAvgScores);
				return this.getOkResponseResult("成功", "success");
			}
			return this.getOkResponseResult("失败", "error");
		}
		return this.getOkResponseResult("失败", "error");

	}
}