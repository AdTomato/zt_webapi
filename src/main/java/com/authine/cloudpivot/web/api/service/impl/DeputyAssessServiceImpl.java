package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.web.api.bean.LeadPerson;
import com.authine.cloudpivot.web.api.bean.deputyassess.Dept;
import com.authine.cloudpivot.web.api.bean.deputyassess.LaunchDeputyAssChild;
import com.authine.cloudpivot.web.api.bean.deputyassess.SubmitDeputyAssChild;
import com.authine.cloudpivot.web.api.mapper.DeputyAssessMapper;
import com.authine.cloudpivot.web.api.service.DeputyAssessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The type Deputy assess service.
 *
 * @Author Asuvera
 * @Date 2020 /7/20 17:04
 * @Version 1.0
 */
@Service
public class DeputyAssessServiceImpl implements DeputyAssessService {
    /**
     * The Deputy assess mapper.
     */
    @Autowired
    DeputyAssessMapper deputyAssessMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertDeptDeputyAsselement(List<LaunchDeputyAssChild> deptDeputyAssessTables) {
        deputyAssessMapper.insertDeptDeputyAsselement(deptDeputyAssessTables);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertOrUpdateDeputyAssesment(String oldParentId, String assessedPersonId, String assessName) {
        List<SubmitDeputyAssChild> list = deputyAssessMapper.selectDetails(oldParentId, assessedPersonId);
//        Iterator<Map.Entry<Integer, List<SubmitDeputyAssChild>>> entries = map.entrySet().iterator();
//        while(entries.hasNext()){
//            Map.Entry<Integer, List<SubmitDeputyAssChild>> entry = entries.next();
//            Integer key = entry.getKey();
//            List<SubmitDeputyAssChild> value = entry.getValue();
//            System.out.println(key+":"+value);
//        }
        //假设10项考核,3种权重
        //indexMap是以专业分组
        Map<String, List<SubmitDeputyAssChild>> indexMap = list.stream().collect(Collectors.groupingBy(SubmitDeputyAssChild::getAssessIndex));
        for (List<SubmitDeputyAssChild> value : indexMap.values()) {
            //上面进来后是第一次循环是第一个专业所有
            //分组,Integer是权重,value是对应权重所有分数明细list
            Map<BigDecimal, List<SubmitDeputyAssChild>> indexWeightMap = value.stream().collect(Collectors.groupingBy(SubmitDeputyAssChild::getWeight));
            //分子
            BigDecimal up = new BigDecimal("0");
            //分母
            BigDecimal down = new BigDecimal("0");
            for (List<SubmitDeputyAssChild> indexWeightList : indexWeightMap.values()) {
                //上面进来后第一循环是第一个专业第一种权重所有
                BigDecimal firstIndexWeightResult = new BigDecimal("0");
                //第一个专业第一种权重求和
                for (SubmitDeputyAssChild submitDeputyAssChild : indexWeightList) {
                      firstIndexWeightResult = firstIndexWeightResult.add(submitDeputyAssChild.getScore());
                }
                //第一个专业第一种权重加权
                BigDecimal divide = firstIndexWeightResult.divide(new BigDecimal(indexWeightList.size()),3,BigDecimal.ROUND_HALF_UP).multiply(indexWeightList.get(0).getWeight()).divide(new BigDecimal("100"),3,BigDecimal.ROUND_HALF_UP);
                //分子求和
                up = up.add(divide);
                down = down.add(indexWeightList.get(0).getWeight());
            }
            BigDecimal divideResult = up.divide(down,3,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"));
            System.out.println(divideResult.toPlainString());
            SubmitDeputyAssChild submitDeputyAssChild = value.get(0);
            submitDeputyAssChild.setScore(divideResult);
            submitDeputyAssChild.setAssessName(assessName);
            Integer haveAssessresult = deputyAssessMapper.isHaveAssessresult(submitDeputyAssChild);
            if (null != haveAssessresult){
                deputyAssessMapper.updateAssessResult(submitDeputyAssChild);
            }else {
                deputyAssessMapper.insertAssessResult(submitDeputyAssChild);
            }

        }
    }





    @Override
    public List<LeadPerson> selectAssessedPeopleFromResult(String id) {
        return deputyAssessMapper.selectAssessedPeopleFromResult(id);
    }


    @Override
    public List<String> selectAnnualFromLaunch() {
        return deputyAssessMapper.selectAnnualFromLaunch();
    }

    @Override
    public List<Dept> selectDeptFromResult() {
        return deputyAssessMapper.selectDeptFromResult();
    }

    @Override
    public List<String> selectHeaders(String deptId,String annual) {
        return deputyAssessMapper.selectHeaders(deptId,annual);

    }
    @Override
    public List<SubmitDeputyAssChild>  selectAssessByDeptIdAndAnnualAndSeasonAndAssessName(String deptId, String annual, String season, String assessName) {
        return deputyAssessMapper.selectAssessByDeptIdAndAnnualAndSeasonAndAssessName(deptId,annual,season,assessName);
    }

    @Override
    public List<SubmitDeputyAssChild> selectAssessByDeptIdAndAnnualAndAssessName(String deptId, String annual, String assessName) {
        return deputyAssessMapper.selectAssessByDeptIdAndAnnualAndAssessName(deptId,annual,assessName);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertDeputyDetails(List<SubmitDeputyAssChild> list) {
        deputyAssessMapper.insertDeputyDetails(list);
    }













    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertSectionAsselement(List<LaunchDeputyAssChild> deptDeputyAssessTables) {
        deputyAssessMapper.insertSectionAsselement(deptDeputyAssessTables);
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertOrUpdateSectionAssesment(String oldParentId, String assessedPersonId, String assessName) {
        List<SubmitDeputyAssChild> list = deputyAssessMapper.selectSectionDetails(oldParentId,assessedPersonId);
        //假设10项考核,3种权重
        //indexMap是以专业分组
        Map<String, List<SubmitDeputyAssChild>> indexMap = list.stream().collect(Collectors.groupingBy(SubmitDeputyAssChild::getAssessIndex));
        for (List<SubmitDeputyAssChild> value : indexMap.values()) {
            //上面进来后是第一次循环是第一个专业所有
            //分组,Integer是权重,value是对应权重所有分数明细list
            Map<BigDecimal, List<SubmitDeputyAssChild>> indexWeightMap = value.stream().collect(Collectors.groupingBy(SubmitDeputyAssChild::getWeight));
            //分子
            BigDecimal up = new BigDecimal("0");
            //分母
            BigDecimal down = new BigDecimal("0");
            for (List<SubmitDeputyAssChild> indexWeightList : indexWeightMap.values()) {
                //上面进来后第一循环是第一个专业第一种权重所有
                BigDecimal firstIndexWeightResult = new BigDecimal("0");
                //第一个专业第一种权重求和
                for (SubmitDeputyAssChild submitDeputyAssChild : indexWeightList) {
                    firstIndexWeightResult = firstIndexWeightResult.add(submitDeputyAssChild.getScore());
                }
                //第一个专业第一种权重加权
                BigDecimal divide = firstIndexWeightResult.divide(new BigDecimal(indexWeightList.size()),3,BigDecimal.ROUND_HALF_UP).multiply(indexWeightList.get(0).getWeight()).divide(new BigDecimal("100"),3,BigDecimal.ROUND_HALF_UP);
                //分子求和
                up = up.add(divide);
                down = down.add(indexWeightList.get(0).getWeight());
            }
            BigDecimal divideResult = up.divide(down,3,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"));
            System.out.println(divideResult.toPlainString());
            SubmitDeputyAssChild submitDeputyAssChild = value.get(0);
            submitDeputyAssChild.setAssessName(assessName);
            submitDeputyAssChild.setScore(divideResult);
            Integer haveAssessresult = deputyAssessMapper.isHaveSectionAssessresult(submitDeputyAssChild);
            if (null != haveAssessresult){
                deputyAssessMapper.updateSectionAssessResult(submitDeputyAssChild);
            }else {
                deputyAssessMapper.insertSectionAssessResult(submitDeputyAssChild);
            }

        }




    }




    @Override
    public List<String> selectSectionHeaders(String deptId, String annual) {
        return deputyAssessMapper.selectSectionHeaders(deptId,annual);
    }

    @Override
    public List<Dept> selectSectionDeptFromResult() {
        return deputyAssessMapper.selectSectionDeptFromResult();
    }

    @Override
    public List<LeadPerson> selectSectionAssessedPeopleFromResult(String id) {
        return deputyAssessMapper.selectSectionAssessedPeopleFromResult(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertSectionDetails(List<SubmitDeputyAssChild> list) {
        deputyAssessMapper.insertSectionDetails(list);
    }

    @Override
    public List<SubmitDeputyAssChild> selectSectionAssessByDeptIdAndAnnualAndSeasonAndAssessName(String deptId, String annual, String season, String assessName) {
        return deputyAssessMapper.selectSectionAssessByDeptIdAndAnnualAndSeasonAndAssessName(deptId,annual,season,assessName);
    }

    @Override
    public List<SubmitDeputyAssChild> selectSectionAssessByDeptIdAndAnnualAndAssessName(String deptId, String annual, String assessName) {
        return deputyAssessMapper.selectSectionAssessByDeptIdAndAnnualAndAssessName(deptId,annual,assessName);
    }


}
