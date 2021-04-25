package com.authine.cloudpivot.web.api.mapper.QualityAssessment;


import com.authine.cloudpivot.web.api.bean.QualityAssessment.AverageScore;
import com.authine.cloudpivot.web.api.bean.QualityAssessment.InspectionPersonnel;
import com.authine.cloudpivot.web.api.bean.QualityAssessment.QualityAssessment;


import java.util.List;

/**
 * personnel映射器
 * 中铁考核人员维护
 *
 * @Author Ke LongHai
 * @Date 2021/3/26 11:55
 * @Version 1.0
 */
public interface PersonnelMapper {


    /**
     * 获取返回人员
     *
     * @return {@link InspectionPersonnel}
     */
    List<InspectionPersonnel> getPersonnelS();

    /**
     * 获取具体人员
     *
     * @param name 名字
     * @return {@link InspectionPersonnel}
     */
    List<InspectionPersonnel> getPersonnel(String name);

    /**
     * 更新人员逻辑
     *
     * @param name  名字
     * @param logic 逻辑
     */
    void updatePersonnel(String name, int logic);


    /**
     * 得到考核总分
     *
     * @param name 名字
     * @return {@link List<QualityAssessment>}
     */
    List<QualityAssessment> getKaoHe(String name);


    /**
     * 保存平均分数
     *
     * @param averageScore 平均分数
     */
    void saveAverageScore(AverageScore averageScore);

    /**
     * 更新平均分数
     *
     * @param gradedName       名字
     * @param averageScore averageScore
     */
    void updateAverageScore(String gradedName, Double averageScore);

}
