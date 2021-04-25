package com.authine.cloudpivot.web.api.controller.QualityAssessment;

import com.authine.cloudpivot.web.api.bean.QualityAssessment.AverageScore;
import com.authine.cloudpivot.web.api.bean.QualityAssessment.InspectionPersonnel;
import com.authine.cloudpivot.web.api.bean.QualityAssessment.QualityAssessment;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.mapper.QualityAssessment.AverageScoreMapper;
import com.authine.cloudpivot.web.api.mapper.QualityAssessment.PersonnelMapper;
import com.authine.cloudpivot.web.api.service.QualityAssessment.PersonnelService;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

/**
 *
 * 平均分控制器
 * @Author Ke LongHai
 * @Date 2021/3/29 8:49
 * @Version 1.0
 */
@RestController
@Slf4j
@RequestMapping("/avg")
public class AverageScoreController extends BaseController {

    @Resource
    PersonnelService personnelService;

    @Resource
    PersonnelMapper personnelMapper;

    @Resource
    AverageScoreMapper averageScoreMapper;


    /**
     * 保存平均分数
     *
     * @return {@link ResponseResult <Object>}
     */
    @PostMapping("/add")
    public ResponseResult<Object> saveAverageScore() {

        List<InspectionPersonnel> personnelList = personnelMapper.getPersonnelS();
        System.out.println("personnelList = " + personnelList);
        for (InspectionPersonnel personnel : personnelList) {
            String name = personnel.getPeopleName();
            if (averageScoreMapper.selectAverageScore(name) ==null){
                List<QualityAssessment> kaoHe = personnelMapper.getKaoHe(name);
                System.out.println("kaoHe = " + kaoHe);
                int count = 0 ;
                int size = kaoHe.size();
                for (QualityAssessment he : kaoHe) {
                    count = count+he.getGrossScore();
                }
                if (count != 0){
                    double mean  = count/size;
                    AverageScore averageScore = new AverageScore();
                    averageScore.setId(UUID.randomUUID().toString().replace("-", ""));
                    averageScore.setGradedName(name);
                    averageScore.setAverageScore(mean);
                    averageScoreMapper.saveAverageScore(averageScore);
                }
            }
        }
        return  this.getOkResponseResult("ok", "succeed");
    }

    /**
     * 更新平均分数
     */
    @PostMapping("/update")
    public ResponseResult<Object> updateAverageScore(){
        List<InspectionPersonnel> personnelList = personnelService.returnPersonnel();
        for (InspectionPersonnel personnel : personnelList) {
            String name = personnel.getPeopleName();
            List<QualityAssessment> kaoHe = personnelMapper.getKaoHe(name);
            int score = 0;
            int size = kaoHe.size();
            for (QualityAssessment he : kaoHe) {
                score = score+he.getGrossScore();
            }
            if (score != 0){
                double mean  = score/size;
                averageScoreMapper.updateAverageScore(name,mean);
            }
        }
        return  this.getOkResponseResult("ok", "succeed");
    }

}
