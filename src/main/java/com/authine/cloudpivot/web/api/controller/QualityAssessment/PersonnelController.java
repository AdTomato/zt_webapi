package com.authine.cloudpivot.web.api.controller.QualityAssessment;

import com.authine.cloudpivot.web.api.bean.OrgUser;
import com.authine.cloudpivot.web.api.bean.QualityAssessment.InspectionPersonnel;
import com.authine.cloudpivot.web.api.bean.QualityAssessment.RecordChart;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.mapper.QualityAssessment.PersonnelMapper;
import com.authine.cloudpivot.web.api.mapper.QualityAssessment.RecordChartMapper;
import com.authine.cloudpivot.web.api.service.IOrgUserService;
import com.authine.cloudpivot.web.api.service.QualityAssessment.PersonnelService;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

/**
 *
 * 中铁能力素质考核
 * @Author Ke LongHai
 * @Date 2021/3/29 8:49
 * @Version 1.0
 */
@RestController
@Slf4j
@RequestMapping("/check")
public class PersonnelController extends BaseController {

    @Resource
    PersonnelService personnelService;

    @Resource
    PersonnelMapper personnelMapper;

    @Resource
    RecordChartMapper recordChartMapper;

    @Resource
    IOrgUserService iOrgUserService;

    /**
     * 返回被打分人员
     *
     * @return {@link ResponseResult <Object>}
     */
    @PostMapping("/list")
    public synchronized ResponseResult<Object> getPersonnel() {

            List<InspectionPersonnel> personnelList = personnelService.returnPersonnel();
            return this.getOkResponseResult(personnelList, "succeed");

    }

    /**
     * 接收返回人员信息
     *
     * @param phone 电话
     * @return {@link ResponseResult<Object>}
     */
    @PostMapping("/save")
    public synchronized ResponseResult<Object> returnPersonnel(String phone) {

        if (StringUtils.isEmpty(phone)) {
            return getErrResponseResult("未返回被打分人",404L, "参数错误");
        } else {
            OrgUser orgUser = iOrgUserService.getOrgUserByMobile(phone);
            String gradedName = orgUser.getId();
            if(recordChartMapper.getChart("[{"+'"'+"id"+'"'+":"+'"'+gradedName +'"'+","+'"'+"type"+'"'+":3}]")==null){
                RecordChart recordChart = new RecordChart();
                recordChart.setId(UUID.randomUUID().toString().replace("-", ""));
                recordChart.setGradedName ("[{"+'"'+"id"+'"'+":"+'"'+gradedName +'"'+","+'"'+"type"+'"'+":3}]");
                recordChart.setGradedNumber(1);
                recordChartMapper.insertChart(recordChart);
            }else {
                RecordChart recordCharts = recordChartMapper.getChart(gradedName);
                int gradedNumber = recordCharts.getGradedNumber();
                if (gradedNumber<= 100){
                    gradedNumber++;
                    recordChartMapper.updateChart(gradedName,gradedNumber);
                }
                else {
                    int logic = 1;
                    personnelMapper.updatePersonnel(gradedName,logic);
                }
            }
            return this.getOkResponseResult("返回参数成功", "succeed");
        }

    }


}
